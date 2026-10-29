package com.sidpatchy.clairebot;

import com.sidpatchy.Robin.Discord.Command;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.DiscordLocale;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Registers the stable command names and localized descriptions.
 */
public class RegisterSlashCommands {

    private static final Commands commands = Main.getCommands();
    private static final Commands defaultLanguageCommands = Main.getCommands(Main.getFallbackLocale());

    public static void DeleteSlashCommands(DiscordApi api) {
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
        ArrayList<SlashCommandOptionChoice> helpCommandOptions = new ArrayList<>();
        for (Command command : commands.getAllCommands()) {
            helpCommandOptions.add(SlashCommandOptionChoice.create(command.getName(), command.getName()));
        }

        Set<SlashCommandBuilder> commandsList = new HashSet<>(Arrays.asList(
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getEightball().getName())
                        .setDescription(defaultLanguageCommands.getEightball().getOverview())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "query", "The question you wish to ask.", true)), "eightball"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getAvatar().getName())
                        .setDescription(defaultLanguageCommands.getAvatar().getOverview())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false))
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "globalAvatar", "Whether the bot should display the global or server avatar.")), "avatar"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getHelp().getName())
                        .setDescription(defaultLanguageCommands.getHelp().getOverview())
                        .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "command-name", "Command to get more info on", false, helpCommandOptions)), "help"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getInfo().getName())
                        .setDescription(defaultLanguageCommands.getInfo().getOverview()), "info"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getLeaderboard().getName())
                        .setDescription(defaultLanguageCommands.getLeaderboard().getOverview())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "global", "Get the global leaderboard?", false)), "leaderboard"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getLevel().getName())
                        .setDescription(defaultLanguageCommands.getLevel().getOverview())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false)), "level"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getPoll().getName())
                        .setDescription(defaultLanguageCommands.getPoll().getOverview())
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
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-9", "Custom choice")), "poll"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getQuote().getName())
                        .setDescription(defaultLanguageCommands.getQuote().getOverview())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "User", "Optionally mention a user", false)), "quote"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getRequest().getName())
                        .setDescription(defaultLanguageCommands.getRequest().getOverview())
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
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "choice-9", "Custom choice")), "request"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getServer().getName())
                        .setDescription(defaultLanguageCommands.getServer().getOverview())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.STRING, "guildID", "Optionally specify a guild by ID.", false)), "server"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getUser().getName())
                        .setDescription(defaultLanguageCommands.getUser().getOverview())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.USER, "user", "Optionally mention a user.", false)), "user"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getConfig().getName())
                        .setDescription(defaultLanguageCommands.getConfig().getOverview())
                        .addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "mode", "Settings to change", false, Arrays.asList(
                                SlashCommandOptionChoice.create("user", "user"),
                                SlashCommandOptionChoice.create("server", "server")
                        ))), "config"),
                localizedDescription(new SlashCommandBuilder()
                        .setName(commands.getSanta().getName())
                        .setDescription(defaultLanguageCommands.getSanta().getOverview())
                        .addOption(SlashCommandOption.create(SlashCommandOptionType.ROLE, "Role", "Role to get users from", true)), "santa")
        ));

        api.bulkOverwriteGlobalApplicationCommands(commandsList).join();
    }

    private static SlashCommandBuilder localizedDescription(SlashCommandBuilder builder, String commandKey) {
        for (Locale locale : Main.getAvailableCommandLocales()) {
            DiscordLocale discordLocale = DiscordLocale.fromLocaleCode(locale.toLanguageTag());
            if (discordLocale == DiscordLocale.UNKNOWN
                    || locale.equals(Main.getFallbackLocale())) {
                continue;
            }

            Command localizedCommand = Main.getCommands(locale).getCommandsByKey().get(commandKey);
            String description = localizedCommand.getOverview();
            if (description != null && !description.isBlank()) {
                builder.addDescriptionLocalization(discordLocale, description);
            }
        }
        return builder;
    }
}
