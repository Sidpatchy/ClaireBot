package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

public class HelpEmbed {
    public static EmbedBuilder getHelp(String commandName) throws FileNotFoundException {
        List<String> regularCommandsList = Arrays.asList("8ball", "avatar", "help", "info", "leaderboard", "level", "poll", "servers", "user");
        List<String> musicCommandsList = Arrays.asList("connect", "leave", "pause", "play", "previous", "queue", "repeat", "skip", "stop");

        // Create HashMaps for help command
        HashMap<String, HashMap<String, String>> allCommands = new HashMap<String, HashMap<String, String>>();
        HashMap<String, HashMap<String, String>> regularCommands = new HashMap<String, HashMap<String, String>>();
        HashMap<String, HashMap<String, String>> musicCommands = new HashMap<String, HashMap<String, String>>();

        for (String s : regularCommandsList) {
            regularCommands.put(s, ParseCommands.get(s));
        }
        for (String s : musicCommandsList) {
            musicCommands.put(s, ParseCommands.get(s));
        }

        allCommands.putAll(regularCommands);
        allCommands.putAll(musicCommands);

        // Commands list
        if (commandName.equalsIgnoreCase("help")) {
            StringBuilder glob = new StringBuilder("```");
            for (String s : regularCommandsList) {
                if (glob.toString().equalsIgnoreCase("```")) {
                    glob.append(ParseCommands.getCommandName(s));
                }
                else {
                    glob.append(", ")
                            .append(ParseCommands.getCommandName(s));
                }
            }
            glob.append("```");

            StringBuilder mus = new StringBuilder("```");
            for (String s : musicCommandsList) {
                if (mus.toString().equalsIgnoreCase("```")) {
                    mus.append(ParseCommands.getCommandName(s));
                }
                else {
                    mus.append(", ")
                            .append(ParseCommands.getCommandName(s));
                }
            }
            mus.append("```");

            return new EmbedBuilder()
                    .setColor(Main.getColor())
                    .addField("Commands", glob.toString(), false)
                    .addField("Music", mus.toString(), false);
        }
        // Command details
        else {
            if (allCommands.get(commandName) == null) {
                String errorCode = Main.getErrorCode("help_command");
                Main.getLogger().error("Unable to locate command \"" + commandName + "\" for help command. Error code: " + errorCode);
                return ErrorEmbed.getError(errorCode);
            }
            else {
                return new EmbedBuilder()
                        .setColor(Main.getColor())
                        .setAuthor(commandName)
                        .setDescription(allCommands.get(commandName).get("help"))
                        .addField("Command", "Usage\n" + "```" + allCommands.get(commandName).get("usage") + "```");
            }
        }
    }
}
