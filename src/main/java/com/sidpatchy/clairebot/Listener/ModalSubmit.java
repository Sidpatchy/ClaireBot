package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Regular.VotingEmbed;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.ChannelUtils;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageFlag;
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

        String voteType = "";
        if (modalID.equalsIgnoreCase("poll")) {
            voteType = "POLL";
            modalID = "request";
        }
        else if (modalID.equalsIgnoreCase("request")) {
            voteType = "REQUEST";
        }

        switch (modalID) {
            case "hex-entry-modal":
                String hexColour = modalInteraction.getTextInputValueByCustomId("hex-entry-field").orElse("");

                if (!hexColour.equals("")) {
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
        }
    }
}
