package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.time.Instant;

public class UserInfoEmbed {

    public static EmbedBuilder getUser(LanguageManager languageManager, User user, User author, Server server) {
        // Temp / localized variables block
        long nowMs = System.currentTimeMillis();
        long creationMs = user.getCreationTimestamp().toEpochMilli();
        String creationDateTag = "<t:" + (creationMs / 1000) + ">";
        String timeSinceCreation = DurationFormatUtils.formatDurationWords(nowMs - creationMs, true, false);

        String userLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserInfoEmbed.User");
        String discordIdLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserInfoEmbed.DiscordID");
        String joinDateLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserInfoEmbed.JoinDate");
        String creationDateLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserInfoEmbed.CreationDate");

        // Null guard for author (use placeholders)
        if (author == null) {
            String errorCode = Main.getErrorCode("User_Info_Null");
            languageManager.addContext(ContextManager.ContextType.GENERIC, "errorcode", errorCode);
            languageManager.addContext(ContextManager.ContextType.GENERIC, "user.id.username", "null");

            String err1 = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserInfoEmbed.Error_1");
            String err2 = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserInfoEmbed.Error_2");
            Main.getLogger().error(err1);
            Main.getLogger().error(err2);
            return ErrorEmbed.getError(languageManager, errorCode);
        }

        String footerText = author.getDiscriminatedName() + " (" + author.getIdAsString() + ")";

        // Build embed
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(user.getIdAsString()))
                .setThumbnail(user.getAvatar())
                .setAuthor(userLabel + "\n" + user.getDiscriminatedName())
                .addField(discordIdLabel, user.getIdAsString(), false);

        if (server != null) {
            Instant joinedAt = user.getJoinedAtTimestamp(server).orElse(Instant.now());
            long joinMs = joinedAt.toEpochMilli();
            String joinDateTag = "<t:" + (joinMs / 1000) + ">";
            long sinceBase = user.getJoinedAtTimestamp(server).orElse(Instant.ofEpochMilli(0)).toEpochMilli();
            String timeSinceJoin = DurationFormatUtils.formatDurationWords(nowMs - sinceBase, true, false);
            embed.addField(joinDateLabel, joinDateTag + "\n*" + timeSinceJoin + "*", false);
        }

        embed.addField(creationDateLabel, creationDateTag + "\n*" + timeSinceCreation + "*", false)
                .setFooter(footerText, author.getAvatar());

        return embed;
    }
}
