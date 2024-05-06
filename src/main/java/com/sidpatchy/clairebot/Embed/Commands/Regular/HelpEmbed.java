package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.Robin.Discord.Command;
import com.sidpatchy.clairebot.Commands;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;

public class HelpEmbed {

    private static final Commands commands = Main.getCommands();

    public static EmbedBuilder getHelp(String commandName, String userID) throws FileNotFoundException {
        HashMap<String, Command> allCommands = new HashMap<>();
        HashMap<String, Command> regularCommands = new HashMap<>();

        for (Field field : commands.getClass().getDeclaredFields()) {
            try {
                Command command = (Command) field.get(commands);
                allCommands.put(field.getName(), command);
                regularCommands.put(field.getName(), command);
            } catch (IllegalAccessException e) {
                // todo handle the exception
            }
        }

        if (commandName.equalsIgnoreCase("help")) {
            return buildHelpEmbed(userID, regularCommands);
        } else {
            return buildCommandDetailEmbed(commandName, userID, allCommands);
        }
    }

    private static EmbedBuilder buildHelpEmbed(String userID, HashMap<String, Command> regularCommands) {
        StringBuilder commandsList = new StringBuilder("```");

        for (String commandName : regularCommands.keySet()) {
            if (commandsList.length() > 3) {
                commandsList.append(", ");
            }
            commandsList.append(commandName);
        }

        commandsList.append("```");

        return new EmbedBuilder()
                .setColor(Main.getColor(userID))
                .addField("Commands", commandsList.toString(), false);
    }

    private static EmbedBuilder buildCommandDetailEmbed(String commandName, String userID, HashMap<String, Command> allCommands) {
        Command command = allCommands.get(commandName);

        if (command == null) {
            String errorCode = Main.getErrorCode("help_command");
            Main.getLogger().error("Unable to locate command \"" + commandName + "\" for help command. Error code: " + errorCode);
            return ErrorEmbed.getError(errorCode);
        } else {
            return new EmbedBuilder()
                    .setColor(Main.getColor(userID))
                    .setAuthor(commandName.toUpperCase())
                    .setDescription(command.getOverview().isEmpty() ? command.getHelp() : command.getOverview())
                    .addField("Command", "Usage\n```" + command.getUsage() + "```");
        }
    }
}

