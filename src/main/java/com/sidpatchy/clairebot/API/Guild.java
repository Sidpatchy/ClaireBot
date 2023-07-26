package com.sidpatchy.clairebot.API;

import com.sidpatchy.Robin.File.RobinConfiguration;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Network.DELETE;
import com.sidpatchy.clairebot.Util.Network.POST;
import com.sidpatchy.clairebot.Util.Network.PUT;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Map;

public class Guild {
    private final String guildID;
    RobinConfiguration guild = new RobinConfiguration();

    public Guild(String guildID) {
        this.guildID = guildID;
    }
    private boolean createNewWithDefaults = true;

    /**
     * Gets guild info from database. Must always be called before attempting to access guild data.
     *
     * @throws IOException if there is any error, likely the guild doesn't yet exist.
     */
    public void getGuild() throws IOException {
        try {
            guild.loadFromURL(Main.getApiUser(), Main.getApiPassword(), Main.getApiPath() + "api/v1/guild/" + guildID);
        }
        catch (Exception e) {
            if (createNewWithDefaults) {
                createGuildWithDefaults();
                createNewWithDefaults = false; // prevent recursion if ClaireData goes down.
                try {
                    getGuild();
                }
                catch (Exception e2) {
                    throw new IOException("It is reasonably likely that the database has gone down.");
                }
            }
        }
    }

    public String getRequestsChannelID() {
        return guild.getString("requestsChannelID");
    }

    public String getModeratorMessagesChannelID() {
        return guild.getString("moderatorMessagesChannelID");
    }

    public boolean isEnforceSeverLanguage() {
        return (boolean) guild.getObj("enforceServerLanguage");
    }

    public void createGuild(String requestsChannelID,
                            String moderatorMessagesChannelID,
                            boolean enforceServerLanguage) throws IOException {
        POST post = new POST();
        post.postToURL(Main.getApiPath() + "api/v1/guild/", guildConstructor(
                requestsChannelID,
                moderatorMessagesChannelID,
                enforceServerLanguage
        ));
    }

    public void createGuildWithDefaults() {
        Map<String, Object> defaults = Main.getGuildDefaults();
        Main.getLogger().warn(Main.getUserDefaults());
        try {
            createGuild((String) defaults.get("requestsChannelID"),
                    (String) defaults.get("moderatorMessagesChannelID"),
                    (boolean) defaults.get("enforceServerLanguage"));
        }
        // top 10 bad ideas #1
        catch (Exception ignored) {
            Main.getLogger().error("Unable to create user with defaults.");
        }
    }

    public void updateGuild(String requestsChannelID,
                            String moderatorMessagesChannelID,
                            boolean enforceServerLanguage) throws IOException {
        PUT put = new PUT();
        put.putToURL(Main.getApiPath() + "api/v1/guild/" + guildID, guildConstructor(
                requestsChannelID,
                moderatorMessagesChannelID,
                enforceServerLanguage
        ));
    }

    public void updateRequestsChannelID(String requestsChannelID) throws IOException {
        updateGuild(
                requestsChannelID,
                getModeratorMessagesChannelID(),
                isEnforceSeverLanguage()
        );
    }

    public void updateModeratorMessagesChannelID(String moderatorMessagesChannelID) throws IOException {
        updateGuild(
                getRequestsChannelID(),
                moderatorMessagesChannelID,
                isEnforceSeverLanguage()
        );
    }

    public void updateEnforceServerLanguage(boolean enforceServerLanguage) throws IOException {
        updateGuild(
                getRequestsChannelID(),
                getModeratorMessagesChannelID(),
                enforceServerLanguage
        );
    }

    public void deleteGuild() throws IOException {
        DELETE delete = new DELETE();
        delete.deleteToURL(Main.getApiPath() + "api/v1/guild/" + guildID);
    }

    public String guildConstructor(String requestsChannelID,
                                   String moderatorMessagesChannelID,
                                   boolean enforceServerLanguage) {
        return "{" +
                "\"guildID\":\"" + guildID + "\"," +
                "\"requestsChannelID\":\""+ requestsChannelID + "\"," +
                "\"moderatorMessagesChannelID\":\"" + moderatorMessagesChannelID + "\"," +
                "\"enforceServerLanguage\":\"" + enforceServerLanguage + "\"" +
                "}";
    }

    /**
     * @return Returns a RobinConfiguration containing ALL users. Intended for use with points leaderboards.
     * @throws IOException
     */
    public InputStreamReader getALLGuilds() throws IOException {
        URL url;
        InputStreamReader reader;

        String link = Main.getApiPath() + "api/v1/guild/";
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
}
