package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Leveling.LevelingTools;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaderboardEmbed {

    public static EmbedBuilder getLeaderboard(Server server, User author) {
        String serverID = server.getIdAsString();

        HashMap<String, Integer> unsortedLevelMap = LevelingTools.rankUsers(serverID);
        Map<String, Integer> namedMap = convertUserIDstoNames(unsortedLevelMap);
        Map<String, Integer> sortedLevelMap = sortMap(namedMap);
        EmbedBuilder embed = initializeLeaderboardEmbed(sortedLevelMap, author);

        embed.setAuthor("Leaderboard for " + server.getName(), "", server.getIcon().orElse(null));

        return embed;
    }

    public static EmbedBuilder getLeaderboard(String serverID, User author) {
        HashMap<String, Integer> unsortedLevelMap = LevelingTools.rankUsers(serverID);
        Map<String, Integer> namedMap = convertUserIDstoNames(unsortedLevelMap);
        Map<String, Integer> sortedLevelMap = sortMap(namedMap);
        EmbedBuilder embed = initializeLeaderboardEmbed(sortedLevelMap, author);

        embed.setAuthor("Global Leaderboard");
        return embed;
    }

    private static Map<String, Integer> convertUserIDstoNames(Map<String, Integer> unsortedMap) {
        Map<String, Integer> namedMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : unsortedMap.entrySet()) {
            Main.getApi().getUserById(entry.getKey()).thenAccept(user -> {
                namedMap.put(user.getDiscriminatedName(), entry.getValue());
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
                .setColor(Main.getColor(author.getIdAsString()))
                .setTimestampToNow()
                .setFooter(String.valueOf(author.getId()));

        for (Map.Entry<String, Integer> entry : namedMap.entrySet()) {
            Integer userLevel = entry.getValue() / 500;
            String levelString = userLevel + " | " + LevelingTools.getProgressBar((entry.getValue().floatValue() / 500), 0);

            embed.addField(entry.getKey(), levelString);
        }

        return embed;
    }
}
