package com.sidpatchy.clairebot.Listener.SlashCommand.Regular;

import com.sidpatchy.clairebot.Embed.Commands.Regular.AvatarEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class Avatar implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();
        User author = slashCommandInteraction.getUser();
        User user = slashCommandInteraction.getOptionUserValueByIndex(0).orElse(null);

        if (commandName.equalsIgnoreCase(ParseCommands.getCommandName("avatar"))) {
            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(AvatarEmbed.getAvatar(user, author))
                    .respond();
        }
    }
}
