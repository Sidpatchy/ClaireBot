package com.sidpatchy.clairebot.SlashCommand.Music;

import com.sidpatchy.clairebot.Embed.Commands.Music.PauseEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Music.PlayEmbed;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class Play implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String query = slashCommandInteraction.getOptionStringValueByName("query").orElse(null);

        if (slashCommandInteraction.getCommandName().equalsIgnoreCase(ParseCommands.getCommandName("play"))) {
            User author = slashCommandInteraction.getUser();

            if (query != null) {
                // get the server
                slashCommandInteraction.getServer().ifPresentOrElse(server -> {
                            long serverID = server.getId();
                            slashCommandInteraction.createImmediateResponder()
                                    .addEmbed(PlayEmbed.getPlayEmbed(query, server, author))
                                    .respond();
                        },
                        () -> {
                            String errorCode = Main.getErrorCode("play_noserver");
                            Main.getLogger().error(errorCode + ": Unable to fetch server.");
                            slashCommandInteraction.createImmediateResponder()
                                    .addEmbed(ErrorEmbed.getCustomError(errorCode, "ClaireBot's music functionality is only available in servers.\n\n" +
                                            "If you're in a server and seeing this please join our [Discord server](https://support.clairebot.net/) and let us know. \n\n" +
                                            "Please include the following error code: " + errorCode))
                                    .respond();
                        });
            }
            else {
                // Resume the queue
                slashCommandInteraction.getServer().ifPresentOrElse(server -> {
                    slashCommandInteraction.createImmediateResponder()
                            .addEmbed(PauseEmbed.getPauseEmbed(server, author))
                            .respond();
                },
                () -> {
                    String errorCode = Main.getErrorCode("play_noserver");
                    Main.getLogger().error(errorCode + ": Unable to fetch server.");
                    slashCommandInteraction.createImmediateResponder()
                            .addEmbed(ErrorEmbed.getCustomError(errorCode, "ClaireBot's music functionality is only available in servers.\n\n" +
                                    "If you're in a server and seeing this please join our [Discord server](https://support.clairebot.net/) and let us know. \n\n" +
                                    "Please include the following error code: " + errorCode))
                            .respond();
                });
            }
        }
    }
}
