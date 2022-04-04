package com.sidpatchy.clairebot.Listener.SlashCommand.Regular;

import com.sidpatchy.clairebot.Embed.Commands.Regular.ServerInfoEmbed;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
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

        EmbedBuilder embed = null;

        Server server = slashCommandInteraction.getServer().orElse(null);
        String guildID = slashCommandInteraction.getOptionStringValueByName("guildID").orElse(null);

        if (server == null && guildID == null) {
            embed = ErrorEmbed.getCustomError(Main.getErrorCode("no-guild-present"), "A guild must be specified. Either run this command in a server or specify a guild ID.");
        }

        if (guildID != null) {
            Server fromGuildID = event.getApi().getServerById(guildID).orElse(null);
            if (fromGuildID != null) {
                embed = ServerInfoEmbed.getServerInfo(fromGuildID);
            }
            else {
                embed = ErrorEmbed.getCustomError(Main.getErrorCode("guildID-invalid"), "Either that guild ID is invalid or I'm not a member of the server.");
            }
        }
        else if (server != null) {
            embed = ServerInfoEmbed.getServerInfo(server);
        }

        slashCommandInteraction.createImmediateResponder()
                .addEmbed(embed)
                .respond();
    }
}
