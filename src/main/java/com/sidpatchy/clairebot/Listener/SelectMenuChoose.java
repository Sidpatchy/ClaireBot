package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.ServerPreferencesEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.MessageComponents.Regular.ServerPreferencesComponents;
import com.sidpatchy.clairebot.MessageComponents.Regular.UserPreferencesComponents;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SelectMenuChooseEvent;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.listener.interaction.SelectMenuChooseListener;

public class SelectMenuChoose implements SelectMenuChooseListener {
    Logger logger = Main.getLogger();

    @Override
    public void onSelectMenuChoose(SelectMenuChooseEvent event) {
        SelectMenuInteraction selectMenuInteraction = event.getSelectMenuInteraction();

        Message message = selectMenuInteraction.getMessage();
        User user = selectMenuInteraction.getUser();
        Server server = selectMenuInteraction.getServer().orElse(null);
        TextChannel channel = selectMenuInteraction.getChannel().orElse(null);

        // Not speaking of message author, rather, the header field
        EmbedAuthor embedAuthor = message.getEmbeds().get(0).getAuthor().orElse(null);
        if (embedAuthor != null && channel != null) {
            String menuName = embedAuthor.getName();
            String label = selectMenuInteraction.getChosenOptions().get(0).getLabel();
            String id = selectMenuInteraction.getChosenOptions().get(0).getValue();

            // User preferences menu
            if (menuName.equalsIgnoreCase("User Preferences Editor")) {
                selectMenuInteraction.acknowledge();

                if (label.equalsIgnoreCase("Accent Colour")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(UserPreferencesEmbed.getAccentColourMenu(user))
                            .addComponents(UserPreferencesComponents.getAccentColourMenu())
                            .send();
                }
                else if (label.equalsIgnoreCase("Language")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(UserPreferencesEmbed.getLanguageMenu(user))
                            .addComponents()
                            .send();
                }
            }
            else if (menuName.equalsIgnoreCase("Accent Colour Editor")) {
                if (label.equalsIgnoreCase("Select Common Colours")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(UserPreferencesEmbed.getAccentColourListMenu(user))
                            .addComponents(UserPreferencesComponents.getAccentColourList())
                            .send();
                }
                else if (label.equalsIgnoreCase("Hexadecimal Entry")) {
                    message.delete();
                    selectMenuInteraction.respondWithModal("hex-entry-modal", "Hex Colour Entry", UserPreferencesComponents.getAccentColourHexEntry());
                }
                selectMenuInteraction.acknowledge();
            }
            else if (menuName.equalsIgnoreCase("Accent Colour List")) {
                selectMenuInteraction.acknowledge();

                String accentColour = selectMenuInteraction.getChosenOptions().get(0).getDescription().orElse("");

                if (accentColour.isEmpty()) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ErrorEmbed.getError(Main.getErrorCode("accentColourParse")))
                            .send();
                }
                else {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(UserPreferencesEmbed.getAcknowledgeAccentColourChange(user, accentColour))
                            .addComponents()
                            .send();
                }
            }

            // Server configuration
            else if (menuName.equalsIgnoreCase("Server Configuration Editor")) {
                selectMenuInteraction.acknowledge();

                if (server == null) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getNotServerMenu())
                            .addComponents()
                            .send();
                }
                else if (label.equalsIgnoreCase("Requests Channel")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getRequestsChannelMenu(user))
                            .addComponents(ServerPreferencesComponents.getRequestsChannelMenu(server))
                            .send();
                }
                else if (label.equalsIgnoreCase("Moderator Messages Channel")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getModeratorChannelMenu(user))
                            .addComponents(ServerPreferencesComponents.getModeratorChannelMenu(server))
                            .send();
                }
                else if (label.equalsIgnoreCase("Enforce Server Language")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getEnforceServerLangMenu(user))
                            .addComponents(ServerPreferencesComponents.getEnforceServerLanguageMenu())
                            .send();
                }
            }
            else if (menuName.equalsIgnoreCase("Requests Channel")) {
                String channelID = selectMenuInteraction.getChosenOptions().get(0).getValue();

                selectMenuInteraction.acknowledge();

                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ServerPreferencesEmbed.getAcknowledgeRequestsChannelChange(server, user, channelID))
                        .addComponents()
                        .send();
            }
            else if (menuName.equalsIgnoreCase("Moderator Messages Channel")) {
                String channelID = selectMenuInteraction.getChosenOptions().get(0).getValue();

                selectMenuInteraction.acknowledge();

                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ServerPreferencesEmbed.getAcknowledgeModeratorChannelChange(server, user, channelID))
                        .addComponents()
                        .send();
            }
            else if (menuName.equalsIgnoreCase("Enforce Server Language")) {
                String bool = selectMenuInteraction.getChosenOptions().get(0).getValue();

                selectMenuInteraction.acknowledge();

                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ServerPreferencesEmbed.getAcknowledgeEnforceServerLanguageUpdate(server, user, bool))
                        .addComponents()
                        .send();
            }
        }
    }
}
