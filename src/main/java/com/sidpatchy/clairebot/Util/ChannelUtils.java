package com.sidpatchy.clairebot.Util;

import com.sidpatchy.clairebot.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.Permission;

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
    public static TextChannel getModeratorsOnlyChannel(Guild server) {
        TextChannel channel = null;
        boolean apiFailed;

        try {
            com.sidpatchy.clairebot.API.Guild guild = new com.sidpatchy.clairebot.API.Guild(server.getId());
            guild.getGuild();
            String channelID = guild.getModeratorMessagesChannelID();
            if (channelID != null && channelID.matches("\\d+")) {
                channel = Main.getApi().getTextChannelById(channelID);
            }
            apiFailed = false;
        }
        catch (IOException ignored) {
            apiFailed = true;
        }

        if (apiFailed || channel == null) {
            channel = server.getTextChannels().stream()
                    .filter(text -> text.getPermissionOverride(server.getPublicRole()) != null
                            && text.getPermissionOverride(server.getPublicRole()).getDenied().contains(Permission.VIEW_CHANNEL))
                    .findFirst().orElse(server.getSystemChannel() != null ? server.getSystemChannel()
                            : server.getTextChannels().get(0));
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
    public static TextChannel getRequestsChannel(Guild server) {
        TextChannel channel = null;
        boolean apiFailed;

        try {
            com.sidpatchy.clairebot.API.Guild guild = new com.sidpatchy.clairebot.API.Guild(server.getId());
            guild.getGuild();
            String channelID = guild.getRequestsChannelID();
            if (channelID != null && channelID.matches("\\d+")) {
                channel = Main.getApi().getTextChannelById(channelID);
            }
            apiFailed = false;
        } catch (IOException e) {
            apiFailed = true;
        }

        if (apiFailed || channel == null) {
            var byName = server.getTextChannelsByName("requests", true);
            channel = byName.isEmpty() ? null : byName.get(0);
        }

        return channel;
    }
}
