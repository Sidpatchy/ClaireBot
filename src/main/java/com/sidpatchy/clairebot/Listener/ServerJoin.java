package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.WelcomeEmbed;
import com.sidpatchy.clairebot.Util.ChannelUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;

public class ServerJoin implements ServerJoinListener {

    /**
     *
     * Welcome users to ClaireBot when added to a new server.
     *
     * @param event
     */
    @Override
    public void onServerJoin(ServerJoinEvent event) {
        Server server = event.getServer();

        TextChannel channel = ChannelUtils.getModeratorsOnlyChannel(server);
        channel.sendMessage(WelcomeEmbed.getWelcome(server));
    }
}
