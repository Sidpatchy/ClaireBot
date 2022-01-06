package com.sidpatchy.clairebot;

import com.sidpatchy.clairebot.File.ParseCommands;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterSlashCommands {

    public static void DeleteSlashCommands (DiscordApi api) {
        api.bulkOverwriteGlobalSlashCommands(List.of()).join();
    }

    /**
     * Register slash commands while feeling like you're doing it wrong no matter how you do it!
     *
     * @param api pass API into function
     */
    public static void RegisterSlashCommand(DiscordApi api) throws FileNotFoundException {

        // Create the command list in the help command without repeating the same thing 50 million times.
        List<String> commandList = Arrays.asList("8ball", "avatar", "leaderboard", "level", "poll", "servers", "user", "connect", "leave", "pause", "play", "previous", "queue", "repeat", "skip", "stop");
        ArrayList<SlashCommandOptionChoice> helpCommandOptions = new ArrayList<>();

        for (String s : commandList) {
            helpCommandOptions.add(SlashCommandOptionChoice.create(ParseCommands.getCommandName(s), ParseCommands.getCommandName(s)));
        }

        api.bulkOverwriteGlobalSlashCommands(Arrays.asList(
                // Regular commands
                new SlashCommandBuilder().setName("info").setDescription("Learn more about ClaireBot"),
                new SlashCommandBuilder().setName("help").setDescription("Help command").addOption(SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "command-name", "Command to get more info on", false, helpCommandOptions))
                
        )).join();
    }
}
