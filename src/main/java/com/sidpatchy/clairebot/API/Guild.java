package com.sidpatchy.clairebot.API;

import com.sidpatchy.Robin.File.RobinConfiguration;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.Network.DELETE;
import com.sidpatchy.clairebot.Util.Network.POST;
import com.sidpatchy.clairebot.Util.Network.PUT;
import com.sidpatchy.clairebot.Util.Network.UrlBuilder;

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
            guild.loadFromURL(Main.getApiUser(), Main.getApiPassword(), UrlBuilder.buildUrl(Main.getApiPath(), "api/v1/guild", guildID));
        }
        catch (Exception e) {
            if (!createNewWithDefaults) {
                throw new IOException("Unable to load guild " + guildID + " from ClaireData", e);
            }
            // An absent guild may need creating; if ClaireData is offline, creation or the retry
            // must fail rather than leaving an empty configuration to be read as valid data.
            createNewWithDefaults = false;
            createGuildWithDefaults();
            try {
                getGuild();
            } catch (IOException retryFailure) {
                throw new IOException("Unable to load guild " + guildID + " after creating it", retryFailure);
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
        Object value = guild.getObj("enforceServerLanguage");
        // Older guild records may lack this setting; the default is not to override user language.
        return Boolean.TRUE.equals(value) || (value instanceof String text && Boolean.parseBoolean(text));
    }

    public String getLocale() {
        return guild.getString("locale");
    }

    public void createGuild(String requestsChannelID,
                            String moderatorMessagesChannelID,
                            boolean enforceServerLanguage,
                            String locale) throws IOException {
        POST post = new POST();
        post.postToURL(UrlBuilder.buildUrl(Main.getApiPath(), "api/v1/guild"), guildConstructor(
                requestsChannelID,
                moderatorMessagesChannelID,
                enforceServerLanguage,
                locale
        ));
    }

    public void createGuildWithDefaults() throws IOException {
        Map<String, Object> defaults = Main.getGuildDefaults();
        createGuild((String) defaults.get("requestsChannelID"),
                (String) defaults.get("moderatorMessagesChannelID"),
                (boolean) defaults.get("enforceServerLanguage"),
                (String) defaults.get("locale"));
    }

    public void updateGuild(String requestsChannelID,
                            String moderatorMessagesChannelID,
                            boolean enforceServerLanguage,
                            String locale) throws IOException {
        PUT put = new PUT();
        put.putToURL(UrlBuilder.buildUrl(Main.getApiPath(), "api/v1/guild", guildID), guildConstructor(
                requestsChannelID,
                moderatorMessagesChannelID,
                enforceServerLanguage,
                locale
        ));
    }

    public void updateRequestsChannelID(String requestsChannelID) throws IOException {
        updateGuild(
                requestsChannelID,
                getModeratorMessagesChannelID(),
                isEnforceSeverLanguage(),
                getLocale()
        );
    }

    public void updateModeratorMessagesChannelID(String moderatorMessagesChannelID) throws IOException {
        updateGuild(
                getRequestsChannelID(),
                moderatorMessagesChannelID,
                isEnforceSeverLanguage(),
                getLocale()
        );
    }

    public void updateEnforceServerLanguage(boolean enforceServerLanguage) throws IOException {
        updateGuild(
                getRequestsChannelID(),
                getModeratorMessagesChannelID(),
                enforceServerLanguage,
                getLocale()
        );
    }

    public void updateLocale(String locale) throws IOException {
        updateGuild(
                getRequestsChannelID(),
                getModeratorMessagesChannelID(),
                isEnforceSeverLanguage(),
                locale
        );
    }

    public void deleteGuild() throws IOException {
        DELETE delete = new DELETE();
        delete.deleteToURL(UrlBuilder.buildUrl(Main.getApiPath(), "api/v1/guild", guildID));
    }

    public String guildConstructor(String requestsChannelID,
                                   String moderatorMessagesChannelID,
                                   boolean enforceServerLanguage,
                                   String locale) {
        return "{" +
                "\"guildID\":\"" + guildID + "\"," +
                "\"requestsChannelID\":\""+ requestsChannelID + "\"," +
                "\"moderatorMessagesChannelID\":\"" + moderatorMessagesChannelID + "\"," +
                "\"enforceServerLanguage\":\"" + enforceServerLanguage + "\"," +
                "\"locale\":\"" + locale + "\"" +
                "}";
    }

    /**
     * @return Returns a RobinConfiguration containing ALL users. Intended for use with points leaderboards.
     * @throws IOException
     */
    public InputStreamReader getALLGuilds() throws IOException {
        URL url;
        InputStreamReader reader;

        String link = UrlBuilder.buildUrl(Main.getApiPath(), "api/v1/guild");
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
