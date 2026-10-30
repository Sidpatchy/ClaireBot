package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class AvatarEmbed {

    public static EmbedBuilder getAvatar(Guild server, User user, User author, boolean globalAvatar) {
        if (user == null) { user = author; }

        // Check whether we should display the user's global avatar, or server specific avatar.
        String avatar;
        if (!globalAvatar && server != null && server.getMember(user) != null) {
            avatar = server.getMember(user).getEffectiveAvatarUrl();
        }
        else {
            avatar = user.getEffectiveAvatarUrl();
        }

        return new EmbedBuilder()
                .setColor(Main.getColor(user.getId()))
                .setTimestamp(java.time.Instant.now())
                .setAuthor(user.getAsTag(), null, user.getEffectiveAvatarUrl()) // always display global avatar in author field.
                .setImage(avatar + "?size=4096")
                .setFooter(String.valueOf(user.getId()));
    }
}
