package com.sidpatchy.clairebot.Util.Voting;

import com.sidpatchy.clairebot.Embed.Commands.Regular.VotingEmbed;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.MessageComponents.Regular.VotingComponents;
import com.sidpatchy.clairebot.Util.ChannelUtils;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.MessageComponentInteractionBase;
import org.javacord.api.interaction.ModalInteraction;

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

        Draft(ModalInteraction interaction, String type, String question, String description) {
            userId = interaction.getUser().getId();
            serverId = interaction.getServer().orElseThrow().getId();
            channelId = interaction.getChannel().orElseThrow().getId();
            this.type = type;
            this.question = question;
            this.description = description;
        }
    }

    private VotingFlow() { }

    private static String text(LanguageManager languageManager, String key) {
        return languageManager.getLocalizedString(PREFIX + key);
    }

    public static void start(ModalInteraction interaction, LanguageManager languageManager, String type,
                             String question, String description) {
        if (interaction.getServer().isEmpty() || interaction.getChannel().isEmpty() || question.isBlank()) {
            invalid(interaction, languageManager);
            return;
        }
        drafts.entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue().created > TIMEOUT);
        String id = UUID.randomUUID().toString();
        drafts.put(id, new Draft(interaction, type, question.trim(), description.trim()));
        interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                .setContent(text(languageManager, "ChoicesPrompt"))
                .addComponents(VotingComponents.getChoicesButtons(languageManager, id))
                .respond();
    }

    private static Draft getDraft(InteractionBase interaction, String id, LanguageManager languageManager) {
        Draft draft = drafts.get(id);
        if (draft == null || System.currentTimeMillis() - draft.created > TIMEOUT
                || interaction.getUser().getId() != draft.userId
                || interaction.getServer().map(Server::getId).orElse(0L) != draft.serverId
                || interaction.getChannel().map(TextChannel::getId).orElse(0L) != draft.channelId) {
            if (draft != null && System.currentTimeMillis() - draft.created > TIMEOUT) {
                drafts.remove(id, draft);
            }
            invalid(interaction, languageManager);
            return null;
        }
        return draft;
    }

    private static void invalid(InteractionBase interaction, LanguageManager languageManager) {
        interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                .setContent(text(languageManager, "DraftUnavailable")).respond();
    }

    public static void options(ModalInteraction interaction, LanguageManager languageManager) {
        String id = interaction.getCustomId().substring("vote-options:".length());
        Draft draft = getDraft(interaction, id, languageManager);
        if (draft == null) return;

        synchronized (draft) {
            if (draft.step != Step.FIRST_OPTIONS && draft.step != Step.MORE_OPTIONS) {
                invalid(interaction, languageManager);
                return;
            }
            int start = draft.nextOption;
            for (int i = start; i < Math.min(start + 5, 11); i++) {
                String value = interaction.getTextInputValueByCustomId("option-" + i).orElse("").trim();
                if (!value.isEmpty()) draft.choices.add(value);
            }
            if (draft.choices.size() < 2) {
                drafts.remove(id, draft);
                interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                        .setContent(text(languageManager, "TooFewChoices")).respond();
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
            interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                    .setContent(text(languageManager, "ReadyToPublish"))
                    .addComponents(buttons).respond();
        }
    }

    public static void button(ButtonInteraction interaction, LanguageManager languageManager) {
        String customId = interaction.getCustomId();
        String id = customId.substring(customId.indexOf(':') + 1);
        Draft draft = getDraft(interaction, id, languageManager);
        if (draft == null) return;

        synchronized (draft) {
            if (customId.startsWith("vote-cancel:") && (draft.step == Step.CHOICES
                    || draft.step == Step.MULTIPLE || draft.step == Step.READY)) {
                drafts.remove(id, draft);
                interaction.createOriginalMessageUpdater().setContent(text(languageManager, "Cancelled"))
                        .removeAllComponents().update();
            } else if (draft.step == Step.CHOICES && customId.startsWith("vote-choices-")) {
                if (customId.startsWith("vote-choices-no:")) {
                    publish(interaction, languageManager, id, draft);
                } else if (customId.startsWith("vote-choices-yes:")) {
                    draft.step = Step.MULTIPLE;
                    interaction.createOriginalMessageUpdater()
                            .setContent(text(languageManager, "AllowMultipleChoicesPrompt"))
                            .removeAllComponents()
                            .addComponents(VotingComponents.getAllowMultipleChoicesButtons(languageManager, id))
                            .update();
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
                ActionRow[] rows = new ActionRow[5];
                for (int i = 0; i < rows.length; i++) {
                    rows[i] = VotingComponents.getOptionActionRow(i + 1, languageManager, i < 2);
                }
                interaction.respondWithModal("vote-options:" + id, text(languageManager, "ChoicesTitle"), rows);
            } else if (draft.step != Step.READY) {
                invalid(interaction, languageManager);
            } else if (customId.startsWith("vote-more:") && draft.choices.size() < 10) {
                draft.step = Step.MORE_OPTIONS;
                draft.nextOption = draft.choices.size() + 1;
                ActionRow[] rows = new ActionRow[Math.min(5, 11 - draft.nextOption)];
                for (int i = 0; i < rows.length; i++) {
                    rows[i] = VotingComponents.getOptionActionRow(i + draft.nextOption, languageManager, false);
                }
                interaction.respondWithModal("vote-options:" + id, text(languageManager, "ChoicesTitle"), rows);
            } else if (customId.startsWith("vote-publish:")) {
                publish(interaction, languageManager, id, draft);
            } else {
                invalid(interaction, languageManager);
            }
        }
    }

    private static void publish(MessageComponentInteractionBase interaction, LanguageManager languageManager, String id, Draft draft) {
        Server server = interaction.getServer().orElseThrow();
        TextChannel target = interaction.getChannel().orElseThrow();
        if (draft.type.equals("REQUEST")) {
            ServerTextChannel requestsChannel = ChannelUtils.getRequestsChannel(server);
            if (requestsChannel == null) {
                interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ErrorEmbed.getCustomError(languageManager, Main.getErrorCode("requestsChannelMissing"),
                                "A requests channel is not configured for this server. An admin can set one in /config server > Requests Channel."))
                        .respond();
                return;
            }
            target = requestsChannel;
        }
        if (!drafts.remove(id, draft)) {
            invalid(interaction, languageManager);
            return;
        }

        TextChannel destination = target;
        interaction.createOriginalMessageUpdater().removeAllComponents().update()
                .thenRun(() -> destination.sendMessage(VotingEmbed.getPoll(languageManager,
                        draft.type, draft.question, draft.description, draft.allowMultiple, draft.choices,
                        server, interaction.getUser(), draft.choices.size())).whenComplete((message, error) -> {
                    if (error != null) {
                        Main.getLogger().error("Could not publish poll/request", error);
                        interaction.createFollowupMessageBuilder().setFlags(MessageFlag.EPHEMERAL)
                                .setContent(text(languageManager, "PublishFailed")).send();
                    } else {
                        addReactions(message, draft.choices.size());
                        if (draft.type.equals("REQUEST")) {
                            interaction.createFollowupMessageBuilder().setFlags(MessageFlag.EPHEMERAL)
                                    .addEmbed(VotingEmbed.getUserResponse(languageManager, interaction.getUser(),
                                            destination.asServerTextChannel().orElseThrow().getMentionTag())).send();
                        } else {
                            interaction.createFollowupMessageBuilder().setFlags(MessageFlag.EPHEMERAL)
                                    .setContent(text(languageManager, "PollPublished")).send();
                        }
                    }
                }));
    }

    public static void addReactions(Message message, int choiceCount) {
        if (choiceCount == 0) {
            message.addReaction("👍");
            message.addReaction("👎");
            message.addReaction(":vote:706373563564949566");
        } else {
            for (int i = 0; i < Math.min(choiceCount, CHOICE_EMOJIS.length); i++) {
                message.addReaction(CHOICE_EMOJIS[i]);
            }
        }
    }
}
