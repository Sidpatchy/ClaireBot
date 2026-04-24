package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.Robin.Discord.Command;
import com.sidpatchy.clairebot.Commands;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class HelpEmbed {

    private static final Commands commands = Main.getCommands();
    private static String commandsLangString;
    private static String usageLangString;

    public static EmbedBuilder getHelp(LanguageManager languageManager, String commandName, String userID) throws FileNotFoundException {
        // Language Strings
        commandsLangString = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.HelpEmbed.Commands");
        usageLangString = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.HelpEmbed.Usage");
        languageManager.addContext(ContextManager.ContextType.GENERIC, "commandname", commandName);

        HashMap<String, Command> allCommands = new HashMap<>();
        HashMap<String, Command> regularCommands = new HashMap<>();

        for (Command command : commands.getAllCommands()) {
            allCommands.put(command.getName(), command);
            regularCommands.put(command.getName(), command);
        }

        if (commandName.equalsIgnoreCase("help")) {
            return buildHelpEmbed(userID, regularCommands);
        } else {
            return buildCommandDetailEmbed(commandName, userID, allCommands, languageManager);
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
                .addField(commandsLangString, commandsList.toString(), false);
    }

    private static EmbedBuilder buildCommandDetailEmbed(String commandName, String userID, HashMap<String, Command> allCommands, LanguageManager languageManager) {
        Command command = allCommands.get(commandName);

        if (command == null) {
            String errorCode = Main.getErrorCode("help_command");
            languageManager.addContext(ContextManager.ContextType.GENERIC, "errorcode", errorCode);
            String errorLangString = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.HelpEmbed.Error");
            Main.getLogger().error(errorLangString);
            return ErrorEmbed.getError(languageManager, errorCode);
        } else {
            return new EmbedBuilder()
                    .setColor(Main.getColor(userID))
                    .setAuthor(commandName.toUpperCase())
                    .setDescription(command.getOverview().isEmpty() ? command.getHelp() : command.getOverview())
                    .addField(commandsLangString, usageLangString + "\n```" + command.getUsage() + "```");
        }
    }
}

