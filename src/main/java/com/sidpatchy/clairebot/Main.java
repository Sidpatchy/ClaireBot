package com.sidpatchy.clairebot;

import com.sidpatchy.clairebot.File.ConfigReader;
import com.sidpatchy.clairebot.File.ParseCommands;
import com.sidpatchy.clairebot.File.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.FileNotFoundException;
import java.util.Arrays;
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
 * @version 3.0-PRE-ALPHA
 * @author Sidpatchy
 */
public class Main {

    // Various parameters extracted from config files
    private String botName;
    private List<String> zerfas;
    private String zerfasEmote;
    private List<String> eightBall;
    private List<String> eightBallRigged;
    private List<String> claireBotOnTopResponses;
    private List<String> onTopTriggers;

    private static final Logger logger = LogManager.getLogger(Main.class);

    // Related to configuration files
    private static final ConfigReader config = new ConfigReader();
    private static final String configFile = "config.yml";
    private static final String commandsFile = "commands.yml";

    public static List<String> commandList = Arrays.asList("8ball", "avatar", "help", "info", "leaderboard", "level", "poll", "servers", "user", "connect", "leave", "pause", "play", "previous", "queue", "repeat", "skip", "stop");

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

        DiscordApi api = DiscordLogin(token, current_shard, total_shards);

        if (api == null) {
            System.exit(2);
        }
        else {
            logger.info("Successfully connected to Discord on shard " + current_shard + " with a total shard count of " + total_shards);
        }

        // Set the bot's activity
        api.updateActivity("ClaireBot v3.0-PRE-ALPHA", video_url);

        // Register slash commands
        try {
            RegisterSlashCommands.RegisterSlashCommand(api);
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
            logger.fatal(e.toString());
            logger.fatal("Unable to log in to Discord. Aborting startup!");
        }
        return null;
    }

    public boolean extractParametersFromConfig(boolean UpdateOutdatedConfigs) {
        logger.info("Loading configuration files...");

        boolean extractionSuccessful = true;

        try {
            botName = config.getString(configFile, "botName");
            zerfas = config.getList(configFile, "zerfas");
            zerfasEmote = "<:" + config.getString(configFile, "zerfas_emote_name") + ":" + config.getString(configFile, "zerfas_emote_id") + ">";
            eightBall = config.getList(configFile, "8bResponses");
            eightBallRigged = config.getList(configFile, "8bRiggedResponses");
            claireBotOnTopResponses = config.getList(configFile, "ClaireBotOnTopResponses");
            onTopTriggers = config.getList(configFile, "OnTopTriggers");
        }
        catch (Exception e) {
            logger.error(e.toString());
            logger.error("There was an error while extracting parameters from the config. This isn't fatal but there's a good chance things will be broken.");
            extractionSuccessful = false;
        }

        return extractionSuccessful;
    }

    public static String getConfigFile() {
        return configFile;
    }

    public static String getCommandsFile() {
        return commandsFile;
    }

    public static Logger getLogger() {
        return logger;
    }
}
