package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.interaction.ModalInteraction;
import org.javacord.api.listener.interaction.ModalSubmitListener;

public class ModalSubmit implements ModalSubmitListener {

    @Override
    public void onModalSubmit(ModalSubmitEvent event) {
        ModalInteraction modalInteraction = event.getModalInteraction();

        User user = modalInteraction.getUser();
        String modalID = modalInteraction.getCustomId();

        Main.getLogger().debug(modalID);

        switch (modalID) {
            case "hex-entry-modal":
                String hexColour = modalInteraction.getTextInputValueByCustomId("hex-entry-field").orElse("");

                if (!hexColour.equals("")) {
                    // Add "#" if not present
                    if (hexColour.length() == 6 || !hexColour.contains("#")) {
                        hexColour = "#" + hexColour;
                    }

                    Main.getLogger().debug(hexColour);

                    modalInteraction.createImmediateResponder()
                        .addEmbed(UserPreferencesEmbed.getAcknowledgeAccentColourChange(user, hexColour))
                        .respond();
                }
        }
    }
}
