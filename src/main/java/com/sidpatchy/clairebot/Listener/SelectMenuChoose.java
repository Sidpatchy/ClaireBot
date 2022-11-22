package com.sidpatchy.clairebot.Listener;

import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.MessageComponents.Regular.UserPreferencesComponents;
import com.sidpatchy.clairebot.Embed.Commands.Regular.UserPreferencesEmbed;
import com.sidpatchy.clairebot.Main;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedAuthor;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SelectMenuChooseEvent;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.listener.interaction.SelectMenuChooseListener;

public class SelectMenuChoose implements SelectMenuChooseListener {
    Logger logger = Main.getLogger();

    @Override
    public void onSelectMenuChoose(SelectMenuChooseEvent event) {
        SelectMenuInteraction selectMenuInteraction = event.getSelectMenuInteraction();

        Message message = selectMenuInteraction.getMessage();
        User user = selectMenuInteraction.getUser();

        EmbedAuthor embedAuthor = message.getEmbeds().get(0).getAuthor().orElse(null);
        if (embedAuthor != null) {
            String menuName = embedAuthor.getName();
            String label = selectMenuInteraction.getChosenOptions().get(0).getLabel();

            if (menuName.equalsIgnoreCase("User Preferences Editor")) {
                if (label.equalsIgnoreCase("Accent Colour")) {
                    message.createUpdater()
                            .setEmbed(UserPreferencesEmbed.getAccentColourMenu(user))
                            .addComponents(UserPreferencesComponents.getAccentColourMenu())
                            .applyChanges();
                    selectMenuInteraction.acknowledge();
                }
                else if (label.equalsIgnoreCase("Language")) {
                    message.createUpdater()
                            .setEmbed(UserPreferencesEmbed.getLanguageMenu(user))
                            .addComponents()
                            .applyChanges();
                    selectMenuInteraction.acknowledge();
                }
            }
            else if (menuName.equalsIgnoreCase("Accent Colour Editor")) {
                if (label.equalsIgnoreCase("Select Common Colours")) {
                    message.createUpdater()
                            .setEmbed(UserPreferencesEmbed.getAccentColourListMenu(user))
                            .addComponents(UserPreferencesComponents.getAccentColourList())
                            .applyChanges();
                    selectMenuInteraction.acknowledge();
                }
                else if (label.equalsIgnoreCase("Hexadecimal Entry")) {
                    message.delete();
                    selectMenuInteraction.respondWithModal("hex-entry-modal", "Hex Colour Entry", UserPreferencesComponents.getAccentColourHexEntry());
                    selectMenuInteraction.acknowledge();
                }
            }
            else if (menuName.equalsIgnoreCase("Accent Colour List")) {
                String accentColour = selectMenuInteraction.getChosenOptions().get(0).getDescription().orElse("");

                if (accentColour.isEmpty()) {
                    message.createUpdater()
                            .setEmbed(ErrorEmbed.getError(Main.getErrorCode("accentColourParse")))
                            .applyChanges();
                }
                else {
                    message.createUpdater()
                            .setEmbed(UserPreferencesEmbed.getAcknowledgeAccentColourChange(user, accentColour))
                            .addComponents()
                            .applyChanges();
                }

                selectMenuInteraction.acknowledge();
            }
        }
    }
}
