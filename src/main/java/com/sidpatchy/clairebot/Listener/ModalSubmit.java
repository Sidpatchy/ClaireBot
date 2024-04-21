package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.SantaEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Regular.VotingEmbed;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.ChannelUtils;
import com.sidpatchy.clairebot.Util.SantaUtils;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.interaction.ModalInteraction;
import org.javacord.api.listener.interaction.ModalSubmitListener;

public class ModalSubmit implements ModalSubmitListener {

    @Override
    public void onModalSubmit(ModalSubmitEvent event) {
        ModalInteraction modalInteraction = event.getModalInteraction();

        User user = modalInteraction.getUser();
        String modalID = modalInteraction.getCustomId();

        Main.getLogger().debug(modalID);

        String voteType = "";       // Allows the Poll/Request feature to distinguish between the two

        // Santa related vars
        String santaMessageID = ""; // Allows the Secret Santa function to identify which message is being referenced.
        SantaUtils.ExtractionResult extractionResult = null;
        Message santaMessage = null;
        if (modalID.equalsIgnoreCase("poll")) {
            voteType = "POLL";
            modalID = "request";
        }
        else if (modalID.equalsIgnoreCase("request")) {
            voteType = "REQUEST";
        }

        else if (modalID.startsWith("santa")) {
            if (modalID.startsWith("santa-rules")) {
                santaMessageID = modalID.replace("santa-rules-", "");
                modalID = "santa-rules";
            }
            else if (modalID.startsWith("santa-theme")) {
                santaMessageID = modalID.replace("santa-theme-", "");
                modalID = "santa-theme";
            }

            santaMessage = Main.getApi().getCachedMessageById(santaMessageID).orElse(null);
            assert santaMessage != null;

            Embed embed = santaMessage.getEmbeds().get(0);
            EmbedFooter footer = embed.getFooter().orElse(null);

            extractionResult = SantaUtils.extractDataFromEmbed(embed, footer);
        }


        
        switch (modalID) {
            case "hex-entry-modal":
                String hexColour = modalInteraction.getTextInputValueByCustomId("hex-entry-field").orElse("");

                if (!hexColour.isEmpty()) {
                    // Add "#" if not present
                    if (hexColour.length() == 6 || !hexColour.contains("#")) {
                        hexColour = "#" + hexColour;
                    }

                    Main.getLogger().debug(hexColour);

                    modalInteraction.createImmediateResponder()
                        .addEmbed(UserPreferencesEmbed.getAcknowledgeAccentColourChange(user, hexColour))
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
                }
                break;
            case "request":
                String question = modalInteraction.getTextInputValueByCustomId("question-modal").orElse("");
                String description = modalInteraction.getTextInputValueByCustomId("details-modal").orElse("");
                Server server = modalInteraction.getServer().orElse(null);
                User author = modalInteraction.getUser();

                if (server == null) {
                    return;
                }

                if (voteType.equalsIgnoreCase("request")) {
                    ServerTextChannel requestsChannel = ChannelUtils.getRequestsChannel(server);
                    modalInteraction.createImmediateResponder()
                            .addEmbed(VotingEmbed.getUserResponse(author, requestsChannel.getMentionTag()))
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();

                    requestsChannel.sendMessage(VotingEmbed.getPoll(voteType, question, description, false, null, server, author, 0));
                }
                else if (voteType.equalsIgnoreCase("poll")) {
                    modalInteraction.createImmediateResponder()
                            .addEmbed(VotingEmbed.getPoll(voteType, question, description, false, null, server, author, 0))
                            .respond();
                }

                break;
            case "santa-rules", "santa-theme":
                modalInteraction.createImmediateResponder().respond();
                extractionResult.rules = modalInteraction.getTextInputValueByCustomId("rules-row").orElse(extractionResult.rules);
                extractionResult.theme = modalInteraction.getTextInputValueByCustomId("theme-row").orElse(extractionResult.theme);

                Role role = Main.getApi().getRoleById(extractionResult.santaID.get("roleID")).orElse(null);
                assert role != null;

                SantaEmbed.getHostMessage(role, user, extractionResult.rules, extractionResult.theme).send(user);
                santaMessage.delete();
        }
    }
}
