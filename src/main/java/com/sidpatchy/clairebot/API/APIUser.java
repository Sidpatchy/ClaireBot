package com.sidpatchy.clairebot.API;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sidpatchy.Robin.File.RobinConfiguration;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Leveling.LevelingTools;
import com.sidpatchy.clairebot.Util.Network.DELETE;
import com.sidpatchy.clairebot.Util.Network.POST;
import com.sidpatchy.clairebot.Util.Network.PUT;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class APIUser {
    private final String userID;
    private final RobinConfiguration user = new RobinConfiguration();

    public APIUser(String userID) {
        this.userID = userID;
    }
    boolean createNewWithDefaults = true;

    /**
     * Gets user info from database. Must always be called before attempting to access guild data.
     *
     * @throws IOException if there is any error, likely the user doesn't yet exist.
     */
    public void getUser() throws IOException {
        try {
            user.loadFromURL(Main.getApiUser(), Main.getApiPassword(), Main.getApiPath() + "api/v1/user/" + userID);
        }
        catch (Exception e) {
            if (createNewWithDefaults) {
                createNewWithDefaults = false; // prevent recursion if ClaireData goes down.
                try {
                    createUserWithDefaults();
                    getUser();
                }
                catch (Exception e2) {
                    throw new IOException("It is reasonably likely that the database has gone down.");
                }
            }
        }

        //fixUserPointsGuildID();
    }

    /**
     *
     * @return the user's selected accent colour
     */
    public String getAccentColour() {
        return user.getString("accentColour");
    }

    /**
     *
     * @return the users preferred language
     */
    public String getLanguage() {
        return user.getString("language");
    }

    /**
     *
     * @return the value of pointsGuildID
     */
    public List<String> getPointsGuildID() {
        return user.getList("pointsGuildID", String.class);
    }

    /**
     *
     * @return
     */
    public List<Integer> getPointsMessages() {
        return user.getList("pointsMessages", Integer.class);
    }

    public List<Integer> getPointsVoiceChat() {
        return user.getList("pointsVoiceChat", Integer.class);
    }

    public void createUser(String accentColour,
                           String language,
                           List<String> pointsGuildID,
                           List<Integer> pointsMessages,
                           List<Integer> pointsVoiceChat) throws IOException {
        POST post = new POST();
        post.postToURL(Main.getApiPath() + "api/v1/user/", userConstructor(accentColour, language, pointsGuildID, pointsMessages, pointsVoiceChat));
    }

    public void createUserWithDefaults() {
        RobinConfiguration.RobinSection defaults = new RobinConfiguration.RobinSection(Main.getUserDefaults());

        try {
            createUser(
                    defaults.getString("accentColour"),
                    defaults.getString("language"),
                    defaults.getList("pointsGuildID", String.class),
                    defaults.getList("pointsMessages", Integer.class),
                    defaults.getList("pointsVoiceChat", Integer.class)
            );
        }
        catch (Exception e) {
            Main.getLogger().error("Unable to create user with defaults.", e);
        }
        createNewWithDefaults = false; // prevent recursion if ClaireData goes down.
    }

    public void updateUser(String accentColour,
                           String language,
                           List<String> pointsGuildID,
                           List<Integer> pointsMessages,
                           List<Integer> pointsVoiceChat) throws IOException {
        // Add null check and fallback for language
        if (language == null) {
            new Exception("Language null origin trace").printStackTrace();
        }

        PUT put = new PUT();
        put.putToURL(Main.getApiPath() + "api/v1/user/" + userID,
                userConstructor(accentColour, language, pointsGuildID, pointsMessages, pointsVoiceChat));
    }

    public void updateUserColour(String accentColour) throws IOException {
        // Ensure that the values for the getters below are populated before querying.
        getUser();
        updateUser(accentColour,
                getLanguage(),
                getPointsGuildID(),
                getPointsMessages(),
                getPointsVoiceChat()
        );
    }

    public void updateUserLanguage(String languageString) throws IOException {
        // Ensure that the values for the getters below are populated before querying.
        getUser();
        updateUser(getAccentColour(),
                languageString,
                getPointsGuildID(),
                getPointsMessages(),
                getPointsVoiceChat());
    }

    public void updateUserPointsGuildID(String guildID, Integer newPoints) throws IOException {
        updateUserPointsGuildID(LevelingTools.updateUserPoints(userID, guildID, newPoints));
    }

    public void updateUserPointsGuildID(Map<String, Integer> guildPointsToUpdate) throws IOException {
        updateUserPointsGuildID(LevelingTools.updateUserPoints(userID, guildPointsToUpdate));
    }

    public void updateUserPointsGuildID(List<String> pointsGuildID) throws IOException {
        // Ensure that the values for the getters below are populated before querying.
        getUser();
        updateUser(getAccentColour(),
                getLanguage(),
                pointsGuildID,
                getPointsMessages(),
                getPointsVoiceChat());
    }

    public void deleteUser() throws IOException {
        DELETE delete = new DELETE();
        delete.deleteToURL(Main.getApiPath() + "api/v1/user/" + userID);
    }

    /**
     * Constructs a user JSON.
     *
     * @param accentColour hex colour, with "#" - should be safe to exclude, but preferred to have.
     * @param language ISO 639-3 compatible
     * @param pointsGuildID
     * @param pointsMessages DEPRECATED, do not use.
     * @param pointsVoiceChat DEPRECATED, do not use.
     * @return
     */
    public String userConstructor(String accentColour,
                                  String language,
                                  List<String> pointsGuildID,
                                  List<Integer> pointsMessages,
                                  List<Integer> pointsVoiceChat) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode userNode = objectMapper.createObjectNode();

        userNode.put("userID", userID);
        userNode.put("accentColour", accentColour);
        userNode.put("language", language);
        userNode.set("pointsGuildID", objectMapper.valueToTree(pointsGuildID));
        userNode.set("pointsMessages", objectMapper.valueToTree(pointsMessages));
        userNode.set("pointsVoiceChat", objectMapper.valueToTree(pointsVoiceChat));

        return userNode.toString();
    }

    /**
     * @return Returns a RobinConfiguration containing ALL users. Intended for use with points leaderboards.
     * @throws IOException
     */
    public InputStreamReader getALLUsers() throws IOException {
        URL url;
        InputStreamReader reader;

        String link = Main.getApiPath() + "api/v1/user/";
        try {
            url = new URL(link);
            URLConnection uc = url.openConnection();
            String userpass = Main.getApiUser() + ":" + Main.getApiPassword();
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
            uc.setRequestProperty("Authorization", basicAuth);
            reader = new InputStreamReader(uc.getInputStream());

            return reader;
        }
        catch (IOException e) {
            Main.getLogger().error(e);
            Main.getLogger().error("Unable to read from " + link);
            throw new IOException("Unable to access database.");
        }
    }

    private void fixUserPointsGuildID() throws IOException {
        Map<String, Object> defaults = Main.getUserDefaults();
        if (getPointsGuildID().get(0).equalsIgnoreCase("global")) {
            updateUserPointsGuildID((ArrayList<String>) defaults.get("pointsGuildID"));
        }
    }
}