package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.Robin.Discord.Command;
import com.sidpatchy.clairebot.Commands;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Map;

public class HelpEmbed {

    public static EmbedBuilder getHelp(LanguageManager languageManager, String commandName, String userID) {
        String commandsLangString = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.HelpEmbed.Commands");
        String usageLangString = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.HelpEmbed.Usage");
        languageManager.addContext(ContextManager.ContextType.GENERIC, "commandname", commandName);

        Commands canonicalCommands = Main.getCommands();
        Commands localizedCommands = Main.getCommands(languageManager.getEffectiveLocale());
        Map<String, Command> canonicalByKey = canonicalCommands.getCommandsByKey();
        Map<String, Command> localizedByKey = localizedCommands.getCommandsByKey();

        if (commandName.equalsIgnoreCase(canonicalCommands.getHelp().getName())) {
            return buildHelpEmbed(userID, commandsLangString, canonicalByKey);
        }

        for (Map.Entry<String, Command> entry : canonicalByKey.entrySet()) {
            if (entry.getValue().getName().equalsIgnoreCase(commandName)) {
                Command localized = localizedByKey.getOrDefault(entry.getKey(), entry.getValue());
                return buildCommandDetailEmbed(entry.getValue(), localized, userID, commandsLangString, usageLangString);
            }
        }

        return buildUnknownCommandEmbed(languageManager);
    }

    private static EmbedBuilder buildHelpEmbed(String userID, String commandsLangString, Map<String, Command> commandsByKey) {
        StringBuilder commandsList = new StringBuilder("```");

        for (Command command : commandsByKey.values()) {
            if (commandsList.length() > 3) {
                commandsList.append(", ");
            }
            commandsList.append(command.getName());
        }

        commandsList.append("```");

        return new EmbedBuilder()
                .setColor(Main.getColor(userID))
                .addField(commandsLangString, commandsList.toString(), false);
    }

    private static EmbedBuilder buildCommandDetailEmbed(Command canonicalCommand, Command localizedCommand,
                                                        String userID, String commandsLangString, String usageLangString) {
        return new EmbedBuilder()
                .setColor(Main.getColor(userID))
                .setAuthor(canonicalCommand.getName().toUpperCase())
                .setDescription(localizedCommand.getHelp())
                .addField(commandsLangString, usageLangString + "\n```" + localizedCommand.getUsage() + "```", false);
    }

    private static EmbedBuilder buildUnknownCommandEmbed(LanguageManager languageManager) {
        String errorCode = Main.getErrorCode("help_command");
        languageManager.addContext(ContextManager.ContextType.GENERIC, "errorcode", errorCode);
        String errorLangString = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.HelpEmbed.Error");
        Main.getLogger().error("{}", errorLangString);
        return ErrorEmbed.getError(languageManager, errorCode);
    }
}
