package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Leveling.LevelingTools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class LevelEmbed {

    public static EmbedBuilder getLevel(String serverID, User user) {
        Integer userPoints = LevelingTools.getUserPoints(user.getId(), serverID);

        if (userPoints == null) {
            userPoints = 0;
        }

        Integer userLevel = userPoints / 500;
        String levelString = userLevel + " | " + LevelingTools.getProgressBar((userPoints.floatValue() / 500), 0);



        return new EmbedBuilder()
                .setColor(Main.getColor(user.getId()))
                .setTimestamp(java.time.Instant.now())
                .setAuthor(user.getAsTag(), null, user.getEffectiveAvatarUrl())
                .setDescription(levelString)
                .setFooter(String.valueOf(user.getId()));
    }
}
