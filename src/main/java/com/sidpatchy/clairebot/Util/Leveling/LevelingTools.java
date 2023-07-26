package com.sidpatchy.clairebot.Util.Leveling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sidpatchy.clairebot.API.APIUser;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelingTools {

    public static HashMap<String, Integer> rankUsers(String guildID) throws IOException {
        APIUser apiUser = new APIUser("");

        // Load the YAML data from an InputStream into a Java object
        Yaml yaml = new Yaml();
        List<Map<String, Object>> users = yaml.load(apiUser.getALLUsers());

        // Iterate over each user and calculate their total points
        HashMap<String, Integer> userPoints = new HashMap<>();
        for (Map<String, Object> user : users) {
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
            Map<String, Integer> pointsGuildIDMaps = parseJsonArray2(pointsGuildIDList);
            Integer userPointsForGuild = pointsGuildIDMaps.getOrDefault(guildID, 0);

            // Exclude users with zero or null points
            if (userPointsForGuild != null && userPointsForGuild != 0) {
                userPoints.put((String) user.get("userID"), userPointsForGuild);
            }
        }

        return userPoints;
    }



    public static Integer getUserPoints(String userID, String guildID) {
        APIUser apiUser = new APIUser(userID);
        try {
            apiUser.getUser();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Integer> guildPoints = parseJsonArray2(apiUser.getPointsGuildID());
        return guildPoints.get(guildID);
    }

    public static List<String> updateUserPoints(String userID, String guildID, int newPoints) {
        // Fetch the user's current points
        Map<String, Integer> currentPointsMap = parseJsonArray2(new APIUser(userID).getPointsGuildID());

        // Update the points
        int updatedPoints = currentPointsMap.getOrDefault(guildID, 0) + newPoints;
        currentPointsMap.put(guildID, updatedPoints);

        List<String> pointsByGuild = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : currentPointsMap.entrySet()) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode pointsNode = objectMapper.createObjectNode();
            String jsonString = pointsNode.put(entry.getKey(), entry.getValue()).toString();
            pointsByGuild.add(jsonString);
        }

        return pointsByGuild;
    }

    /**
     * Update the user points for multiple guilds at the same time.
     *
     * @param userID
     * @param guildPointsToUpdate
     * @return
     */
    public static List<String> updateUserPoints(String userID, Map<String, Integer> guildPointsToUpdate) {
        // Fetch the user's current points
        Map<String, Integer> currentPointsMap = parseJsonArray2(new APIUser(userID).getPointsGuildID());

        // Iterate over each guild ID and update the points
        for (Map.Entry<String, Integer> guildEntry : guildPointsToUpdate.entrySet()) {
            String guildID = guildEntry.getKey();
            int newPoints = guildEntry.getValue();
            int updatedPoints = currentPointsMap.getOrDefault(guildID, 0) + newPoints;
            currentPointsMap.put(guildID, updatedPoints);
        }

        List<String> pointsByGuild = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : currentPointsMap.entrySet()) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode pointsNode = objectMapper.createObjectNode();
            String jsonString = pointsNode.put(entry.getKey(), entry.getValue()).toString();
            pointsByGuild.add(jsonString);
        }

        return pointsByGuild;
    }


    private static Map<String, Integer> parseJsonArray2(List<String> jsonArray) {
        Map<String, Integer> result = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();  // Create an ObjectMapper instance

        for (String json : jsonArray) {

            // this if/else only exists because I messed up in every way possible at one point --CC
            if (json.equals("global")) {
                // If the JSON string is "global", assume the user has 0 points in all guilds
                result.put("global", 0);
            } else {
                // Otherwise, parse the JSON string and add it to the result list
                try {
                    // Parse the JSON string into a Map<String, Integer>
                    Map<String, Integer> map = mapper.readValue(json, new TypeReference<Map<String, Integer>>() {});
                    // Add all entries from the map to the result
                    result.putAll(map);
                } catch (IOException e) {
                    // Handle the exception
                    e.printStackTrace();
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
        String[] charset = switch (style) {
            case 0 -> new String[]{"▰", "▱"};
            case 1 -> new String[]{"◼", "▭"};
            case 2 -> new String[]{"⬛", "⬜"};
            case 3 -> new String[]{"▮", "▯"};
            default -> new String[]{};
        };

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
