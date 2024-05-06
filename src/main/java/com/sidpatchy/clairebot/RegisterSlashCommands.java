package com.sidpatchy.clairebot;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Also delete them too!
 *
 * todo switch to registering commands on a per-server basis so that server admins may use different commands.yml languages.
 */
public class RegisterSlashCommands {

    private static final Commands commands = Main.getCommands();

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
        for (Field field : commands.getClass().getDeclaredFields()) {
            helpCommandOptions.add(SlashCommandOptionChoice.create(field.getName(), field.getName()));
        }

        Set<SlashCommandBuilder> commandsList = new HashSet<>(Arrays.asList(
                // Regular commands
                new SlashCommandBuilder()
                        .setName(commands.getEightball().getName())
                        .setDescription(commands.getEightball().getHelp())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "query", "The question you wish to ask.", true)),
                new SlashCommandBuilder()
                        .setName(commands.getAvatar().getName())
                        .setDescription(commands.getAvatar().getHelp())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "globalAvatar", "Whether the bot should display the global or server avatar.")),
                new SlashCommandBuilder()
                        .setName(commands.getHelp().getName())
                        .setDescription(commands.getHelp().getHelp())
                        .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "command-name", "Command to get more info on", false, helpCommandOptions)),
                new SlashCommandBuilder()
                        .setName(commands.getInfo().getName())
                        .setDescription(commands.getInfo().getHelp()),
                new SlashCommandBuilder()
                        .setName(commands.getLeaderboard().getName())
                        .setDescription(commands.getLeaderboard().getHelp())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "global", "Get the global leaderboard?", false)),
                new SlashCommandBuilder()
                        .setName(commands.getLevel().getName())
                        .setDescription(commands.getLevel().getHelp())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false)),
                new SlashCommandBuilder()
                        .setName(commands.getPoll().getName())
                        .setDescription(commands.getPoll().getHelp())
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
                new SlashCommandBuilder()
                        .setName(commands.getQuote().getName())
                        .setDescription(commands.getQuote().getHelp())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "User", "Optionally mention a user", false)),
                new SlashCommandBuilder()
                        .setName(commands.getRequest().getName())
                        .setDescription(commands.getRequest().getHelp())
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
                new SlashCommandBuilder()
                        .setName(commands.getServer().getName())
                        .setDescription(commands.getServer().getHelp())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "guildID", "Optionally specify a guild by ID.", false)),
                new SlashCommandBuilder()
                        .setName(commands.getServer().getName())
                        .setDescription(commands.getServer().getHelp())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false)),
                new SlashCommandBuilder()
                        .setName(commands.getConfig().getName())
                        .setDescription(commands.getConfig().getHelp())
                        .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "mode", "Settings to change", false, Arrays.asList(
                                SlashCommandOptionChoice.create("user", "user"),
                                SlashCommandOptionChoice.create("server", "server")
                        ))),
                new SlashCommandBuilder()
                        .setName(commands.getSanta().getName())
                        .setDescription(commands.getSanta().getHelp())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.ROLE, "Role", "Role to get users from", true))
                //new SlashCommandBuilder().setName(parseCommands.getCommandName("debug")).setDescription(parseCommands.getCommandHelp("debug"))
        ));

        api.bulkOverwriteGlobalApplicationCommands(commandsList).join();
    }
}
