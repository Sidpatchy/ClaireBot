package com.sidpatchy.clairebot.SlashCommand;

import com.sidpatchy.clairebot.Embed.Commands.Regular.VotingEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.ArrayList;
import java.util.List;

public class Poll implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();
        Server server = slashCommandInteraction.getServer().orElse(null);

        if (commandName.equalsIgnoreCase(ParseCommands.getCommandName("poll")) && server != null) {
            User author = slashCommandInteraction.getUser();

            String question = slashCommandInteraction.getFirstOptionStringValue().orElse(null);
            boolean allowMultipleChoices = slashCommandInteraction.getSecondOptionBooleanValue().orElse(false);
            List<String> choices = new ArrayList<>();

            // Populate choices
            int numChoices = 0;
            for (int i = 0; i < 10; i++) {
                choices.add(slashCommandInteraction.getOptionStringValueByName("choice-" + (i + 1)).orElse(null));
                if (slashCommandInteraction.getOptionStringValueByName("choice-" + (i + 1)).orElse(null) == null) {
                    numChoices = i;
                    break;
                }
            }

            InteractionOriginalResponseUpdater response = slashCommandInteraction.createImmediateResponder()
                    .addEmbed(VotingEmbed.getPoll("POLL", question, allowMultipleChoices, choices, server, author, numChoices))
                    .respond()
                    .join();
        }
    }
}
