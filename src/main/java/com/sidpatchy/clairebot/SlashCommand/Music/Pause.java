package com.sidpatchy.clairebot.SlashCommand.Music;

import com.sidpatchy.clairebot.Embed.Commands.Music.PauseEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class Pause implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();

        if (slashCommandInteraction.getCommandName().equalsIgnoreCase(ParseCommands.getCommandName("pause"))) {
            User author = slashCommandInteraction.getUser();

            slashCommandInteraction.getServer().ifPresent(server -> {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(PauseEmbed.getPauseEmbed(server, author))
                        .respond();
            });
        }
    }
}
