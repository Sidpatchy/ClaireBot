package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.WelcomeEmbed;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.ChannelUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerJoin extends ListenerAdapter {

    private LanguageManager languageManager;

    /**
     *
     * Welcome users to ClaireBot when added to a new server.
     *
     * @param event
     */
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Guild server = event.getGuild();

        languageManager = new LanguageManager(Main.getFallbackLocale(), null); // this null should probably be fine

        TextChannel channel = ChannelUtils.getModeratorsOnlyChannel(server);
        channel.sendMessageEmbeds(WelcomeEmbed.getWelcome(languageManager, server).build()).queue();
    }
}
