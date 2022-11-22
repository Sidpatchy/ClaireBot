package com.sidpatchy.clairebot.API;

import com.sidpatchy.Robin.Util.GetYAMLFromURL;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Network.DELETE;
import com.sidpatchy.clairebot.Util.Network.POST;
import com.sidpatchy.clairebot.Util.Network.PUT;

import java.io.IOException;
import java.util.Map;

public class User {
    private final String userID;
    private Map<String, Object> user;

    public User(String userID) {
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
            GetYAMLFromURL yaml = new GetYAMLFromURL();
            user = yaml.getYAMLFromURL(Main.getApiUser(), Main.getApiPassword(), Main.getApiPath() + "api/v1/user/" + userID);
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

    public void createUser(String accentColour,
                           String language) throws IOException {
        POST post = new POST();
        post.postToURL(Main.getApiPath() + "api/v1/user/", userConstructor(accentColour, language));
    }

    public void createUserWithDefaults() {
        Map<String, Object> defaults = Main.getUserDefaults();

        try {
            createUser((String) defaults.get("accentColour"), (String) defaults.get("language"));
        }
        // top 10 bad ideas #1
        catch (Exception ignored) {
            Main.getLogger().error("Unable to create user with defaults.");
        }
        createNewWithDefaults = false; // prevent recursion if ClaireData goes down.
    }

    public void updateUser(String accentColour,
                           String language) throws IOException {
        PUT put = new PUT();
        put.putToURL(Main.getApiPath() + "api/v1/user/" + userID, userConstructor(accentColour, language));
    }

    public void deleteUser() throws IOException {
        DELETE delete = new DELETE();
        delete.deleteToURL(Main.getApiPath() + "api/v1/user/" + userID);
    }

    public String userConstructor(String accentColour,
                                  String language) {
        return "{" +
                "\"userID\":\"" + userID + "\"," +
                "\"accentColour\":\""+ accentColour + "\"," +
                "\"language\":\"" + language + "\"," +

                "}";
    }
}
