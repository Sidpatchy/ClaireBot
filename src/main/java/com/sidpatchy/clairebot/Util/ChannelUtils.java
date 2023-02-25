package com.sidpatchy.clairebot.Util;

import com.sidpatchy.clairebot.API.Guild;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;

import java.io.IOException;

public class ChannelUtils {

    /**
     * Checks if a channel is registered in the database and returns it. If it doesn't exist, ClaireBot takes a shot in
     * the dark.
     *
     * Will never return null.
     *
     * @param server server to check for
     * @return requests channel
     */
    public static TextChannel getModeratorsOnlyChannel(Server server) {
        TextChannel channel = null;
        boolean apiFailed;

        try {
            Guild guild = new Guild(server.getIdAsString());
            guild.getGuild();
            channel = Main.getApi().getTextChannelById(guild.getModeratorMessagesChannelID()).orElse(null);
            apiFailed = false;
        }
        catch (IOException ignored) {
            apiFailed = true;
        }

        if (apiFailed || channel == null) {
            channel = server.getModeratorsOnlyChannel()
                    .orElse(server.getSystemChannel()
                            .orElse(server.getTextChannels().get(0)));
        }

        return channel;
    }

    /**
     * Checks if a channel is registered in the database and returns it. If it doesn't exist, ClaireBot takes a shot in
     * the dark.
     *
     * May return null.
     *
     * @param server server to check for
     * @return requests channel
     */
    public static ServerTextChannel getRequestsChannel(Server server) {
        ServerTextChannel channel = null;
        boolean apiFailed;

        try {
            Guild guild = new Guild(server.getIdAsString());
            guild.getGuild();
            channel = Main.getApi().getServerTextChannelById(guild.getRequestsChannelID()).orElse(null);
            apiFailed = false;
        } catch (IOException e) {
            apiFailed = true;
        }

        if (apiFailed || channel == null) {
            channel = server.getTextChannelsByName("requests").get(0);
        }

        return channel;
    }
}
