package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

public class AvatarEmbed {

    public static EmbedBuilder getAvatar(User user, User author) {
        if (user == null) { user = author; }

        return new EmbedBuilder()
                .setColor(Main.getColor(user.getIdAsString()))
                .setTimestampToNow()
                .setAuthor(user.getDiscriminatedName(), "", user.getAvatar())
                .setImage(user.getAvatar().getUrl().toString().replace("?size=1024", "?size=4096"))
                .setFooter(String.valueOf(user.getId()));
    }
}
