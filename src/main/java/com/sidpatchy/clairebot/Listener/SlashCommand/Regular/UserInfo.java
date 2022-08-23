package com.sidpatchy.clairebot.Listener.SlashCommand.Regular;

import com.sidpatchy.clairebot.Embed.Commands.Regular.UserInfoEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class UserInfo implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();
        User author = slashCommandInteraction.getUser();
        User user = slashCommandInteraction.getOptionUserValueByIndex(0).orElse(author);
        Server server = slashCommandInteraction.getServer().orElse(null);

        if (commandName.equalsIgnoreCase(ParseCommands.getCommandName("user"))) {
            slashCommandInteraction.createImmediateResponder()
                    .addEmbed(UserInfoEmbed.getUser(user, author, server))
                    .respond();
        }
    }
}
