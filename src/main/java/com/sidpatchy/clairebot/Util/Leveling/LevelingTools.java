package com.sidpatchy.clairebot.Util.Leveling;

import com.sidpatchy.clairebot.API.APIUser;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.*;

public class LevelingTools {

    public static List<Map.Entry<String, Integer>> rankUsers(String guildID) {
        APIUser apiUser = new APIUser("");

        // Load the YAML data from an InputStream into a Java object
        Yaml yaml = new Yaml();
        List<Map<String, Object>> users = yaml.load(apiUser.getALLUsers());

        // Iterate over each user and calculate their total points
        Map<String, Integer> userPoints = new HashMap<>();
        for (Map<String, Object> user : users) {
            int totalPoints = 0;
            Object pointsGuildID = user.get("pointsGuildID");
            List<String> pointsGuildIDList = new ArrayList<>();
            if (pointsGuildID instanceof String) {
                // If pointsGuildID is a string, create a list with a single element
                pointsGuildIDList.add((String) pointsGuildID);
            } else if (pointsGuildID instanceof List) {
                // Otherwise, cast pointsGuildID to a list of strings
                pointsGuildIDList = (List<String>) pointsGuildID;
            }
            // Parse the pointsGuildID list and sum the points for the matching guild ID
            List<Map<String, Object>> pointsGuildIDMaps = parseJsonArray(pointsGuildIDList);
            for (Map<String, Object> point : pointsGuildIDMaps) {
                if (point.get("guildID").equals(guildID)) { // Change the guild ID to match your criteria
                    totalPoints += (int) point.get("points");
                }
            }
            userPoints.put((String) user.get("userID"), totalPoints);
        }

        // Sort the userPoints map by value in descending order
        List<Map.Entry<String, Integer>> sortedUserPoints = new ArrayList<>(userPoints.entrySet());
        sortedUserPoints.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        return sortedUserPoints;
    }

    public String getUserPoints(String userID, String guildID) {
        APIUser apiUser = new APIUser(userID);
        try {
            apiUser.getUser();
            apiUser.getPointsGuildID();
            return null; // can't be bothered to finish this right now
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Map<String, Object>> parseJsonArray(List<String> jsonArray) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String json : jsonArray) {
            if (json.equals("global")) {
                // If the JSON string is "global", assume the user has 0 points in all guilds
                result.add(Map.of("guildID", "global", "points", 0));
            } else {
                // Otherwise, parse the JSON string and add it to the result list
                Object yamlObject = new Yaml().load(json);
                if (yamlObject instanceof Map) {
                    result.add((Map<String, Object>) yamlObject);
                }
            }
        }
        return result;
    }

    /**
     *
     * @param percentage in decimal form
     * @param style 0, 1, 2, or 3
     * @return progress bar
     */
    public static String getProgressBar(float percentage, int style) {
        String[] charset = new String[]{};

        switch (style) {
            case 0:
                charset = new String[]{"▰", "▱"};
                break;

            case 1:
                charset = new String[]{"◼", "▭"};
                break;

            case 2:
                charset = new String[]{"⬛", "⬜"};
                break;

            case 3:
                charset = new String[]{"▮", "▯"};
                break;
        }

        int percent;
        try {
            percent = Integer.parseInt(String.valueOf(percentage).substring(2, 3));
        } catch (Exception e) {
            percent = 0;
        }

        return charset[0].repeat(Math.max(0, percent)) +
                charset[1].repeat(Math.max(0, 10 - percent));
    }
}
