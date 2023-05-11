package com.sidpatchy.clairebot.API;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sidpatchy.Robin.Exception.InvalidConfigurationException;
import com.sidpatchy.Robin.File.RobinConfiguration;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Network.DELETE;
import com.sidpatchy.clairebot.Util.Network.POST;
import com.sidpatchy.clairebot.Util.Network.PUT;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

public class APIUser {
    private final String userID;
    private Map<String, Object> user;

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
            RobinConfiguration yaml = new RobinConfiguration();
            yaml.loadFromURL(Main.getApiUser(), Main.getApiPassword(), Main.getApiPath() + "api/v1/user/" + userID);
            fixUserPointsGuildID();
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
    }

    public String getAccentColour() {
        return (String) user.get("accentColour");
    }

    public String getLanguage() {
        return (String) user.get("language");
    }

    public ArrayList<String> getPointsGuildID() {
        return (ArrayList<String>) user.get("pointsGuildID");
    }

    public ArrayList<Integer> getPointsMessages() {
        return (ArrayList<Integer>) user.get("pointsMessages");
    }

    public ArrayList<Integer> getPointsVoiceChat() {
        return (ArrayList<Integer>) user.get("pointsVoiceChat");
    }

    public void createUser(String accentColour,
                           String language,
                           ArrayList<String> pointsGuildID,
                           ArrayList<Integer> pointsMessages,
                           ArrayList<Integer> pointsVoiceChat) throws IOException {
        POST post = new POST();
        post.postToURL(Main.getApiPath() + "api/v1/user/", userConstructor(accentColour, language, pointsGuildID, pointsMessages, pointsVoiceChat));
    }

    public void createUserWithDefaults() {
        Map<String, Object> defaults = Main.getUserDefaults();

        try {
            createUser(
                    (String) defaults.get("accentColour"),
                    (String) defaults.get("language"),
                    (ArrayList<String>) defaults.get("pointsGuildID"),
                    (ArrayList<Integer>) defaults.get("pointsMessages"),
                    (ArrayList<Integer>) defaults.get("pointsVoiceChat")
            );
        }
        // top 10 bad ideas #1
        catch (Exception ignored) {
            ignored.printStackTrace();
            Main.getLogger().error("Unable to create user with defaults.");
        }
        createNewWithDefaults = false; // prevent recursion if ClaireData goes down.
    }

    public void updateUser(String accentColour,
                           String language,
                           ArrayList<String> pointsGuildID,
                           ArrayList<Integer> pointsMessages,
                           ArrayList<Integer> pointsVoiceChat) throws IOException {
        PUT put = new PUT();
        put.putToURL(Main.getApiPath() + "api/v1/user/" + userID, userConstructor(accentColour, language, pointsGuildID, pointsMessages, pointsVoiceChat));
    }

    public void updateUserColour(String accentColour) throws IOException {
        updateUser(accentColour,
                getLanguage(),
                getPointsGuildID(),
                getPointsMessages(),
                getPointsVoiceChat()
        );
    }

    public void updateUserLanguage(String languageString) throws IOException {
        updateUser(getAccentColour(),
                languageString,
                getPointsGuildID(),
                getPointsMessages(),
                getPointsVoiceChat());
    }

    public void deleteUser() throws IOException {
        DELETE delete = new DELETE();
        delete.deleteToURL(Main.getApiPath() + "api/v1/user/" + userID);
    }

    public String userConstructor(String accentColour,
                                  String language,
                                  ArrayList<String> pointsGuildID,
                                  ArrayList<Integer> pointsMessages,
                                  ArrayList<Integer> pointsVoiceChat) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode userNode = objectMapper.createObjectNode();

        userNode.put("userID", userID);
        userNode.put("accentColour", accentColour);
        userNode.put("language", language);
        userNode.put("pointsGuildID", objectMapper.valueToTree(pointsGuildID));
        userNode.put("pointsMessages", objectMapper.valueToTree(pointsMessages));
        userNode.put("pointsVoiceChat", objectMapper.valueToTree(pointsVoiceChat));

        return userNode.toString();
    }

    /**
     * @return Returns a RobinConfiguration containing ALL users. Intended for use with points leaderboards.
     * @throws IOException
     */
    public InputStreamReader getALLUsers() {
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
            return null;
        }
    }

    private void fixUserPointsGuildID() throws IOException {
        Map<String, Object> defaults = Main.getUserDefaults();
        if (getPointsGuildID().get(0).equalsIgnoreCase("global")) {
            updateUser(
                    getAccentColour(),
                    getLanguage(),
                    (ArrayList<String>) defaults.get("pointsGuildID"),
                    getPointsMessages(),
                    getPointsVoiceChat()
            );
        }
    }
}
