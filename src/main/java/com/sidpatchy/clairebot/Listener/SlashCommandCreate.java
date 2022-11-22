package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.Robin.Discord.ParseCommands;
import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.MessageComponents.Regular.UserPreferencesComponents;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

public class SlashCommandCreate implements SlashCommandCreateListener {

    static ParseCommands parseCommands = new ParseCommands(Main.getCommandsFile());
    Logger logger = Main.getLogger();

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        Server server = slashCommandInteraction.getServer().orElse(null);
        String commandName = slashCommandInteraction.getCommandName();
        User author = slashCommandInteraction.getUser();
        User user = slashCommandInteraction.getOptionUserValueByName("user").orElse(null);

        if (user == null) {
            user = author;
        }

        if (commandName.equalsIgnoreCase(parseCommands.getCommandName("config"))) {
            String mode = slashCommandInteraction.getOptionStringValueByName("mode").orElse("");

            if (mode.equalsIgnoreCase("user")) {
                slashCommandInteraction.createImmediateResponder()
                        .addEmbed(UserPreferencesEmbed.getMainMenu(author))
                        .addComponents(UserPreferencesComponents.getMainMenu())
                        .respond();
            }
            else if (mode.equalsIgnoreCase("server")) {

            }
        }
    }
}
