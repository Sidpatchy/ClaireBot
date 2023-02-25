package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.time.Instant;

public class UserInfoEmbed {

    public static EmbedBuilder getUser(User user, User author, Server server) {

        String creationDate = "<t:" + user.getCreationTimestamp().toEpochMilli() / 1000 + ">";
        String timeSinceCreation = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - user.getCreationTimestamp().toEpochMilli(), true, false);

        if (author == null) {
            String errorCode = Main.getErrorCode("User_Info_Null");
            Main.getLogger().error("The value for author was null when passed into UserInfo Embed. Error code: " + errorCode);
            Main.getLogger().error("Author: " + author.getDiscriminatedName());
            return ErrorEmbed.getError(errorCode);
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(user.getIdAsString()))
                .setThumbnail(user.getAvatar())
                .setAuthor("User\n" + user.getDiscriminatedName())
                .addField("Discord ID", user.getIdAsString(), false);

        if (server != null) {
            String joinDate = "<t:" + user.getJoinedAtTimestamp(server).orElse(Instant.now()).toEpochMilli() / 1000 + ">";
            String timeSinceJoin = DurationFormatUtils.formatDurationWords(System.currentTimeMillis() - user.getJoinedAtTimestamp(server).orElse(Instant.ofEpochMilli(0)).toEpochMilli(), true, false);
            embed.addField("Server Join Date", joinDate + "\n*" + timeSinceJoin + "*", false);
        }

        embed.addField("Account Creation Date", creationDate + "\n*" + timeSinceCreation + "*", false);
        embed.setFooter(author.getDiscriminatedName() + " (" + author.getIdAsString() + ")", author.getAvatar());

        return embed;
    }
}
