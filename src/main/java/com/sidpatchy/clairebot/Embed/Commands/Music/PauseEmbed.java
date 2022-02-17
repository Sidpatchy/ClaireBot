package com.sidpatchy.clairebot.Embed.Commands.Music;

import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Music.AudioManager;
import com.sidpatchy.clairebot.Util.Music.ServerMusicManager;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * Class to handle play and pause embeds
 */
public class PauseEmbed {

    public static EmbedBuilder getPauseEmbed(Server server, User author) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor())
                .setFooter(author.getDiscriminatedName(), author.getAvatar());

        server.getAudioConnection().ifPresentOrElse(audioConnection -> {
            ServerMusicManager manager = AudioManager.get(server.getId());

            if (manager.player.isPaused()) {
                AudioManager.get(server.getId()).player.setPaused(false);
                embed.setDescription("Playback resumed!");

            }
            else {
                AudioManager.get(server.getId()).player.setPaused(true);
                embed.setDescription("Playback paused!");
            }
        },
        () -> {
            embed.setDescription("No songs to play as the queue is empty.");
        });

        return embed;
    }
}
