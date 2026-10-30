package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Leveling.LevelingTools;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaderboardEmbed {

    public static EmbedBuilder getLeaderboard(LanguageManager languageManager, Guild server, User author) {
        // Language Strings
        String leaderboardFor = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.LeaderboardEmbed.LeaderboardForServer");

        String serverID = server.getId();

        HashMap<String, Integer> unsortedLevelMap = null;
        try {
            unsortedLevelMap = LevelingTools.rankUsers(serverID);
        } catch (IOException e) {
            String errorCode = Main.getErrorCode("loclead");

            Main.getLogger().error("Failed to query database while generating leaderboard. Ref: {}", errorCode, e);
            return ErrorEmbed.getError(languageManager, errorCode);
        }
        Map<String, Integer> namedMap = convertUserIDstoNames(unsortedLevelMap);
        Map<String, Integer> sortedLevelMap = sortMap(namedMap);
        EmbedBuilder embed = initializeLeaderboardEmbed(sortedLevelMap, author);

        embed.setAuthor(leaderboardFor + " " + server.getName(), null, server.getIconUrl());

        return embed;
    }

    public static EmbedBuilder getLeaderboard(LanguageManager languageManager, String serverID, User author) {
        String globalLeaderboard = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.LeaderboardEmbed.GlobalLeaderboard");

        HashMap<String, Integer> unsortedLevelMap;
        try {
            unsortedLevelMap = LevelingTools.rankUsers(serverID);
        } catch (IOException e) {
            String errorCode = Main.getErrorCode("globlead");

            Main.getLogger().error("Failed to query database while generating leaderboard. Ref: {}", errorCode, e);
            return ErrorEmbed.getError(languageManager, errorCode);
        }
        Map<String, Integer> namedMap = convertUserIDstoNames(unsortedLevelMap);
        Map<String, Integer> sortedLevelMap = sortMap(namedMap);
        EmbedBuilder embed = initializeLeaderboardEmbed(sortedLevelMap, author);

        embed.setAuthor(globalLeaderboard);
        return embed;
    }

    private static Map<String, Integer> convertUserIDstoNames(Map<String, Integer> unsortedMap) {
        Map<String, Integer> namedMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : unsortedMap.entrySet()) {
            Main.getApi().retrieveUserById(entry.getKey()).queue(user -> {
                namedMap.put(user.getAsTag(), entry.getValue());
            });
            if (namedMap.size() >= 10) {
                break;
            }
        }

        return namedMap;
    }

    private static Map<String, Integer> sortMap(Map<String, Integer> namedMap) {
        return namedMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private static EmbedBuilder initializeLeaderboardEmbed(Map<String, Integer> namedMap, User author) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getColor(author.getId()))
                .setTimestamp(java.time.Instant.now())
                .setFooter(String.valueOf(author.getId()));

        for (Map.Entry<String, Integer> entry : namedMap.entrySet()) {
            Integer userLevel = entry.getValue() / 500;
            String levelString = userLevel + " | " + LevelingTools.getProgressBar((entry.getValue().floatValue() / 500), 0);

            embed.addField(entry.getKey(), levelString, false);
        }

        return embed;
    }
}
