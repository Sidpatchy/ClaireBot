package com.sidpatchy.clairebot;

import com.sidpatchy.clairebot.File.ParseCommands;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Also delete them too!
 */
public class RegisterSlashCommands {

    public static void DeleteSlashCommands (DiscordApi api) {
        api.bulkOverwriteGlobalApplicationCommands(List.of()).join();
    }

    /**
     * Register slash commands while feeling like you're doing it wrong no matter how you do it!
     *
     * Only called on startup.
     *
     * @param api pass API into function
     */
    public static void RegisterSlashCommand(DiscordApi api) {

        // Create the command list in the help command without repeating the same thing 50 million times.
        ArrayList<SlashCommandOptionChoice> helpCommandOptions = new ArrayList<>();
        for (String s : Main.commandList) {
            helpCommandOptions.add(SlashCommandOptionChoice.create(ParseCommands.getCommandName(s), ParseCommands.getCommandName(s)));
        }

        api.bulkOverwriteGlobalApplicationCommands(Arrays.asList(
                // Regular commands
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("8ball")).setDescription(ParseCommands.getCommandHelp("8ball")).addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "query", "The question you wish to ask.", true)),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("avatar")).setDescription(ParseCommands.getCommandHelp("avatar")).addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false)),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("help")).setDescription(ParseCommands.getCommandHelp("help"))
                        .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "command-name", "Command to get more info on", false, helpCommandOptions)),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("info")).setDescription(ParseCommands.getCommandHelp("info")),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("leaderboard")).setDescription(ParseCommands.getCommandHelp("leaderboard")).addOption(SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "global", "Get the global leaderboard?", false)),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("level")).setDescription(ParseCommands.getCommandHelp("level")).addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false)),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("poll")).setDescription(ParseCommands.getCommandHelp("poll"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "question", "Question to ask", true))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "allow-multiple-choices", "Whether multiple choices should be enabled.", false))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-1", "Custom choice"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-2", "Custom choice"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-3", "Custom choice"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-4", "Custom choice"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-5", "Custom choice"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-6", "Custom choice"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-7", "Custom choice"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-8", "Custom choice"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-9", "Custom choice")),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("servers")).setDescription(ParseCommands.getCommandHelp("servers")),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("user")).setDescription(ParseCommands.getCommandHelp("user")).addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false)),

                // Music commands
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("connect")).setDescription(ParseCommands.getCommandHelp("connect")),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("leave")).setDescription(ParseCommands.getCommandHelp("leave")),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("pause")).setDescription(ParseCommands.getCommandHelp("pause")),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("play")).setDescription(ParseCommands.getCommandHelp("play")).addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "query", "A link or name to search for.", false)),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("previous")).setDescription(ParseCommands.getCommandHelp("previous")),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("queue")).setDescription(ParseCommands.getCommandHelp("queue")).addOption(SlashCommandOption.create(SlashCommandOptionType.DECIMAL, "number-of-tracks", "The number of tracks to show", false)),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("repeat")).setDescription(ParseCommands.getCommandHelp("repeat"))
                        .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "repeat-mode", "Which repeat mode the bot should use", true,
                        Arrays.asList(
                                SlashCommandOptionChoice.create("OFF", "OFF"),
                                SlashCommandOptionChoice.create("CURRENT-TRACK", "CURRENT-TRACK"),
                                SlashCommandOptionChoice.create("QUEUE", "QUEUE")
                        ))),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("skip")).setDescription(ParseCommands.getCommandHelp("skip")),
                new SlashCommandBuilder().setName(ParseCommands.getCommandName("stop")).setDescription(ParseCommands.getCommandHelp("stop"))
        )).join();
    }
}
