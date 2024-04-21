package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class AvatarEmbed {

    public static EmbedBuilder getAvatar(Server server, User user, User author, boolean globalAvatar) {
        if (user == null) { user = author; }

        // Check whether we should display the user's global avatar, or server specific avatar.
        Icon avatar;
        if (!globalAvatar && server != null) {
            avatar = user.getServerAvatar(server).orElse(user.getAvatar());
        }
        else {
            avatar = user.getAvatar();
        }

        return new EmbedBuilder()
                .setColor(Main.getColor(user.getIdAsString()))
                .setTimestampToNow()
                .setAuthor(user.getDiscriminatedName(), "", user.getAvatar()) // always display global avatar in author field.
                .setImage(avatar.getUrl().toString().replace("?size=1024", "?size=4096"))
                .setFooter(String.valueOf(user.getId()));
    }
}
