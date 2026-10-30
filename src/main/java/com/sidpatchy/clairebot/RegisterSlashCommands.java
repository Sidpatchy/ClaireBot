package com.sidpatchy.clairebot;

import com.sidpatchy.Robin.Discord.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static net.dv8tion.jda.api.interactions.commands.build.Commands.slash;

/**
 * Registers the stable command names and localized descriptions.
 */
public class RegisterSlashCommands {

    private static final Commands commands = Main.getCommands();
    private static final Commands defaultLanguageCommands = Main.getCommands(Main.getFallbackLocale());

    public static void DeleteSlashCommands(JDA api) {
        api.updateCommands().addCommands().complete();
    }

    /**
     * Register slash commands while feeling like you're doing it wrong no matter how you do it!
     *
     * Only called on startup.
     *
     * @param api pass API into function
     */
    public static void RegisterSlashCommand(JDA api) {
        ArrayList<Choice> helpCommandOptions = new ArrayList<>();
        for (Command command : commands.getAllCommands()) {
            helpCommandOptions.add(new Choice(command.getName(), command.getName()));
        }

        Set<SlashCommandData> commandsList = new HashSet<>(Arrays.asList(
                localizedDescription(slash(commands.getEightball().getName(), description(defaultLanguageCommands.getEightball()))
                        .addOption(OptionType.STRING, "query", "The question you wish to ask.", true), "eightball"),
                localizedDescription(slash(commands.getAvatar().getName(), description(defaultLanguageCommands.getAvatar()))
                        .addOption(OptionType.USER, "user", "Optionally mention a user.", false)
                        .addOption(OptionType.BOOLEAN, "globalavatar", "Whether the bot should display the global or server avatar."), "avatar"),
                localizedDescription(slash(commands.getHelp().getName(), description(defaultLanguageCommands.getHelp()))
                        .addOptions(new OptionData(OptionType.STRING, "command-name", "Command to get more info on", false).addChoices(helpCommandOptions)), "help"),
                localizedDescription(slash(commands.getInfo().getName(), description(defaultLanguageCommands.getInfo())), "info"),
                localizedDescription(slash(commands.getLeaderboard().getName(), description(defaultLanguageCommands.getLeaderboard()))
                        .addOption(OptionType.BOOLEAN, "global", "Get the global leaderboard?", false), "leaderboard"),
                localizedDescription(slash(commands.getLevel().getName(), description(defaultLanguageCommands.getLevel()))
                        .addOption(OptionType.USER, "user", "Optionally mention a user.", false), "level"),
                localizedDescription(addVotingOptions(slash(commands.getPoll().getName(), description(defaultLanguageCommands.getPoll()))), "poll"),
                localizedDescription(slash(commands.getQuote().getName(), description(defaultLanguageCommands.getQuote()))
                        .addOption(OptionType.USER, "user", "Optionally mention a user", false), "quote"),
                localizedDescription(addVotingOptions(slash(commands.getRequest().getName(), description(defaultLanguageCommands.getRequest()))), "request"),
                localizedDescription(slash(commands.getServer().getName(), description(defaultLanguageCommands.getServer()))
                        .addOption(OptionType.STRING, "guildid", "Optionally specify a guild by ID.", false), "server"),
                localizedDescription(slash(commands.getUser().getName(), description(defaultLanguageCommands.getUser()))
                        .addOption(OptionType.USER, "user", "Optionally mention a user.", false), "user"),
                localizedDescription(slash(commands.getConfig().getName(), description(defaultLanguageCommands.getConfig()))
                        .addOptions(new OptionData(OptionType.STRING, "mode", "Settings to change", false)
                                .addChoice("user", "user").addChoice("server", "server")), "config"),
                localizedDescription(slash(commands.getSanta().getName(), description(defaultLanguageCommands.getSanta()))
                        .addOption(OptionType.ROLE, "role", "Role to get users from", true), "santa")
        ));

        api.updateCommands().addCommands(commandsList).complete();
    }

    private static SlashCommandData addVotingOptions(SlashCommandData builder) {
        builder.addOption(OptionType.STRING, "question", "Question to ask", false)
                .addOption(OptionType.BOOLEAN, "allow-multiple-choices", "Whether multiple choices should be enabled.", false);
        for (int i = 1; i <= 10; i++) {
            builder.addOption(OptionType.STRING, "choice-" + i, "Custom choice");
        }
        return builder;
    }

    private static String description(Command command) {
        String overview = command.getOverview();
        String description = overview == null || overview.isBlank() ? command.getHelp() : overview;
        if (description == null || description.isBlank()) description = command.getName();
        return description.substring(0, Math.min(description.length(), 100));
    }

    private static SlashCommandData localizedDescription(SlashCommandData builder, String commandKey) {
        for (Locale locale : Main.getAvailableCommandLocales()) {
            DiscordLocale discordLocale = DiscordLocale.from(locale);
            if (discordLocale == DiscordLocale.UNKNOWN
                    || locale.equals(Main.getFallbackLocale())) {
                continue;
            }

            Command localizedCommand = Main.getCommands(locale).getCommandsByKey().get(commandKey);
            String description = localizedCommand.getOverview();
            if (description != null && !description.isBlank()) {
                builder.setDescriptionLocalization(discordLocale, description);
            }
        }
        return builder;
    }
}
