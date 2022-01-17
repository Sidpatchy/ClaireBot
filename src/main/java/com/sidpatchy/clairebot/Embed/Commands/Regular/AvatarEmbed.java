package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class AvatarEmbed {

    public static EmbedBuilder getAvatar(User user, User author) {
        if (user == null) { user = author; }

        return new EmbedBuilder()
                .setColor(Main.getColor())
                .setTimestampToNow()
                .setAuthor(user.getDiscriminatedName(), "", user.getAvatar())
                .setImage(user.getAvatar().getUrl().toString() + "?size=1024")
                .setFooter(String.valueOf(user.getId()));
    }
}
