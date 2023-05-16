package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Leveling.LevelingTools;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

public class LevelEmbed {

    public static EmbedBuilder getLevel(String serverID, User user) {
        Integer userPoints = LevelingTools.getUserPoints(user.getIdAsString(), serverID);

        if (userPoints == null) {
            userPoints = 0;
        }

        Integer userLevel = userPoints / 500;
        String levelString = userLevel + " | " + LevelingTools.getProgressBar((userPoints.floatValue() / 500), 0);



        return new EmbedBuilder()
                .setColor(Main.getColor(user.getIdAsString()))
                .setTimestampToNow()
                .setAuthor(user.getDiscriminatedName(), "", user.getAvatar())
                .setDescription(levelString)
                .setFooter(String.valueOf(user.getId()));
    }
}
