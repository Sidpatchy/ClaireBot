package com.sidpatchy.clairebot.Util;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;

public class ChannelUtils {

    // Eventually replace with query from database, if not present fallback to getModeratorsOnlyChannel.
    public static TextChannel getModeratorsOnlyChannel(Server server) {
        return server.getModeratorsOnlyChannel()
                .orElse(server.getSystemChannel()
                        .orElse(server.getTextChannels().get(1)));
    }
}
