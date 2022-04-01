package com.sidpatchy.clairebot.Listener.SlashCommand.Regular;

import com.sidpatchy.clairebot.Embed.Commands.Regular.HelpEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.io.FileNotFoundException;

public class Help implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();
        String command = slashCommandInteraction.getFirstOptionStringValue().orElse("help");

        if (commandName.equalsIgnoreCase(ParseCommands.getCommandName("help"))) {
            try {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(HelpEmbed.getHelp(command))
                        .respond();
            } catch (FileNotFoundException e) {
                Main.getLogger().error(e);
                Main.getLogger().error("There was an issue locating the commands file at some point in the chain while the help command was running, good luck!");
            }
        }
    }
}
