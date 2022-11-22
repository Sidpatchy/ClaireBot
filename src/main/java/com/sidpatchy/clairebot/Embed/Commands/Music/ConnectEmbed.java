package com.sidpatchy.clairebot.Embed.Commands.Music;

import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectEmbed {

    private static final AtomicBoolean error = new AtomicBoolean(false);

    public static EmbedBuilder getConnectEmbed(User author, ServerVoiceChannel channel) {
        if (channel == null) { return ErrorEmbed.getError(Main.getErrorCode("connect_NullChannel")); }

        if (channel.canYouConnect() && channel.canYouSee()) {
            channel.connect().thenAccept(audioConnection -> {})
                    .exceptionally(e -> {
                        e.printStackTrace();
                        error.set(true);
                        return null;
                    });
        }
        if (error.get()) {
            return ErrorEmbed.getError(Main.getErrorCode("connectionFailure"));
        }
        else {
            return new EmbedBuilder()
                    .setColor(Main.getColor(author.getIdAsString()))
                    .setDescription("Connected to <#" + channel.getIdAsString() + ">")
                    .setFooter(author.getDiscriminatedName(), author.getAvatar());
        }
    }
}
