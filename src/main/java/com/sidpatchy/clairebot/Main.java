package com.sidpatchy.clairebot;

import com.sidpatchy.clairebot.File.ConfigReader;
import com.sidpatchy.clairebot.File.ParseCommands;
import com.sidpatchy.clairebot.File.ResourceLoader;
import com.sidpatchy.clairebot.Listener.AntiPhish;
import com.sidpatchy.clairebot.Listener.ServerJoin;
import com.sidpatchy.clairebot.Listener.SlashCommand.Music.Connect;
import com.sidpatchy.clairebot.Listener.SlashCommand.Music.Leave;
import com.sidpatchy.clairebot.Listener.SlashCommand.Music.Pause;
import com.sidpatchy.clairebot.Listener.SlashCommand.Music.Play;
import com.sidpatchy.clairebot.Listener.SlashCommand.Regular.*;
import com.sidpatchy.clairebot.Listener.Voting.AddReactions;
import com.sidpatchy.clairebot.Listener.Voting.ModerateReactions;
import com.sidpatchy.clairebot.Util.Music.PlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    // Various parameters extracted from config files
    private static String botName;
    private static String color;
    private static String errorColor;
    private static boolean musicBotEnabled;
    private static List<String> errorGifs;
    private static List<String> zerfas;
    private static String zerfasEmote;
    private static List<String> eightBall;
    private static List<String> eightBallRigged;
    private static List<String> claireBotOnTopResponses;
    private static List<String> onTopTriggers;

    // Commands
    private static HashMap<String, String> helpCommand;

    private static final Logger logger = LogManager.getLogger(Main.class);

    // Related to configuration files
    private static final ConfigReader config = new ConfigReader();
    private static final String configFile = "config.yml";
    private static final String commandsFile = "commands.yml";

    public static List<String> commandList = Arrays.asList("8ball", "avatar", "help", "info", "leaderboard", "level", "poll", "server", "user", "connect", "leave", "pause", "play", "previous", "queue", "repeat", "skip", "stop");

    public static void main(String[] args) throws FileNotFoundException {
        logger.info("ClaireBot loading...");

        // Make sure require resources are loaded
        ResourceLoader loader = new ResourceLoader();
        loader.saveResource(configFile, false);
        loader.saveResource(commandsFile, false);

        // Read data from config file
        String token = config.getString(configFile, "token");
        Integer current_shard = config.getInt(configFile, "current_shard");
        Integer total_shards = config.getInt(configFile, "total_shards");
        String video_url = config.getString(configFile, "video_url");

        extractParametersFromConfig(true);

        api = DiscordLogin(token, current_shard, total_shards);

        if (api == null) {
            System.exit(2);
        }
        else {
            logger.info("Successfully connected to Discord on shard " + current_shard + " with a total shard count of " + total_shards);
        }

        Clockwork.initClockwork();

        if (musicBotEnabled()) { PlayerManager.initManager(); }

        // Set the bot's activity
        api.updateActivity("ClaireBot v3.0.0-alpha", video_url);

        // Register slash commands
        //registerSlashCommands();

        // Register SlashCommand listeners
        api.addSlashCommandCreateListener(new EightBall());
        api.addSlashCommandCreateListener(new Avatar());
        api.addSlashCommandCreateListener(new Help());
        api.addSlashCommandCreateListener(new Poll());
        api.addSlashCommandCreateListener(new ServerInfo());

        api.addSlashCommandCreateListener(new UserInfo());

        // Related to music commands
        if (musicBotEnabled()) {
            api.addSlashCommandCreateListener(new Connect());
            api.addSlashCommandCreateListener(new Leave());
            api.addSlashCommandCreateListener(new Pause());
            api.addSlashCommandCreateListener(new Play());
        }

        // Related to Voting Functions
        api.addMessageCreateListener(new AddReactions());
        api.addReactionAddListener(new ModerateReactions());

        // Misc. Events
        api.addServerJoinListener(new ServerJoin());
        api.addMessageCreateListener(new AntiPhish());
    }

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

    public static void extractParametersFromConfig(boolean updateOutdatedConfigs) {
        logger.info("Loading configuration files...");

        try {
            botName = config.getString(configFile, "botName");
            color = config.getString(configFile, "color");
            errorColor = config.getString(configFile, "errorColor");
            musicBotEnabled = config.getBool(configFile, "music_bot_enabled");
            errorGifs = config.getList(configFile, "error_gifs");
            zerfas = config.getList(configFile, "zerfas");
            zerfasEmote = "<:" + config.getString(configFile, "zerfas_emote_name") + ":" + config.getLong(configFile, "zerfas_emote_id") + ">";
            eightBall = config.getList(configFile, "8bResponses");
            eightBallRigged = config.getList(configFile, "8bRiggedResponses");
            claireBotOnTopResponses = config.getList(configFile, "ClaireBotOnTopResponses");
            onTopTriggers = config.getList(configFile, "OnTopTriggers");

            // commands
            helpCommand = ParseCommands.get("help");
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("There was an error while extracting parameters from the config. This isn't fatal but there's a good chance things will be very broken.");
        }

    }

    public static void registerSlashCommands() throws FileNotFoundException {
        try {
            RegisterSlashCommands.RegisterSlashCommand(api);
            logger.info("Slash commands registered successfully!");
        }
        catch (NullPointerException e) {
            logger.fatal(e.toString());
            logger.fatal("There was an error while registering slash commands. There's a pretty good chance it's related to an uncaught issue with the commands.yml file, trying to read all commands and printing out results.");
            for (String s : Main.commandList) {
                logger.fatal(ParseCommands.getCommandName(s));
            }
            logger.fatal("If the above list looks incomplete or generates another error, check your commands.yml file!");
            System.exit(4);
        }
        catch (Exception e) {
            logger.fatal(e.toString());
            logger.fatal("There was an error while registering slash commands.");
            System.exit(5);
        }
    }

    public static Color getColor() { return Color.decode(color); }

    public static Color getErrorColor() { return Color.decode(errorColor); }

    public static boolean musicBotEnabled() { return musicBotEnabled; }

    public static List<String> getErrorGifs() { return errorGifs; }

    public static List<String> getEightBall() { return eightBall; }

    public static List<String> getEightBallRigged() { return eightBallRigged; }

    public static List<String> getOnTopTriggers() { return onTopTriggers; }

    public static String getConfigFile() { return configFile; }

    public static String getCommandsFile() { return commandsFile; }

    public static Logger getLogger() { return logger; }

    public static String getErrorCode(String descriptor) {
        return descriptor + ":" + api.getCurrentShard() + ":" + api.getTotalShards() + ":" + api.getClientId() + ":" + System.currentTimeMillis() / 1000L;
    }

    public static DiscordApi getApi() { return api; }

    public static List<String> getVoteEmoji() { return Arrays.asList("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟", "\uD83D\uDC4D", "\uD83D\uDC4E"); }

    // Commands
    public static HashMap<String, String> getHelpCommand() { return helpCommand; }
}
