package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.SantaEmbed;
import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import com.sidpatchy.clairebot.Util.SantaUtils;
import com.sidpatchy.clairebot.Util.Voting.VotingFlow;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedFooter;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.interaction.ModalInteraction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.ModalSubmitListener;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ModalSubmit implements ModalSubmitListener {

    private LanguageManager languageManager;

    @Override
    public void onModalSubmit(ModalSubmitEvent event) {
        ModalInteraction modalInteraction = event.getModalInteraction();

        Server server = modalInteraction.getServer().orElse(null);
        TextChannel textchannel = modalInteraction.getChannel().orElse(null);
        User user = modalInteraction.getUser();
        String modalID = modalInteraction.getCustomId();

        Main.getLogger().debug(modalID);

        ContextManager context = new ContextManager(server, textchannel, user, user, null, new HashMap<>());
        languageManager = new LanguageManager(Main.getFallbackLocale(), context);

        // Santa related vars
        String santaMessageID = ""; // Allows the Secret Santa function to identify which message is being referenced.
        SantaUtils.ExtractionResult extractionResult = null;
        Message santaMessage = null;
        if (modalID.startsWith("santa")) {
            if (modalID.startsWith("santa-rules")) {
                santaMessageID = modalID.replace("santa-rules-", "");
                modalID = "santa-rules";
            }
            else if (modalID.startsWith("santa-theme")) {
                santaMessageID = modalID.replace("santa-theme-", "");
                modalID = "santa-theme";
            }

            santaMessage = Main.getApi().getCachedMessageById(santaMessageID).orElse(null);
            assert santaMessage != null;

            Embed embed = santaMessage.getEmbeds().getFirst();
            EmbedFooter footer = embed.getFooter().orElse(null);

            extractionResult = SantaUtils.extractDataFromEmbed(embed, footer);
        }


        
        switch (modalID) {
            case "hex-entry-modal":
                String hexColour = modalInteraction.getTextInputValueByCustomId("hex-entry-field").orElse("");

                if (!hexColour.isEmpty()) {
                    // Add "#" if not present
                    if (hexColour.length() == 6 || !hexColour.contains("#")) {
                        hexColour = "#" + hexColour;
                    }

                    Main.getLogger().debug(hexColour);

                    modalInteraction.createImmediateResponder()
                        .addEmbed(UserPreferencesEmbed.getAcknowledgeAccentColourChange(languageManager, user, hexColour))
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
                }
                break;
            case "poll", "request":
                String question = modalInteraction.getTextInputValueByCustomId("question-modal").orElse("");
                String description = modalInteraction.getTextInputValueByCustomId("details-modal").orElse("");
                VotingFlow.start(modalInteraction, languageManager, modalID.toUpperCase(), question, description);
                break;
            default:
                if (modalID.startsWith("vote-options:")) {
                    VotingFlow.options(modalInteraction, languageManager);
                }
                break;
            case "santa-rules", "santa-theme":
                // Silently defer the modal response (no visible message), then update the existing host message in place
                CompletableFuture<InteractionOriginalResponseUpdater> deferred = modalInteraction.respondLater();

                assert extractionResult != null;
                extractionResult.rules = modalInteraction.getTextInputValueByCustomId("rules-row").orElse(extractionResult.rules);
                extractionResult.theme = modalInteraction.getTextInputValueByCustomId("theme-row").orElse(extractionResult.theme);

                Role role = Main.getApi().getRoleById(extractionResult.santaID.get("roleID")).orElse(null);
                assert role != null;

                // Rebuild the host embed with the existing giver/receiver pairs and edit the original message
                santaMessage.edit(SantaEmbed.buildHostEmbedFromPairs(languageManager, role, user, extractionResult.rules, extractionResult.theme, extractionResult.givers, extractionResult.receivers));
                // Finalize and immediately delete the deferred response to avoid leaving a "Bot is thinking…" stub
                deferred.thenAccept(interactionOriginalResponseUpdater -> {
                    try {
                        interactionOriginalResponseUpdater.setContent("\u200B").update().thenAccept(msg -> {
                            try { msg.delete(); } catch (Exception ignored2) {}
                        });
                    } catch (Exception ignored) {}
                });
                break;
        }
    }
}
