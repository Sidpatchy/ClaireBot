package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.WelcomeEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.ChannelUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.server.ServerJoinEvent;
import org.javacord.api.listener.server.ServerJoinListener;

public class ServerJoin implements ServerJoinListener {

    private LanguageManager languageManager;

    /**
     *
     * Welcome users to ClaireBot when added to a new server.
     *
     * @param event
     */
    @Override
    public void onServerJoin(ServerJoinEvent event) {
        Server server = event.getServer();

        languageManager = new LanguageManager(Main.getFallbackLocale(), null); // this null should probably be fine

        TextChannel channel = ChannelUtils.getModeratorsOnlyChannel(server);
        channel.sendMessage(WelcomeEmbed.getWelcome(languageManager, server));
    }
}
