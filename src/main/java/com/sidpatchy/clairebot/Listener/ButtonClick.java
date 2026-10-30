package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.QuoteEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Regular.SantaEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.MessageComponents.Regular.SantaModal;
import com.sidpatchy.clairebot.Util.SantaUtils;
import com.sidpatchy.clairebot.Util.Voting.VotingFlow;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;

import java.util.HashMap;

public class ButtonClick extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        ButtonInteractionEvent buttonInteraction = event;

        Guild server = buttonInteraction.getGuild();
        String buttonID = buttonInteraction.getComponentId().toLowerCase();
        User buttonAuthor = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();
        MessageChannel channel = message.getChannel();

        ContextManager context = new ContextManager(server, channel, buttonAuthor, buttonAuthor, message, new HashMap<>());
        LanguageManager languageManager = new LanguageManager(Main.getFallbackLocale(), context);

        if (buttonID.startsWith("vote-choices-") || buttonID.startsWith("vote-multi-")
                || buttonID.startsWith("vote-more:") || buttonID.startsWith("vote-publish:")
                || buttonID.startsWith("vote-cancel:")) {
            VotingFlow.button(buttonInteraction, languageManager);
            return;
        }

        MessageEmbed embed = buttonInteraction.getMessage().getEmbeds().get(0);
        MessageEmbed.Footer footer = embed.getFooter();

        // Extract data from embed fields
        SantaUtils.ExtractionResult extractionResult = null;
        User author = null;
        if (!buttonID.equalsIgnoreCase("view_original")) {
            extractionResult = SantaUtils.extractDataFromEmbed(embed, footer);
            server = Main.getApi().getGuildById(extractionResult.santaID.get("serverID"));
            author = Main.getApi().retrieveUserById(extractionResult.santaID.get("authorID")).complete();
        }
        final Guild santaServer = server;
        final User santaAuthor = author;
        final SantaUtils.ExtractionResult santa = extractionResult;

        switch (buttonID) {
            case "rules":
                buttonInteraction.replyModal(Modal.create("santa-rules-" + message.getId(), "Update Rules")
                        .addComponents(SantaModal.getRulesRow(languageManager)).build()).queue();

                break;
            case "theme":
                buttonInteraction.replyModal(Modal.create("santa-theme-" + message.getId(), "Update Theme")
                        .addComponents(SantaModal.getThemeRow(languageManager)).build()).queue();

                break;
            case "send":
                buttonInteraction.deferEdit().queue();
                for (int i = 0; i < extractionResult.givers.size(); i++) {
                    User giver = extractionResult.givers.get(i);
                    User receiver = extractionResult.receivers.get(i);
                    giver.openPrivateChannel().flatMap(dm -> dm.sendMessage(SantaEmbed.getSantaMessage(languageManager, santaServer, santaAuthor, giver, receiver, santa.rules, santa.theme).build())).queue();
                }

                break;
            case "test":
                buttonInteraction.deferEdit().queue();
                buttonAuthor.openPrivateChannel().flatMap(dm -> dm.sendMessage(SantaEmbed.getSantaMessage(languageManager, santaServer, santaAuthor, santa.givers.get(0), santa.receivers.get(0), santa.rules, santa.theme).build())).queue();

                break;
            case "randomize":
                buttonInteraction.deferEdit().queue();
                Role role = server.getRoleById(extractionResult.santaID.get("roleID"));
                // Edit the existing host message in place with a fresh randomized pairing
                buttonInteraction.getMessage().editMessageEmbeds(
                        SantaEmbed.buildHostEmbedRandomized(languageManager, role, author, extractionResult.rules, extractionResult.theme).build()
                ).queue();

                break;

            // Quote command
            case "view_original":
                //buttonInteraction.acknowledge();
                buttonInteraction.replyEmbeds(QuoteEmbed.viewOriginalMessageBuilder(channel, message).build())
                        .setEphemeral(true).queue();

                break;
        }
    }
}
