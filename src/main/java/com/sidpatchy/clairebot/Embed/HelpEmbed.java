package com.sidpatchy.clairebot.Embed;

import com.sidpatchy.clairebot.File.ParseCommands;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

public class HelpEmbed {
    public static EmbedBuilder getHelp(String commandName) throws FileNotFoundException {
        List<String> regularCommandsList = Arrays.asList("8ball", "avatar", "help", "info", "leaderboard", "level", "poll", "servers", "user");
        List<String> musicCommandsList = Arrays.asList("connect", "leave", "pause", "play", "previous", "queue", "repeat", "skip", "stop");

        // Create HashMaps for help command
        HashMap<String, List<String>> regularCommands = new HashMap<String, List<String>>();
        HashMap<String, List<String>> musicCommands = new HashMap<String, List<String>>();

        for (String s : regularCommandsList) {
            regularCommands.put(ParseCommands.getCommandName(s), Arrays.asList(ParseCommands.getCommandUsage(s), ParseCommands.getCommandHelp(s)));
        }
        for (String s : musicCommandsList) {
            musicCommands.put(ParseCommands.getCommandName(s), Arrays.asList(ParseCommands.getCommandUsage(s), ParseCommands.getCommandHelp(s)));
        }

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
                    .setColor(Color.decode("#3498db"))
                    .addField("Commands", glob.toString(), false)
                    .addField("Music", mus.toString(), false);
        }
        // Command details
        else {
            List<String> info;
            try {
                info = regularCommands.get(commandName);
            }
            catch (Exception ignored) {
                info = musicCommands.get(commandName);
            }

            if (info == null) {
                String errorCode = Main.getErrorCode("help_command");
                Main.getLogger().error("Unable to locate command \"" + commandName + "\" for help command. Error code: " + errorCode);
                return ErrorEmbed.getError(errorCode);
            }
            else {
                return new EmbedBuilder()
                        .setColor(Color.decode("#3498db"))
                        .setAuthor(commandName)
                        .setDescription(info.get(1))
                        .addField("Command", "Usage\n" + "```" + info.get(0) + "```");
            }
        }
    }
}
