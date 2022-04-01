package com.sidpatchy.clairebot.Listener.SlashCommand.Music;

import com.sidpatchy.clairebot.Embed.Commands.Music.LeaveEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class Leave implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();

        if (commandName.equalsIgnoreCase(ParseCommands.getCommandName("leave")) || commandName.equalsIgnoreCase(ParseCommands.getCommandName("stop"))) {
            slashCommandInteraction.getServer().ifPresent(server -> {
                User author = slashCommandInteraction.getUser();
                ServerVoiceChannel channel = author.getConnectedVoiceChannel(server).orElse(null);

                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(LeaveEmbed.getLeaveEmbed(author, server, channel))
                        .respond();
            });
        }
    }
}
