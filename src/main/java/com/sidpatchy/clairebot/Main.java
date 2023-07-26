package com.sidpatchy.clairebot;

import com.sidpatchy.Robin.Discord.ParseCommands;
import com.sidpatchy.Robin.Exception.InvalidConfigurationException;
import com.sidpatchy.Robin.File.ResourceLoader;
import com.sidpatchy.Robin.File.RobinConfiguration;
import com.sidpatchy.clairebot.API.APIUser;
import com.sidpatchy.clairebot.API.Guild;
import com.sidpatchy.clairebot.Listener.*;
import com.sidpatchy.clairebot.Listener.Voting.AddReactions;
import com.sidpatchy.clairebot.Listener.Voting.ModerateReactions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClaireBot - Simply the best.
 * Copyright (C) 2021  Sidpatchy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * @since April 2020
 * @version 3.0.0-alpha
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
    private static String botName;
    private static String color;
    private static String errorColor;
    private static List<Object> errorGifs;
    private static List<Object> zerfas;
    private static String zerfasEmote;
    private static List<Object> eightBall;
    private static List<Object> eightBallRigged;
    private static List<Object> claireBotOnTopResponses;
    private static List<Object> onTopTriggers;

    // Commands
    private static final Logger logger = LogManager.getLogger(Main.class);

    // Related to configuration files
    private static final String configFile = "config.yml";
    private static final String commandsFile = "commands.yml";
    private static RobinConfiguration config;
    private static ParseCommands commands;

    public static List<String> commandList = Arrays.asList("8ball", "avatar", "help", "info", "leaderboard", "level", "poll", "request", "server", "user", "config");

    public static void main(String[] args) throws InvalidConfigurationException {
        logger.info("ClaireBot loading...");

        // Make sure required resources are loaded
        ResourceLoader loader = new ResourceLoader();
        loader.saveResource(configFile, false);
        loader.saveResource(commandsFile, false);

        // Init config handlers
        config = new RobinConfiguration("config/" + configFile);
        commands = new ParseCommands("config/" + commandsFile);

        config.load();

        // Read data from config file
        String token = config.getString("token");
        Integer current_shard = config.getInt("current_shard");
        Integer total_shards = config.getInt("total_shards");
        String video_url = config.getString("video_url");

        extractParametersFromConfig(true);

        verifyDatabaseConnectivity();

        api = DiscordLogin(token, current_shard, total_shards);

        if (api == null) {
            System.exit(2);
        }
        else {
            logger.info("Successfully connected to Discord on shard " + current_shard + " with a total shard count of " + total_shards);
        }

        Clockwork.initClockwork();

        // Set the bot's activity
        api.updateActivity("ClaireBot v3.0.0", video_url);

        // Register slash commands
        registerSlashCommands();

        // Register Command-related listeners
        api.addSlashCommandCreateListener(new SlashCommandCreate());
        api.addSelectMenuChooseListener(new SelectMenuChoose());
        api.addModalSubmitListener(new ModalSubmit());

        // Related to Voting Functions
        api.addMessageCreateListener(new AddReactions());
        api.addReactionAddListener(new ModerateReactions());

        // Misc. Events
        api.addServerJoinListener(new ServerJoin());
        api.addMessageCreateListener(new AntiPhish());
        api.addMessageCreateListener(new MessageCreate());
    }

    // Connect to Discord and create an API object
    private static DiscordApi DiscordLogin(String token, Integer current_shard, Integer total_shards) {
        if (token == null || token.equals("")) {
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
            e.printStackTrace();
            logger.fatal(e.toString());
            logger.fatal("Unable to log in to Discord. Aborting startup!");
        }
        return null;
    }

    // Extract parameters from the config.yml file, update the config if applicable.
    @SuppressWarnings("unchecked")
    public static void extractParametersFromConfig(boolean updateOutdatedConfigs) {
        logger.info("Loading configuration files...");

        try {
            botName = config.getString("botName");
            apiPath = config.getString("apiPath");
            apiUser = config.getString("apiUser");
            apiPassword = config.getString("apiPassword");
            userDefaults = ((Map<String, Object>) config.getObj("userDefaults"));
            guildDefaults = ((Map<String, Object>) config.getObj("guildDefaults"));
            color = config.getString("color");
            errorColor = config.getString("errorColor");
            errorGifs = config.getList("error_gifs");
            zerfas = config.getList("zerfas");
            zerfasEmote = "<:" + config.getString( "zerfas_emote_name") + ":" + config.getLong("zerfas_emote_id") + ">";
            eightBall = config.getList("8bResponses");
            eightBallRigged = config.getList("8bRiggedResponses");
            claireBotOnTopResponses = config.getList("ClaireBotOnTopResponses");
            onTopTriggers = config.getList("OnTopTriggers");
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("There was an error while extracting parameters from the config. This isn't fatal but there's a good chance things will be very broken.");
        }

    }

    // Handle the registry of slash commands and any errors associated.
    public static void registerSlashCommands() {
        try {
            RegisterSlashCommands.RegisterSlashCommand(api);
            logger.info("Slash commands registered successfully!");
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            logger.fatal("There was an error while registering slash commands. There's a pretty good chance it's related to an uncaught issue with the commands.yml file, trying to read all commands and printing out results.");
            for (String s : Main.commandList) {
                logger.fatal(commands.getCommandName(s));
            }
            logger.fatal("If the above list looks incomplete or generates another error, check your commands.yml file!");
            System.exit(4);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.fatal("There was a fatal error while registering slash commands.");
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
            e.printStackTrace();
            logger.error("ClaireBot was unable to access the APIUser table. See previous errors for more details.");
            logger.error("This isn't strictly fatal, but things WILL be very broken.");
        }

        // test Guild connectivity
        try {
            Guild api = new Guild("12345");
            api.getALLGuilds();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("ClaireBot was unable to access the Guild table. See previous errors for more details.");
            logger.error("This isn't strictly fatal, but things WILL be very broken.");
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
        return errorGifs.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public static List<String> getEightBall() {
        return eightBall.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public static List<String> getEightBallRigged() {
        return eightBallRigged.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public static List<String> getOnTopTriggers() {
        return onTopTriggers.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public static List<String> getClaireBotOnTopResponses() {
        return claireBotOnTopResponses.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public static String getConfigFile() { return configFile; }

    public static String getCommandsFile() { return "config/" + commandsFile; }

    public static Logger getLogger() { return logger; }

    public static String getErrorCode(String descriptor) {
        return descriptor + ":" + api.getCurrentShard() + ":" + api.getTotalShards() + ":" + api.getClientId() + ":" + System.currentTimeMillis() / 1000L;
    }

    public static DiscordApi getApi() { return api; }

    public static List<String> getVoteEmoji() { return Arrays.asList("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟", "\uD83D\uDC4D", "\uD83D\uDC4E"); }

    public static long getStartMillis() { return startMillis; }
}
