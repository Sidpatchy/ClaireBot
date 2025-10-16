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

        // Route exclusively by customId and stable option values to avoid localization issues
        String customId = selectMenuInteraction.getCustomId();
        String value = selectMenuInteraction.getChosenOptions().get(0).getValue();

        // User preferences main menu (customId: "settings")
        if ("settings".equalsIgnoreCase(customId)) {
            // Values are stable English identifiers set in UserPreferencesComponents
            if ("Accent Colour Editor".equalsIgnoreCase(value)) {
                selectMenuInteraction.acknowledge();
                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(UserPreferencesEmbed.getAccentColourMenu(languageManager, user))
                        .addComponents(UserPreferencesComponents.getAccentColourMenu(languageManager))
                        .send();
            }
            else if ("Language Editor".equalsIgnoreCase(value)) {
                selectMenuInteraction.acknowledge();
                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(UserPreferencesEmbed.getLanguageMenu(languageManager, user))
                        .addComponents()
                        .send();
            }
            else if ("Requests Channel".equalsIgnoreCase(value)
                    || "Moderator Messages Channel".equalsIgnoreCase(value)
                    || "Enforce Server Language".equalsIgnoreCase(value)) {
                // This block will only be hit if server settings menu mistakenly used the same customId.
                // We keep it for backward compatibility; prefer using "server-settings" going forward.
                selectMenuInteraction.acknowledge();
                if (server == null) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getNotServerMenu(languageManager))
                            .addComponents()
                            .send();
                } else if ("Requests Channel".equalsIgnoreCase(value)) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getRequestsChannelMenu(languageManager, user))
                            .addComponents(ServerPreferencesComponents.getRequestsChannelMenu(languageManager, server))
                            .send();
                } else if ("Moderator Messages Channel".equalsIgnoreCase(value)) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getModeratorChannelMenu(languageManager, user))
                            .addComponents(ServerPreferencesComponents.getModeratorChannelMenu(languageManager, server))
                            .send();
                } else if ("Enforce Server Language".equalsIgnoreCase(value)) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ServerPreferencesEmbed.getEnforceServerLangMenu(languageManager, user))
                            .addComponents(ServerPreferencesComponents.getEnforceServerLanguageMenu(languageManager))
                            .send();
                }
            }
        }
        // User preferences accent color submenu and list share customId "accent-color"
        else if ("accent-color".equalsIgnoreCase(customId)) {
            // Two possible values from the first submenu, otherwise treat as selecting a color from the list
            if ("Select Common Colours".equalsIgnoreCase(value)) {
                selectMenuInteraction.acknowledge();
                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(UserPreferencesEmbed.getAccentColourListMenu(languageManager, user))
                        .addComponents(UserPreferencesComponents.getAccentColourList(languageManager))
                        .send();
            }
            else if ("Hexadecimal Entry".equalsIgnoreCase(value)) {
                // Switch to modal input, delete the menu message to reduce clutter
                selectMenuInteraction.acknowledge();
                message.delete();
                selectMenuInteraction.respondWithModal("hex-entry-modal", "Hex Colour Entry", UserPreferencesComponents.getAccentColourHexEntry(languageManager));
            }
            else {
                // Selecting a specific color from the list; hex is stored as the description
                selectMenuInteraction.acknowledge();
                String accentColour = selectMenuInteraction.getChosenOptions().get(0).getDescription().orElse("");
                if (accentColour.isEmpty()) {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(ErrorEmbed.getError(Main.getErrorCode("accentColourParse")))
                            .send();
                } else {
                    selectMenuInteraction.createFollowupMessageBuilder()
                            .setFlags(MessageFlag.EPHEMERAL)
                            .addEmbed(UserPreferencesEmbed.getAcknowledgeAccentColourChange(languageManager, user, accentColour))
                            .addComponents()
                            .send();
                }
            }
        }
        // Server configuration main menu (new customId: "server-settings")
        else if ("server-settings".equalsIgnoreCase(customId)) {
            selectMenuInteraction.acknowledge();
            if (server == null) {
                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ServerPreferencesEmbed.getNotServerMenu(languageManager))
                        .addComponents()
                        .send();
            } else if ("Requests Channel".equalsIgnoreCase(value)) {
                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ServerPreferencesEmbed.getRequestsChannelMenu(languageManager, user))
                        .addComponents(ServerPreferencesComponents.getRequestsChannelMenu(languageManager, server))
                        .send();
            } else if ("Moderator Messages Channel".equalsIgnoreCase(value)) {
                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ServerPreferencesEmbed.getModeratorChannelMenu(languageManager, user))
                        .addComponents(ServerPreferencesComponents.getModeratorChannelMenu(languageManager, server))
                        .send();
            } else if ("Enforce Server Language".equalsIgnoreCase(value)) {
                selectMenuInteraction.createFollowupMessageBuilder()
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addEmbed(ServerPreferencesEmbed.getEnforceServerLangMenu(languageManager, user))
                        .addComponents(ServerPreferencesComponents.getEnforceServerLanguageMenu(languageManager))
                        .send();
            }
        }
        // Server channel selection submenus
        else if ("requestsChannel".equalsIgnoreCase(customId)) {
            String channelID = value; // value holds the selected channel ID
            selectMenuInteraction.acknowledge();
            selectMenuInteraction.createFollowupMessageBuilder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .addEmbed(ServerPreferencesEmbed.getAcknowledgeRequestsChannelChange(languageManager, server, user, channelID))
                    .addComponents()
                    .send();
        }
        else if ("moderatorChannel".equalsIgnoreCase(customId)) {
            String channelID = value; // value holds the selected channel ID
            selectMenuInteraction.acknowledge();
            selectMenuInteraction.createFollowupMessageBuilder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .addEmbed(ServerPreferencesEmbed.getAcknowledgeModeratorChannelChange(languageManager, server, user, channelID))
                    .addComponents()
                    .send();
        }
        else if ("enforceServerLanguage".equalsIgnoreCase(customId)) {
            String bool = value;
            selectMenuInteraction.acknowledge();
            selectMenuInteraction.createFollowupMessageBuilder()
                    .setFlags(MessageFlag.EPHEMERAL)
                    .addEmbed(ServerPreferencesEmbed.getAcknowledgeEnforceServerLanguageUpdate(languageManager, server, user, bool))
                    .addComponents()
                    .send();
        }
    }
}
