package com.sidpatchy.clairebot.Embed.Commands.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Music.AudioManager;
import com.sidpatchy.clairebot.Util.Music.PlayerAudioSource;
import com.sidpatchy.clairebot.Util.Music.PlayerManager;
import com.sidpatchy.clairebot.Util.Music.ServerMusicManager;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class PlayEmbed {
    private static final AudioPlayerManager manager = PlayerManager.getManager();

    private static final AtomicBoolean error = new AtomicBoolean(false);
    private static final AtomicReference<String> errorCode = new AtomicReference<>();

    private static final AtomicBoolean unableToFindTrack = new AtomicBoolean(false);
    private static final AtomicBoolean notConnectedToSameChannel = new AtomicBoolean(false);

    private static final AtomicReference<String> trackName = new AtomicReference<>();

    public static EmbedBuilder getPlayEmbed(String query, Server server, User author) {
        // Check if the user is in a voice channel
        author.getConnectedVoiceChannel(server).ifPresentOrElse(channel -> {
            User self = Main.getApi().getYourself();
            if (channel.canYouConnect() && channel.canYouSee() && channel.hasPermission(self, PermissionType.SPEAK)) {
                ServerMusicManager m = AudioManager.get(server.getId());

                if (!channel.isConnected(self) && server.getAudioConnection().isEmpty()) {
                    channel.connect().thenAccept(audioConnection -> {
                        AudioSource audio = new PlayerAudioSource(Main.getApi(), m.player);
                        audioConnection.setAudioSource(audio);
                        audioConnection.setSelfDeafened(true);

                        play(query, m);
                    });
                }
                else if (server.getAudioConnection().isPresent()) {
                    server.getAudioConnection().ifPresent(audioConnection -> {

                        if (audioConnection.getChannel().getId() == channel.getId()) {
                            AudioSource audio = new PlayerAudioSource(Main.getApi(), m.player);
                            audioConnection.setAudioSource(audio);
                            audioConnection.setSelfDeafened(true);

                            play(query, m);
                        }
                    });
                }
                else {
                    notConnectedToSameChannel.set(true);
                    errorCode.set(Main.getErrorCode("play_notInSameChannel"));
                }
            }
        }, () -> {
            //else
        });

        if (error.get()) {
            return ErrorEmbed.getError(errorCode.get());
        }
        else if (unableToFindTrack.get()) {
            return ErrorEmbed.getError(errorCode.get(), "I wasn't able to find the requested track.");
        }
        else if (notConnectedToSameChannel.get()) {
            return ErrorEmbed.getCustomError(errorCode.get(), "You're not connected to the same channel as me! Move me or switch channels. \n\n" +
                    "If we're in the same channel, try reconnecting me, this can occasionally happen shortly after I restart.");
        }
        else {
            return new EmbedBuilder()
                    .setColor(Main.getColor(author.getIdAsString()))
                    .setAuthor("Added to Queue")
                    .setDescription(trackName.get())
                    .setFooter(author.getDiscriminatedName(), author.getAvatar());
        }
    }

    private static void play(String query, ServerMusicManager m) {

        manager.loadItemOrdered(m, isURL(query) ? query : "ytsearch: " + query,
                new FunctionalResultHandler(audioTrack -> {
                    m.scheduler.queue(audioTrack);
                    trackName.set(audioTrack.getInfo().title);
                },
                audioPlaylist -> {
                    if (audioPlaylist.isSearchResult()) {
                        m.scheduler.queue(audioPlaylist.getTracks().get(0));
                        trackName.set(audioPlaylist.getTracks().get(0).getInfo().title);
                    }
                    else {
                        audioPlaylist.getTracks().forEach(audioTrack -> {
                            m.scheduler.queue(audioTrack);
                            trackName.set(audioTrack.getInfo().title);
                        });
                    }
                },
                () -> {
                    unableToFindTrack.set(true);
                    errorCode.set(Main.getErrorCode("play_null"));
                    Main.getLogger().error("Unable to play track " + query + "Error Code: " + errorCode);
                },
                e -> {
                    errorCode.set(Main.getErrorCode("play_error"));
                    Main.getLogger().error(e.getMessage());
                    Main.getLogger().error("Unable to play track " + query + "Error Code: " + errorCode);
                    error.set(true);
                }));
        Main.getLogger().info(trackName.get());
    }

    private static boolean isURL(String query) {
        try {
            URL url = new URL(query);
        }
        catch (MalformedURLException ignored) {
            return false;
        }
        return true;
    }
}
