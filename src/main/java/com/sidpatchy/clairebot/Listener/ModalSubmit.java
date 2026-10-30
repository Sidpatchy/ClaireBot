package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.SantaEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.SantaUtils;
import com.sidpatchy.clairebot.Util.Voting.VotingFlow;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class ModalSubmit extends ListenerAdapter {

    private LanguageManager languageManager;

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        ModalInteractionEvent modalInteraction = event;

        Guild server = modalInteraction.getGuild();
        MessageChannel textchannel = modalInteraction.getChannel();
        User user = modalInteraction.getUser();
        String modalID = modalInteraction.getModalId();

        Main.getLogger().debug(modalID);

        ContextManager context = new ContextManager(server, textchannel, user, user, null, new HashMap<>());
        languageManager = new LanguageManager(Main.getFallbackLocale(), context);

        // Santa related vars
        String santaMessageID = ""; // Allows the Secret Santa function to identify which message is being referenced.
        SantaUtils.ExtractionResult extractionResult = null;
        Message santaMessage = null;
        if (modalID.startsWith("santa")) {
            if (modalID.startsWith("santa-rules")) {
                santaMessageID = modalID.replace("santa-rules-", "");
                modalID = "santa-rules";
            }
            else if (modalID.startsWith("santa-theme")) {
                santaMessageID = modalID.replace("santa-theme-", "");
                modalID = "santa-theme";
            }

            santaMessage = modalInteraction.getMessage();
            if (santaMessage == null && !santaMessageID.isEmpty()) {
                santaMessage = textchannel.retrieveMessageById(santaMessageID).complete();
            }
            assert santaMessage != null;

            MessageEmbed embed = santaMessage.getEmbeds().getFirst();
            MessageEmbed.Footer footer = embed.getFooter();

            extractionResult = SantaUtils.extractDataFromEmbed(embed, footer);
        }


        
        switch (modalID) {
            case "hex-entry-modal":
                String hexColour = modalInteraction.getValue("hex-entry-field") == null ? "" : modalInteraction.getValue("hex-entry-field").getAsString();

                if (!hexColour.isEmpty()) {
                    // Add "#" if not present
                    if (hexColour.length() == 6 || !hexColour.contains("#")) {
                        hexColour = "#" + hexColour;
                    }

                    Main.getLogger().debug(hexColour);

                    modalInteraction.replyEmbeds(UserPreferencesEmbed.getAcknowledgeAccentColourChange(languageManager, user, hexColour).build())
                            .setEphemeral(true).queue();
                }
                break;
            case "poll", "request":
                String question = modalInteraction.getValue("question-modal") == null ? "" : modalInteraction.getValue("question-modal").getAsString();
                String description = modalInteraction.getValue("details-modal") == null ? "" : modalInteraction.getValue("details-modal").getAsString();
                VotingFlow.start(modalInteraction, languageManager, modalID.toUpperCase(), question, description);
                break;
            default:
                if (modalID.startsWith("vote-options:")) {
                    VotingFlow.options(modalInteraction, languageManager);
                }
                break;
            case "santa-rules", "santa-theme":
                // Silently defer the modal response (no visible message), then update the existing host message in place
                modalInteraction.deferReply(true).queue(hook -> hook.editOriginal("\u200B")
                        .queue(message -> hook.deleteOriginal().queue()));

                assert extractionResult != null;
                extractionResult.rules = modalInteraction.getValue("rules-row") == null ? extractionResult.rules : modalInteraction.getValue("rules-row").getAsString();
                extractionResult.theme = modalInteraction.getValue("theme-row") == null ? extractionResult.theme : modalInteraction.getValue("theme-row").getAsString();

                Role role = server.getRoleById(extractionResult.santaID.get("roleID"));
                assert role != null;

                // Rebuild the host embed with the existing giver/receiver pairs and edit the original message
                santaMessage.editMessageEmbeds(SantaEmbed.buildHostEmbedFromPairs(languageManager, role, user, extractionResult.rules, extractionResult.theme, extractionResult.givers, extractionResult.receivers).build()).queue();
                break;
        }
    }
}
