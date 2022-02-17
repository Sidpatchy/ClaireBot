package com.sidpatchy.clairebot.SlashCommand.Regular;

import com.sidpatchy.clairebot.Embed.Commands.Regular.EightBallEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class EightBall implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();
        User author = slashCommandInteraction.getUser();

        if (commandName.equalsIgnoreCase(ParseCommands.getCommandName("8ball"))) {
            String query = slashCommandInteraction.getFirstOptionStringValue().orElse(null);

            if (query == null) {return;}
            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(EightBallEmbed.getEightBall(query, author))
                    .respond();
        }
    }
}
