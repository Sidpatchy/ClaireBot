package com.sidpatchy.clairebot.Listener.SlashCommand.Regular;

import com.sidpatchy.clairebot.Embed.Commands.Regular.VotingEmbed;
import com.sidpatchy.clairebot.File.ParseCommands;
import com.sidpatchy.clairebot.MessageComponents.Regular.VotingComponents;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Poll implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        String commandName = slashCommandInteraction.getCommandName();
        Server server = slashCommandInteraction.getServer().orElse(null);

        if (commandName.equalsIgnoreCase(ParseCommands.getCommandName("poll")) && server != null) {
            if (slashCommandInteraction.getOptionStringValueByName("question").orElse(null) == null) {
                try {
                    CompletableFuture<Void> cum = slashCommandInteraction.respondWithModal("poll-1", "Create Poll",
                            VotingComponents.getQuestionRow(),
                            VotingComponents.getDetailsRow()
                            );

                    cum.exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                User author = slashCommandInteraction.getUser();

                String question = slashCommandInteraction.getOptionStringValueByIndex(0).orElse(null);
                boolean allowMultipleChoices = slashCommandInteraction.getOptionBooleanValueByIndex(1).orElse(false);
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
}
