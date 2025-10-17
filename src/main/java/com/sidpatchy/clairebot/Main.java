package com.sidpatchy.clairebot;

import com.sidpatchy.Robin.Discord.CommandFactory;
import com.sidpatchy.Robin.Exception.InvalidConfigurationException;
import com.sidpatchy.Robin.File.ResourceLoader;
import com.sidpatchy.Robin.File.RobinConfiguration;
import com.sidpatchy.clairebot.API.APIUser;
import com.sidpatchy.clairebot.API.Guild;
import com.sidpatchy.clairebot.Listener.*;
import com.sidpatchy.clairebot.Listener.Voting.ModerateReactions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.net.URL;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * ClaireBot - Simply the best.
 * Copyright (C) 2021 Sidpatchy
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <<a href="https://www.gnu.org/licenses/">...</a>>.
 *
 * @since April 2020
 * @version 3.4.0-SNAPSHOT
 * @author Sidpatchy
 */
public class Main {

    // Discord API
    private static DiscordApi api;

    private static final long startMillis = System.currentTimeMillis();

    // Related to ClaireData API
    private static String apiPath;
    private static String apiUser;
    private static String apiPassword;

    // Default values for users and guilds when creating them
    private static Map<String, Object> userDefaults;
    private static Map<String, Object> guildDefaults;

    // Various parameters extracted from config files
    private static String color;
    private static String errorColor;
    private static List<String> errorGifs;
    private static Locale fallbackLocale;
    private static List<String> zerfas;
    private static String zerfasEmojiServerID;
    private static String zerfasEmojiID;
    private static List<String> eightBall;
    private static List<String> eightBallRigged;
    private static List<String> claireBotOnTopResponses;
    private static List<String> onTopTriggers;
    private static List<String> plsBanResponses;
    private static List<String> plsBanTriggers;

    // Commands
    private static final Logger logger = LogManager.getLogger(Main.class);

    // Related to configuration files
    private static final String configFile = "config.yml";
    private static final String commandsFile = "commands.yml";
    private static final String translationsPath = "config/translations/";
    private static RobinConfiguration config;
    private static Commands commands;
    private static final Properties buildProperties = new Properties() {{
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("build.properties")) {
            if (input != null) load(input);
            else System.err.println("build.properties missing!");
        } catch (IOException e) { throw new RuntimeException("Failed to load build.properties", e); }
    }};
    private static String buildVersion;
    private static String buildDate;
    private static String github;
    private static String supportServer;
    private static String website;
    private static String documentationWebsite;
    private static String inviteLink;

    static void main(String[] args) throws InvalidConfigurationException {
        logger.info("ClaireBot loading...");

        // Make sure required resources are loaded
        ResourceLoader loader = new ResourceLoader();
        loader.saveResource(configFile, false);
        loader.saveResource(commandsFile, false);
        loadAllBundledTranslations(loader, true); // TODO make this false once language files are stable

        // Init config handlers
        config = new RobinConfiguration("config/" + configFile);

        config.load();

        // Read data from config file
        String token = config.getString("token");
        Integer current_shard = config.getInt("current_shard");
        Integer total_shards = config.getInt("total_shards");
        String video_url = config.getString("video_url");

        extractParametersFromConfig(true);
        loadCommandDefs();

        verifyDatabaseConnectivity();

        api = DiscordLogin(token, current_shard, total_shards);

        if (api == null) {
            System.exit(2);
        }
        else {
            logger.info("Successfully connected to Discord on shard {} with a total shard count of {}", current_shard, total_shards);
        }

        Clockwork.initClockwork();

        // Set the bot's activity
        api.updateActivity("ClaireBot " + buildVersion, video_url);

        // Register slash commands
        registerSlashCommands();

        // Register Command-related listeners
        api.addSlashCommandCreateListener(new SlashCommandCreate());
        api.addSelectMenuChooseListener(new SelectMenuChoose());
        api.addModalSubmitListener(new ModalSubmit());

        // Related to Voting Functions
        api.addReactionAddListener(new ModerateReactions());

        // Misc. Events
        api.addServerJoinListener(new ServerJoin());
        api.addMessageCreateListener(new AntiPhish());
        api.addMessageCreateListener(new MessageCreate());
        api.addButtonClickListener(new ButtonClick());
    }

    // Connect to Discord and create an API object
    private static DiscordApi DiscordLogin(String token, Integer current_shard, Integer total_shards) {
        if (token == null || token.isEmpty()) {
            logger.fatal("Token can't be null or empty. Check your config file!");
            System.exit(1);
        }
        else if (current_shard == null || total_shards == null) {
            logger.fatal("Shard config is empty, check your config file!");
            System.exit(3);
        }

        try {
            // Connect to Discord
            logger.info("Attempting discord login");
            return new DiscordApiBuilder()
                    .setToken(token)
                    .setAllIntents()
                    .setCurrentShard(current_shard)
                    .setTotalShards(total_shards)
                    .login().join();
        }
        catch (Exception e) {
            logger.fatal("Unable to log in to Discord. Aborting startup!", e);
        }
        return null;
    }

    // Extract parameters from the config.yml file, update the config if applicable.
    // todo stop using Robin for this. Switch to standard Java classses.
    @SuppressWarnings("unchecked")
    public static void extractParametersFromConfig(boolean updateOutdatedConfigs) {
        logger.info("Loading configuration files...");

        try {
            apiPath = config.getString("apiPath");
            apiUser = config.getString("apiUser");
            apiPassword = config.getString("apiPassword");
            userDefaults = ((Map<String, Object>) config.getObj("userDefaults"));
            guildDefaults = ((Map<String, Object>) config.getObj("guildDefaults"));
            color = config.getString("color");
            errorColor = config.getString("errorColor");
            errorGifs = config.getList("error_gifs", String.class);
            fallbackLocale = Locale.forLanguageTag(config.getString("fallback_language"));
            zerfas = config.getList("zerfas", String.class);
            zerfasEmojiServerID = String.valueOf(config.getLong("zerfas_emoji_server_id"));
            zerfasEmojiID = String.valueOf(config.getLong("zerfas_emoji_id"));
            eightBall = config.getList("8bResponses", String.class);
            eightBallRigged = config.getList("8bRiggedResponses", String.class);
            claireBotOnTopResponses = config.getList("ClaireBotOnTopResponses", String.class);
            onTopTriggers = config.getList("OnTopTriggers", String.class);
            plsBanResponses = config.getList("PlsBanResponses", String.class);
            plsBanTriggers = config.getList("PlsBanTriggers", String.class);
            buildVersion = buildProperties.getProperty("clairebot.version");
            buildDate = buildProperties.getProperty("clairebot.buildDate");
            github = buildProperties.getProperty("clairebot.github");
            supportServer = buildProperties.getProperty("clairebot.supportServer");
            website = buildProperties.getProperty("clairebot.website");
            documentationWebsite = buildProperties.getProperty("clairebot.documentationWebsite");
            inviteLink = config.getString("clairebot.inviteLink");
        }
        catch (Exception e) {
            logger.error("There was an error while extracting parameters from the config. This isn't fatal but there's a good chance things will be very broken.", e);
        }

    }

    public static void loadCommandDefs() {
        try {
            commands = CommandFactory.loadConfig("config/" + commandsFile, Commands.class);
            logger.warn(commands.getInfo().getName());
            logger.warn(commands.getInfo().getHelp());
        } catch (IOException e) {
            logger.fatal("There was a fatal error while registering slash commands", e);
            throw new RuntimeException(e);
        }
    }

    private static void loadAllBundledTranslations(ResourceLoader loader, boolean replace) {
        String resourceDir = "translations";
        Set<String> resourcePaths = new HashSet<>();
        try {
            ClassLoader cl = Main.class.getClassLoader();
            Enumeration<URL> urls = cl.getResources(resourceDir);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    try {
                        File dir = new File(url.toURI());
                        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
                        if (files != null) {
                            for (File f : files) {
                                resourcePaths.add(resourceDir + "/" + f.getName());
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to enumerate file resources for translations: {}", e.getMessage());
                    }
                } else if ("jar".equals(protocol)) {
                    try {
                        JarURLConnection conn = (JarURLConnection) url.openConnection();
                        try (JarFile jarFile = conn.getJarFile()) {
                            Enumeration<JarEntry> entries = jarFile.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                String name = entry.getName();
                                if (!entry.isDirectory() && name.startsWith(resourceDir + "/") && name.endsWith(".yml")) {
                                    resourcePaths.add(name);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Failed to enumerate JAR resources for translations: {}", e.getMessage());
                    }
                } else {
                    logger.debug("Unsupported classpath URL protocol for translations: {}", protocol);
                }
            }
        } catch (IOException e) {
            logger.warn("Unable to list translation resources: {}", e.getMessage());
        }

        if (resourcePaths.isEmpty()) {
            // Fallback: attempt known default file to ensure at least the template exists on first run
            logger.warn("No translation resources discovered via classpath enumeration. Falling back to default copy of en-US and TEMPLATE if present.");
            String[] fallbacks = new String[] {"translations/lang_en-US.yml", "translations/lang_TEMPLATE.yml"};
            for (String path : fallbacks) {
                try {
                    loader.saveResource(path, replace);
                } catch (Exception e) {
                    logger.debug("Fallback translation '{}' not present in resources: {}", path, e.getMessage());
                }
            }
            return;
        }

        for (String path : resourcePaths) {
            try {
                loader.saveResource(path, replace);
                logger.debug("Ensured translation resource available: {}", path);
            } catch (Exception e) {
                logger.warn("Failed to save translation resource '{}': {}", path, e.getMessage());
            }
        }
    }

    // Handle the registry of slash commands and any errors associated.
    public static void registerSlashCommands() {
        try {
            RegisterSlashCommands.RegisterSlashCommand(api);
            logger.info("Slash commands registered successfully!");
        }
        catch (NullPointerException e) {
            logger.fatal("There was an error while registering slash commands. There's a pretty good chance it's related to an uncaught issue with the commands.yml file.", e);
            logger.fatal("Check your commands.yml file!");
            System.exit(4);
        }
        catch (Exception e) {
            logger.fatal("There was a fatal error while registering slash commands.", e);
            System.exit(5);
        }
    }

    // Verify that the database is online and responding to the bot's queries.
    public static void verifyDatabaseConnectivity() {
        // test APIUser connectivity
        try {
            APIUser api = new APIUser("12345");
            api.getALLUsers();
        } catch (IOException e) {
            logger.error("ClaireBot was unable to access the APIUser table.", e);
        }

        // test Guild connectivity
        try {
            Guild api = new Guild("12345");
            api.getALLGuilds();
        } catch (IOException e) {
            logger.error("ClaireBot was unable to access the Guild table.", e);
        }
    }

    // Getters
    public static String getApiPath() { return apiPath; }

    public static String getApiUser() { return apiUser; }

    public static String getApiPassword() { return apiPassword; }

    public static Map<String, Object> getUserDefaults() { return userDefaults; }

    public static Map<String, Object> getGuildDefaults() { return guildDefaults; }

    public static Color getColor(String userID) {
        if (userID == null) { return Color.decode(color); }

        APIUser user = new APIUser(userID);
        try {
            user.getUser();
            return Color.decode(user.getAccentColour());
        }
        catch (Exception exception) {
            return Color.decode(color);
        }
    }

    public static Color getErrorColor() { return Color.decode(errorColor); }

    public static List<String> getErrorGifs() {
        return errorGifs;
    }

    public static List<String> getEightBall() {
        return eightBall;
    }

    public static List<String> getEightBallRigged() {
        return eightBallRigged;
    }

    public static List<String> getOnTopTriggers() {
        return onTopTriggers;
    }

    public static List<String> getClaireBotOnTopResponses() {
        return claireBotOnTopResponses;
    }

    public static List<String> getPlsBanTriggers() {
        return plsBanTriggers;
    }

    public static List<String> getPlsBanResponses() {
        return plsBanResponses;
    }

    public static List<String> getZerfas() {
        return zerfas;
    }

    public static String getZerfasEmojiServerID() {
        return zerfasEmojiServerID;
    }

    public static String getZerfasEmojiID() {
        return zerfasEmojiID;
    }

    public static String getConfigFile() { return configFile; }

    public static String getCommandsFile() { return "config/" + commandsFile; }

    public static Commands getCommands() {
        return commands;
    }

    public static Logger getLogger() { return logger; }

    public static String getErrorCode(String descriptor) {
        return descriptor + ":" + api.getCurrentShard() + ":" + api.getTotalShards() + ":" + api.getClientId() + ":" + System.currentTimeMillis() / 1000L;
    }

    public static DiscordApi getApi() { return api; }

    public static long getStartMillis() { return startMillis; }

    public static String getTranslationsPath() {
        return translationsPath;
    }

    public static String getBuildVersion() {
        return buildVersion;
    }

    public static String getBuildDate() {
        return buildDate;
    }

    public static String getGithub() {
        return github;
    }

    public static String getSupportServer() {
        return supportServer;
    }

    public static String getWebsite() {
        return website;
    }

    public static String getDocumentationWebsite() {
        return documentationWebsite;
    }

    public static String getInviteLink() {
        return inviteLink;
    }

    public static Locale getFallbackLocale() {
        return fallbackLocale;
    }
}