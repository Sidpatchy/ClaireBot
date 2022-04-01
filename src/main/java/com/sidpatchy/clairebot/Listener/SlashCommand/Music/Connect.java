package com.sidpatchy.clairebot.Listener.SlashCommand.Music;

import com.sidpatchy.clairebot.Embed.Commands.Music.ConnectEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class Connect implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();

        if (commandName.equalsIgnoreCase(ParseCommands.getCommandName("connect"))) {
            slashCommandInteraction.getServer().ifPresent(server -> {
                User author = slashCommandInteraction.getUser();
                ServerVoiceChannel channel = author.getConnectedVoiceChannel(server).orElse(null);

                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(ConnectEmbed.getConnectEmbed(author, channel))
                        .respond();
            });
        }
    }
}
