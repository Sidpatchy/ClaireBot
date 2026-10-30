package com.sidpatchy.clairebot.Util.Voting;

import com.sidpatchy.clairebot.Embed.Commands.Regular.VotingEmbed;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.MessageComponents.Regular.VotingComponents;
import com.sidpatchy.clairebot.Util.ChannelUtils;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.modals.Modal;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Guides modal-created polls through choices and publishes them using the same embed as slash-command polls. */
public final class VotingFlow {
    private static final Map<String, Draft> drafts = new ConcurrentHashMap<>();
    private static final long TIMEOUT = Duration.ofMinutes(15).toMillis();
    private static final String PREFIX = "ClaireLang.Embed.Commands.Components.Regular.Voting.";
    private static final String[] CHOICE_EMOJIS = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟"};

    private enum Step { CHOICES, MULTIPLE, FIRST_OPTIONS, MORE_OPTIONS, READY }

    private static final class Draft {
        final long userId;
        final long serverId;
        final long channelId;
        final String type;
        final String question;
        final String description;
        final long created = System.currentTimeMillis();
        final List<String> choices = new ArrayList<>();
        Step step = Step.CHOICES;
        boolean allowMultiple;
        int nextOption = 1;

        Draft(ModalInteractionEvent interaction, String type, String question, String description) {
            userId = interaction.getUser().getIdLong();
            serverId = interaction.getGuild().getIdLong();
            channelId = interaction.getChannelIdLong();
            this.type = type;
            this.question = question;
            this.description = description;
        }
    }

    private VotingFlow() { }

    private static String text(LanguageManager languageManager, String key) {
        return languageManager.getLocalizedString(PREFIX + key);
    }

    public static void start(ModalInteractionEvent interaction, LanguageManager languageManager, String type,
                             String question, String description) {
        if (interaction.getGuild() == null || interaction.getMessageChannel() == null || question.isBlank()) {
            invalid(interaction, languageManager);
            return;
        }
        drafts.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().created > TIMEOUT);
        String id = UUID.randomUUID().toString();
        drafts.put(id, new Draft(interaction, type, question.trim(), description.trim()));
        interaction.reply(text(languageManager, "ChoicesPrompt")).setEphemeral(true)
                .addComponents(VotingComponents.getChoicesButtons(languageManager, id)).queue();
    }

    private static Draft getDraft(IReplyCallback interaction, String id, LanguageManager languageManager) {
        Draft draft = drafts.get(id);
        if (draft == null || System.currentTimeMillis() - draft.created > TIMEOUT
                || interaction.getUser().getIdLong() != draft.userId
                || (interaction.getGuild() == null ? 0L : interaction.getGuild().getIdLong()) != draft.serverId
                || interaction.getChannelIdLong() != draft.channelId) {
            if (draft != null && System.currentTimeMillis() - draft.created > TIMEOUT) {
                drafts.remove(id, draft);
            }
            invalid(interaction, languageManager);
            return null;
        }
        return draft;
    }

    private static void invalid(IReplyCallback interaction, LanguageManager languageManager) {
        interaction.reply(text(languageManager, "DraftUnavailable")).setEphemeral(true).queue();
    }

    public static void options(ModalInteractionEvent interaction, LanguageManager languageManager) {
        String id = interaction.getModalId().substring("vote-options:".length());
        Draft draft = getDraft(interaction, id, languageManager);
        if (draft == null) return;

        synchronized (draft) {
            if (draft.step != Step.FIRST_OPTIONS && draft.step != Step.MORE_OPTIONS) {
                invalid(interaction, languageManager);
                return;
            }
            int start = draft.nextOption;
            for (int i = start; i < Math.min(start + 5, 11); i++) {
                String value = interaction.getValue("option-" + i) == null ? "" : interaction.getValue("option-" + i).getAsString().trim();
                if (!value.isEmpty()) draft.choices.add(value);
            }
            if (draft.choices.size() < 2) {
                drafts.remove(id, draft);
                interaction.reply(text(languageManager, "TooFewChoices")).setEphemeral(true).queue();
                return;
            }
            boolean canAddMore = draft.choices.size() < 10;
            draft.step = Step.READY;
            ActionRow buttons = canAddMore
                    ? ActionRow.of(Button.primary("vote-more:" + id, text(languageManager, "AddMoreChoices")),
                            Button.success("vote-publish:" + id, text(languageManager, "Publish")),
                            Button.danger("vote-cancel:" + id, text(languageManager, "Cancel")))
                    : ActionRow.of(Button.success("vote-publish:" + id, text(languageManager, "Publish")),
                            Button.danger("vote-cancel:" + id, text(languageManager, "Cancel")));
            interaction.reply(text(languageManager, "ReadyToPublish")).setEphemeral(true)
                    .addComponents(buttons).queue();
        }
    }

    public static void button(ButtonInteractionEvent interaction, LanguageManager languageManager) {
        String customId = interaction.getComponentId();
        String id = customId.substring(customId.indexOf(':') + 1);
        Draft draft = getDraft(interaction, id, languageManager);
        if (draft == null) return;

        synchronized (draft) {
            if (customId.startsWith("vote-cancel:") && (draft.step == Step.CHOICES
                    || draft.step == Step.MULTIPLE || draft.step == Step.READY)) {
                drafts.remove(id, draft);
                interaction.editMessage(text(languageManager, "Cancelled"))
                        .setComponents().queue();
            } else if (draft.step == Step.CHOICES && customId.startsWith("vote-choices-")) {
                if (customId.startsWith("vote-choices-no:")) {
                    publish(interaction, languageManager, id, draft);
                } else if (customId.startsWith("vote-choices-yes:")) {
                    draft.step = Step.MULTIPLE;
                    interaction.editMessage(text(languageManager, "AllowMultipleChoicesPrompt"))
                            .setComponents(VotingComponents.getAllowMultipleChoicesButtons(languageManager, id)).queue();
                } else {
                    invalid(interaction, languageManager);
                }
            } else if (draft.step == Step.MULTIPLE && customId.startsWith("vote-multi-")) {
                if (!customId.startsWith("vote-multi-yes:") && !customId.startsWith("vote-multi-no:")) {
                    invalid(interaction, languageManager);
                    return;
                }
                draft.allowMultiple = customId.startsWith("vote-multi-yes:");
                draft.step = Step.FIRST_OPTIONS;
                Label[] rows = new Label[5];
                for (int i = 0; i < rows.length; i++) {
                    rows[i] = VotingComponents.getOptionActionRow(i + 1, languageManager, i < 2);
                }
                interaction.replyModal(Modal.create("vote-options:" + id, text(languageManager, "ChoicesTitle")).addComponents(rows).build()).queue();
            } else if (draft.step != Step.READY) {
                invalid(interaction, languageManager);
            } else if (customId.startsWith("vote-more:") && draft.choices.size() < 10) {
                draft.step = Step.MORE_OPTIONS;
                draft.nextOption = draft.choices.size() + 1;
                Label[] rows = new Label[Math.min(5, 11 - draft.nextOption)];
                for (int i = 0; i < rows.length; i++) {
                    rows[i] = VotingComponents.getOptionActionRow(i + draft.nextOption, languageManager, false);
                }
                interaction.replyModal(Modal.create("vote-options:" + id, text(languageManager, "ChoicesTitle")).addComponents(rows).build()).queue();
            } else if (customId.startsWith("vote-publish:")) {
                publish(interaction, languageManager, id, draft);
            } else {
                invalid(interaction, languageManager);
            }
        }
    }

    private static void publish(ButtonInteractionEvent interaction, LanguageManager languageManager, String id, Draft draft) {
        Guild server = interaction.getGuild();
        MessageChannel target = interaction.getChannel();
        if (draft.type.equals("REQUEST")) {
            TextChannel requestsChannel = ChannelUtils.getRequestsChannel(server);
            if (requestsChannel == null) {
                interaction.replyEmbeds(ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("requestsChannelMissing"),
                        "A requests channel is not configured for this server. An admin can set one in /config server > Requests Channel.").build())
                        .setEphemeral(true).queue();
                return;
            }
            target = requestsChannel;
        }
        if (!drafts.remove(id, draft)) {
            invalid(interaction, languageManager);
            return;
        }

        MessageChannel destination = target;
        interaction.editComponents().queue(hook -> destination.sendMessageEmbeds(VotingEmbed.getPoll(languageManager,
                        draft.type, draft.question, draft.description, draft.allowMultiple, draft.choices,
                        server, interaction.getUser(), draft.choices.size()).build()).queue(message -> {
                        addReactions(message, draft.choices.size());
                        if (draft.type.equals("REQUEST")) {
                            hook.sendMessageEmbeds(VotingEmbed.getUserResponse(languageManager, interaction.getUser(),
                                    ((TextChannel) destination).getAsMention()).build()).setEphemeral(true).queue();
                        } else {
                            hook.sendMessage(text(languageManager, "PollPublished")).setEphemeral(true).queue();
                        }
                    }, error -> {
                        Main.getLogger().error("Could not publish poll/request", error);
                        hook.sendMessage(text(languageManager, "PublishFailed")).setEphemeral(true).queue();
                    }));
    }

    public static void addReactions(Message message, int choiceCount) {
        if (choiceCount == 0) {
            message.addReaction(Emoji.fromUnicode("👍")).queue();
            message.addReaction(Emoji.fromUnicode("👎")).queue();
            message.addReaction(Emoji.fromCustom("vote", 706373563564949566L, false)).queue();
        } else {
            for (int i = 0; i < Math.min(choiceCount, CHOICE_EMOJIS.length); i++) {
                message.addReaction(Emoji.fromUnicode(CHOICE_EMOJIS[i])).queue();
            }
        }
    }
}
