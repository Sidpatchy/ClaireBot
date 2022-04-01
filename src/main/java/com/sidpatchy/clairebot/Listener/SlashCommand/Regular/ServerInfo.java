package com.sidpatchy.clairebot.Listener.SlashCommand.Regular;

import com.sidpatchy.clairebot.Embed.Commands.Regular.ServerInfoEmbed;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.concurrent.atomic.AtomicReference;

public class ServerInfo implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();

        if (!commandName.equalsIgnoreCase(ParseCommands.getCommandName("server"))) { return; }

        AtomicReference<EmbedBuilder> embed = new AtomicReference<>();

        slashCommandInteraction.getServer().ifPresentOrElse(server -> {
            embed.set(ServerInfoEmbed.getServerInfo(server));
        },
        // else
        () -> {
            slashCommandInteraction.getOptionStringValueByName("guildID").ifPresentOrElse(guildID -> {
                event.getApi().getServerById(guildID).ifPresentOrElse(server -> {
                    embed.set(ServerInfoEmbed.getServerInfo(server));
                },
                // else
                () -> {
                    ErrorEmbed.getCustomError(Main.getErrorCode("guildID-invalid"), "That guild ID appears to be invalid.");
                });
            },
            // else
            () -> {
                ErrorEmbed.getCustomError(Main.getErrorCode("no-guild-present"), "A guild must be specified. Either run this command in a server or specify a guild ID.");
            });
        });

        slashCommandInteraction.createImmediateResponder()
                .addEmbed(embed.get())
                .respond();
    }
}
