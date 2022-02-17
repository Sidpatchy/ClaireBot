package com.sidpatchy.clairebot.Util.Music;

import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.channel.ServerVoiceChannel;

import java.util.concurrent.atomic.AtomicReference;

public class VoiceUtils {

    public static final AtomicReference<AudioConnection> audioConnection = new AtomicReference<>(null);

    public static void connect(ServerVoiceChannel channel) {
        // Silently fail if channel is null
        if (channel == null) { return; }

        if (channel.canYouConnect() && channel.canYouSee()) {
            channel.connect().thenAccept(audioConnection::set)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
            });
        }
    }
}
