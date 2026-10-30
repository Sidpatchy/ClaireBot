package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.ServerPreferencesEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.MessageComponents.Regular.ServerPreferencesComponents;
import com.sidpatchy.clairebot.MessageComponents.Regular.UserPreferencesComponents;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import org.apache.logging.log4j.Logger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.modals.Modal;

import java.util.HashMap;

public class SelectMenuChoose extends ListenerAdapter {
    Logger logger = Main.getLogger();
    private LanguageManager languageManager;

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        StringSelectInteractionEvent selectMenuInteraction = event;

        Message message = selectMenuInteraction.getMessage();
        User user = selectMenuInteraction.getUser();
        Guild server = selectMenuInteraction.getGuild();
        MessageChannel channel = selectMenuInteraction.getChannel();

        ContextManager context = new ContextManager(server, channel, user, user, null, new HashMap<>());
        languageManager = new LanguageManager(Main.getFallbackLocale(), context);

        // Route exclusively by customId and stable option values to avoid localization issues
        String customId = selectMenuInteraction.getComponentId();
        String value = selectMenuInteraction.getValues().get(0);

        // User preferences main menu (customId: "settings")
        if ("settings".equalsIgnoreCase(customId)) {
            // Values are stable English identifiers set in UserPreferencesComponents
            if ("Accent Colour Editor".equalsIgnoreCase(value)) {
                reply(selectMenuInteraction, UserPreferencesEmbed.getAccentColourMenu(languageManager, user),
                        UserPreferencesComponents.getAccentColourMenu(languageManager));
            }
            else if ("Language Editor".equalsIgnoreCase(value)) {
                reply(selectMenuInteraction, UserPreferencesEmbed.getLanguageMenu(languageManager, user),
                        UserPreferencesComponents.getLanguageMenu(languageManager));
            }
            else if ("Requests Channel".equalsIgnoreCase(value)
                    || "Moderator Messages Channel".equalsIgnoreCase(value)
                    || "Enforce Server Language".equalsIgnoreCase(value)) {
                // This block will only be hit if server settings menu mistakenly used the same customId.
                // We keep it for backward compatibility; prefer using "server-settings" going forward.
                if (server == null) {
                    reply(selectMenuInteraction, ServerPreferencesEmbed.getNotServerMenu(languageManager));
                } else if ("Requests Channel".equalsIgnoreCase(value)) {
                    reply(selectMenuInteraction, ServerPreferencesEmbed.getRequestsChannelMenu(languageManager, user),
                            ServerPreferencesComponents.getRequestsChannelMenu(languageManager, server));
                } else if ("Moderator Messages Channel".equalsIgnoreCase(value)) {
                    reply(selectMenuInteraction, ServerPreferencesEmbed.getModeratorChannelMenu(languageManager, user),
                            ServerPreferencesComponents.getModeratorChannelMenu(languageManager, server));
                } else if ("Enforce Server Language".equalsIgnoreCase(value)) {
                    reply(selectMenuInteraction, ServerPreferencesEmbed.getEnforceServerLangMenu(languageManager, user),
                            ServerPreferencesComponents.getEnforceServerLanguageMenu(languageManager));
                }
            }
        }
        // User preferences accent color submenu and list share customId "accent-color"
        else if ("accent-color".equalsIgnoreCase(customId)) {
            // Two possible values from the first submenu, otherwise treat as selecting a color from the list
            if ("Select Common Colours".equalsIgnoreCase(value)) {
                reply(selectMenuInteraction, UserPreferencesEmbed.getAccentColourListMenu(languageManager, user),
                        UserPreferencesComponents.getAccentColourList(languageManager));
            }
            else if ("Hexadecimal Entry".equalsIgnoreCase(value)) {
                // Switch to modal input, delete the menu message to reduce clutter
                selectMenuInteraction.replyModal(Modal.create("hex-entry-modal", "Hex Colour Entry")
                        .addComponents(UserPreferencesComponents.getAccentColourHexEntry(languageManager)).build()).queue();
                message.delete().queue();
            }
            else {
                // Selecting a specific color from the list; hex is stored as the description
                String accentColour = selectMenuInteraction.getSelectedOptions().get(0).getDescription();
                if (accentColour.isEmpty()) {
                    reply(selectMenuInteraction, ErrorEmbed.getError(languageManager, Main.getErrorCode("accentColourParse")));
                } else {
                    reply(selectMenuInteraction, UserPreferencesEmbed.getAcknowledgeAccentColourChange(languageManager, user, accentColour));
                }
            }
        }
        // Server configuration main menu (new customId: "server-settings")
        else if ("server-settings".equalsIgnoreCase(customId)) {
            if (server == null) {
                reply(selectMenuInteraction, ServerPreferencesEmbed.getNotServerMenu(languageManager));
            } else if ("Requests Channel".equalsIgnoreCase(value)) {
                reply(selectMenuInteraction, ServerPreferencesEmbed.getRequestsChannelMenu(languageManager, user),
                        ServerPreferencesComponents.getRequestsChannelMenu(languageManager, server));
            } else if ("Moderator Messages Channel".equalsIgnoreCase(value)) {
                reply(selectMenuInteraction, ServerPreferencesEmbed.getModeratorChannelMenu(languageManager, user),
                        ServerPreferencesComponents.getModeratorChannelMenu(languageManager, server));
            } else if ("Enforce Server Language".equalsIgnoreCase(value)) {
                reply(selectMenuInteraction, ServerPreferencesEmbed.getEnforceServerLangMenu(languageManager, user),
                        ServerPreferencesComponents.getEnforceServerLanguageMenu(languageManager));
            } else if ("Server Language".equalsIgnoreCase(value)) {
                reply(selectMenuInteraction, ServerPreferencesEmbed.getServerLanguageMenu(languageManager, user),
                        ServerPreferencesComponents.getServerLanguageMenu(languageManager));
            }
        }
        // Server channel selection submenus
        else if ("requestsChannel".equalsIgnoreCase(customId)) {
            String channelID = value; // value holds the selected channel ID
            reply(selectMenuInteraction, ServerPreferencesEmbed.getAcknowledgeRequestsChannelChange(languageManager, server, user, channelID));
        }
        else if ("moderatorChannel".equalsIgnoreCase(customId)) {
            String channelID = value; // value holds the selected channel ID
            reply(selectMenuInteraction, ServerPreferencesEmbed.getAcknowledgeModeratorChannelChange(languageManager, server, user, channelID));
        }
        else if ("enforceServerLanguage".equalsIgnoreCase(customId)) {
            String bool = value;
            reply(selectMenuInteraction, ServerPreferencesEmbed.getAcknowledgeEnforceServerLanguageUpdate(languageManager, server, user, bool));
        }
        else if ("server-language".equalsIgnoreCase(customId)) {
            String languageTag = value;
            reply(selectMenuInteraction, ServerPreferencesEmbed.getAcknowledgeServerLanguageChange(languageManager, server, user, languageTag));
        }
        else if ("user-language".equalsIgnoreCase(customId)) {
            // Value is the IETF language tag (e.g., en-US)
            String languageTag = value;
            reply(selectMenuInteraction, UserPreferencesEmbed.getAcknowledgeLanguageChange(languageManager, user, languageTag));
        }
    }

    private static void reply(StringSelectInteractionEvent event, EmbedBuilder embed, ActionRow... components) {
        event.deferEdit().queue(hook -> hook.sendMessageEmbeds(embed.build())
                .addComponents(components).setEphemeral(true).queue());
    }
}
