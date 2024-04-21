package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.SantaEmbed;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.MessageComponents.Regular.SantaModal;
import com.sidpatchy.clairebot.Util.SantaUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.listener.interaction.ButtonClickListener;

public class ButtonClick implements ButtonClickListener {
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        ButtonInteraction buttonInteraction = event.getButtonInteraction();

        String buttonID = buttonInteraction.getCustomId().toLowerCase();
        User buttonAuthor = buttonInteraction.getUser();
        Message message = buttonInteraction.getMessage();

        Embed embed = buttonInteraction.getMessage().getEmbeds().get(0);
        EmbedFooter footer = embed.getFooter().orElse(null);

        // Extract data from embed fields
        SantaUtils.ExtractionResult extractionResult = SantaUtils.extractDataFromEmbed(embed, footer);

        Server server = Main.getApi().getServerById(extractionResult.santaID.get("serverID")).orElse(null);
        User author = Main.getApi().getUserById(extractionResult.santaID.get("authorID")).join();

        switch (buttonID) {
            case "rules":
                buttonInteraction.respondWithModal("santa-rules-" + message.getIdAsString(), "Update Rules",
                        SantaModal.getRulesRow()
                );

                break;
            case "theme":
                buttonInteraction.respondWithModal("santa-theme-" + message.getIdAsString(), "Update Theme",
                        SantaModal.getThemeRow()
                );

                break;
            case "send":
                buttonInteraction.acknowledge();
                for (int i = 0; i < extractionResult.givers.size(); i++) {
                    SantaEmbed.getSantaMessage(server, author, extractionResult.givers.get(i), extractionResult.receivers.get(i), extractionResult.rules, extractionResult.theme).send(extractionResult.givers.get(i));
                }

                break;
            case "test":
                buttonInteraction.acknowledge();
                SantaEmbed.getSantaMessage(server, author, extractionResult.givers.get(0), extractionResult.receivers.get(0), extractionResult.rules, extractionResult.theme).send(buttonAuthor);

                break;
            case "randomize":
                buttonInteraction.acknowledge();
                Role role = Main.getApi().getRoleById(extractionResult.santaID.get("roleID")).orElse(null);
                buttonInteraction.getMessage().delete();
                SantaEmbed.getHostMessage(role, buttonAuthor, extractionResult.rules, extractionResult.theme).send(buttonAuthor);

                break;
        }
    }
}
