package com.sidpatchy.clairebot.Embed.Commands.Music;

import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Music.AudioManager;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.concurrent.atomic.AtomicBoolean;

public class LeaveEmbed {

    private static final AtomicBoolean error = new AtomicBoolean(false);

    public static EmbedBuilder getLeaveEmbed(User author, Server server, ServerVoiceChannel channel) {
        if (channel == null) { return ErrorEmbed.getCustomError(Main.getErrorCode("disconnect_NullChannel"), "It looks like you're not in a voice channel. Please join my voice channel and try again."); }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor())
                .setFooter(author.getDiscriminatedName(), author.getAvatar());

        server.getAudioConnection().ifPresentOrElse(audioConnection -> {
            AudioManager.get(server.getId()).player.stopTrack();
            channel.disconnect();
            embed.setDescription("Disconnected from <#" + channel.getIdAsString() + ">");
        },
        () -> {
            embed.setDescription("I'm fairly certain I'm not in a call. Unable to leave.");
        });

        return embed;
    }
}
