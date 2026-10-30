package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.SantaEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.SantaUtils;
import com.sidpatchy.clairebot.Util.Voting.VotingFlow;
import net.dv8tion.jda.api.JDA;
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

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        ModalInteractionEvent modalInteraction = event;

        Guild server = modalInteraction.getGuild();
        MessageChannel textchannel = modalInteraction.getChannel();
        User user = modalInteraction.getUser();
        String modalID = modalInteraction.getModalId();

        Main.getLogger().debug(modalID);

        ContextManager context = new ContextManager(server, textchannel, user, user, null, new HashMap<>());
        LanguageManager languageManager = new LanguageManager(Main.getFallbackLocale(), context);

        if (modalID.startsWith("santa-rules-") || modalID.startsWith("santa-theme-")) {
            String santaMessageID = modalID.substring(modalID.lastIndexOf('-') + 1);
            updateSantaHostMessage(modalInteraction, languageManager, textchannel, santaMessageID);
            return;
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
        }
    }

    private static void updateSantaHostMessage(ModalInteractionEvent interaction, LanguageManager languageManager,
                                               MessageChannel channel, String santaMessageID) {
        interaction.deferEdit().queue(hook -> {
            try {
                Message santaMessage = interaction.getMessage();
                if (santaMessage == null) {
                    santaMessage = channel.retrieveMessageById(santaMessageID).complete();
                }

                MessageEmbed embed = santaMessage.getEmbeds().getFirst();
                SantaUtils.ExtractionResult result = SantaUtils.extractDataFromEmbed(embed, embed.getFooter());
                Role role = getSantaRole(interaction.getJDA(), result.santaID);
                if (role == null) {
                    hook.sendMessage("Could not update Secret Santa: the server or role is no longer available.")
                            .setEphemeral(true).queue();
                    return;
                }

                result.rules = interaction.getValue("rules-row") == null ? result.rules : interaction.getValue("rules-row").getAsString();
                result.theme = interaction.getValue("theme-row") == null ? result.theme : interaction.getValue("theme-row").getAsString();

                // Rebuild the host embed with the existing giver/receiver pairs and edit the original message
                santaMessage.editMessageEmbeds(SantaEmbed.buildHostEmbedFromPairs(languageManager, role, interaction.getUser(),
                        result.rules, result.theme, result.givers, result.receivers).build()).queue(null, error -> {
                    Main.getLogger().error("Could not update Secret Santa host message", error);
                    hook.sendMessage("Could not update Secret Santa. Please try again.").setEphemeral(true).queue();
                });
            } catch (RuntimeException error) {
                Main.getLogger().error("Could not update Secret Santa host message", error);
                hook.sendMessage("Could not update Secret Santa. Please try again.").setEphemeral(true).queue();
            }
        });
    }

    static Role getSantaRole(JDA api, HashMap<String, String> santaID) {
        Guild server = api.getGuildById(santaID.get("serverID"));
        return server == null ? null : server.getRoleById(santaID.get("roleID"));
    }
}
