package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.ServerPreferencesEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
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

import java.util.HashMap;

public class SelectMenuChoose implements SelectMenuChooseListener {
    Logger logger = Main.getLogger();
    private LanguageManager languageManager;

    @Override
    public void onSelectMenuChoose(SelectMenuChooseEvent event) {
        SelectMenuInteraction selectMenuInteraction = event.getSelectMenuInteraction();

        Message message = selectMenuInteraction.getMessage();
        User user = selectMenuInteraction.getUser();
        Server server = selectMenuInteraction.getServer().orElse(null);
        TextChannel channel = selectMenuInteraction.getChannel().orElse(null);

        ContextManager context = new ContextManager(server, channel, user, user, null, new HashMap<>());
        languageManager = new LanguageManager(Main.getFallbackLocale(), context);

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
                            .addEmbed(UserPreferencesEmbed.getAccentColourMenu(languageManager, user))
                            .addComponents(UserPreferencesComponents.getAccentColourMenu(languageManager))
                            .send();
                }
                else if (label.equalsIgnoreCase("Language")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(UserPreferencesEmbed.getLanguageMenu(languageManager, user))
                            .addComponents()
                            .send();
                }
            }
            else if (menuName.equalsIgnoreCase("Accent Colour Editor")) {
                if (label.equalsIgnoreCase("Select Common Colours")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(UserPreferencesEmbed.getAccentColourListMenu(languageManager, user))
                            .addComponents(UserPreferencesComponents.getAccentColourList(languageManager))
                            .send();
                }
                else if (label.equalsIgnoreCase("Hexadecimal Entry")) {
                    message.delete();
                    selectMenuInteraction.respondWithModal("hex-entry-modal", "Hex Colour Entry", UserPreferencesComponents.getAccentColourHexEntry(languageManager));
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
                            .addEmbed(UserPreferencesEmbed.getAcknowledgeAccentColourChange(languageManager, user, accentColour))
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
                            .addEmbed(ServerPreferencesEmbed.getNotServerMenu(languageManager))
                            .addComponents()
                            .send();
                }
                else if (label.equalsIgnoreCase("Requests Channel")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getRequestsChannelMenu(languageManager, user))
                            .addComponents(ServerPreferencesComponents.getRequestsChannelMenu(languageManager, server))
                            .send();
                }
                else if (label.equalsIgnoreCase("Moderator Messages Channel")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getModeratorChannelMenu(languageManager, user))
                            .addComponents(ServerPreferencesComponents.getModeratorChannelMenu(languageManager, server))
                            .send();
                }
                else if (label.equalsIgnoreCase("Enforce Server Language")) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getEnforceServerLangMenu(languageManager, user))
                            .addComponents(ServerPreferencesComponents.getEnforceServerLanguageMenu(languageManager))
                            .send();
                }
            }
            else if (menuName.equalsIgnoreCase("Requests Channel")) {
                String channelID = selectMenuInteraction.getChosenOptions().get(0).getValue();

                selectMenuInteraction.acknowledge();

                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ServerPreferencesEmbed.getAcknowledgeRequestsChannelChange(languageManager, server, user, channelID))
                        .addComponents()
                        .send();
            }
            else if (menuName.equalsIgnoreCase("Moderator Messages Channel")) {
                String channelID = selectMenuInteraction.getChosenOptions().get(0).getValue();

                selectMenuInteraction.acknowledge();

                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ServerPreferencesEmbed.getAcknowledgeModeratorChannelChange(languageManager, server, user, channelID))
                        .addComponents()
                        .send();
            }
            else if (menuName.equalsIgnoreCase("Enforce Server Language")) {
                String bool = selectMenuInteraction.getChosenOptions().get(0).getValue();

                selectMenuInteraction.acknowledge();

                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ServerPreferencesEmbed.getAcknowledgeEnforceServerLanguageUpdate(languageManager, server, user, bool))
                        .addComponents()
                        .send();
            }
        }
    }
}
