package com.sidpatchy.clairebot;

import com.sidpatchy.Robin.Discord.ParseCommands;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Also delete them too!
 */
public class RegisterSlashCommands {

    private static final ParseCommands parseCommands = new ParseCommands(Main.getCommandsFile());

    public static void DeleteSlashCommands (DiscordApi api) {
        api.bulkOverwriteGlobalApplicationCommands(Set.of()).join();
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
            helpCommandOptions.add(SlashCommandOptionChoice.create(parseCommands.getCommandName(s), parseCommands.getCommandName(s)));
        }

        Set<SlashCommandBuilder> commandsList = new HashSet<>(Arrays.asList(
                // Regular commands
                new SlashCommandBuilder().setName(parseCommands.getCommandName("8ball")).setDescription(parseCommands.getCommandHelp("8ball")).addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "query", "The question you wish to ask.", true)),
                new SlashCommandBuilder().setName(parseCommands.getCommandName("avatar")).setDescription(parseCommands.getCommandHelp("avatar")).addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false)),
                new SlashCommandBuilder().setName(parseCommands.getCommandName("help")).setDescription(parseCommands.getCommandHelp("help"))
                        .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "command-name", "Command to get more info on", false, helpCommandOptions)),
                new SlashCommandBuilder().setName(parseCommands.getCommandName("info")).setDescription(parseCommands.getCommandHelp("info")),
                new SlashCommandBuilder().setName(parseCommands.getCommandName("leaderboard")).setDescription(parseCommands.getCommandHelp("leaderboard")).addOption(SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "global", "Get the global leaderboard?", false)),
                new SlashCommandBuilder().setName(parseCommands.getCommandName("level")).setDescription(parseCommands.getCommandHelp("level")).addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false)),
                new SlashCommandBuilder().setName(parseCommands.getCommandName("poll")).setDescription(parseCommands.getCommandHelp("poll"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "question", "Question to ask", false))
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
                new SlashCommandBuilder().setName(parseCommands.getCommandName("quote")).setDescription(parseCommands.getCommandOverview("quote"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "User", "Optionally mention a user", false)),
                new SlashCommandBuilder().setName(parseCommands.getCommandName("request")).setDescription(parseCommands.getCommandOverview("request"))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "question", "Question to ask", false))
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
                new SlashCommandBuilder().setName(parseCommands.getCommandName("server")).setDescription(parseCommands.getCommandHelp("server")).addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "guildID", "Optionally specify a guild by ID.", false)),
                new SlashCommandBuilder().setName(parseCommands.getCommandName("user")).setDescription(parseCommands.getCommandHelp("user")).addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false)),
                new SlashCommandBuilder().setName(parseCommands.getCommandName("config")).setDescription(parseCommands.getCommandHelp("config"))
                        .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "mode", "Settings to change", false, Arrays.asList(
                                SlashCommandOptionChoice.create("user", "user"),
                                SlashCommandOptionChoice.create("server", "server")
                        )))
                //new SlashCommandBuilder().setName(parseCommands.getCommandName("debug")).setDescription(parseCommands.getCommandHelp("debug"))
        ));

        api.bulkOverwriteGlobalApplicationCommands(commandsList).join();
    }
}
