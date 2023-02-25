package com.sidpatchy.clairebot.MessageComponents.Regular;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.ActionRowBuilder;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerPreferencesComponents {

    public static ActionRow getMainMenu() {
        return new ActionRowBuilder()
                .addComponents(
                        SelectMenu.create("settings", "Click to display settings", 1, 1,
                                Arrays.asList(SelectMenuOption.create("Requests Channel", "Requests Channel", "Choose where ClaireBot should post requests."),
                                        SelectMenuOption.create("Moderator Messages Channel", "Moderator Messages Channel", "Choose where ClaireBot should mod messages."),
                                        SelectMenuOption.create("Enforce Server Language", "Enforce Server Language", "Force ClaireBot to use the same language as the server regardless of user preference.")))
                ).build();
    }

    public static ActionRow getRequestsChannelMenu(Server server) {
        return getChannelListActionRow(server, "requestsChannel");
    }

    public static ActionRow getModeratorChannelMenu(Server server) {
        return getChannelListActionRow(server, "moderatorChannel");
    }

    public static ActionRow getEnforceServerLanguageMenu() {
        return new ActionRowBuilder()
                .addComponents(
                        SelectMenu.create("enforceServerLanguage", "Click to select an option", 1, 1,
                                Arrays.asList(
                                        SelectMenuOption.create("True", "true"),
                                        SelectMenuOption.create("False", "false")
                                ))
                ).build();
    }

    /**
     * Create a select menu with a list of the server's channels.
     *
     * @param server The server the list should be generated for
     * @param customId The ID of the SelectMenu
     * @return ActionRow with SelectMenu
     */
    private static ActionRow getChannelListActionRow(Server server, String customId) {
        List<ServerTextChannel> channels = server.getTextChannels();
        List<SelectMenuOption> options = new ArrayList<>();
        for (ServerTextChannel channel : channels) {
            options.add(SelectMenuOption.create(channel.getName(), channel.getIdAsString()));
        }

        return new ActionRowBuilder()
                .addComponents(
                        SelectMenu.create(customId, "Click to select a channel", 1, 1, options)
                ).build();
    }
}
