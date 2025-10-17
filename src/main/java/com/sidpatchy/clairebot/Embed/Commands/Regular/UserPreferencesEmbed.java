package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.API.APIUser;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class UserPreferencesEmbed {

    public static EmbedBuilder getMainMenu(LanguageManager languageManager, User author) {
        // Temp/localized variables
        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserPreferencesEmbed.MainMenuText");

        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor(title);
    }

    public static EmbedBuilder getAccentColourMenu(LanguageManager languageManager, User author) {
        // Temp/localized variables
        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserPreferencesEmbed.AccentColourMenu");

        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor(title);
    }

    public static EmbedBuilder getAccentColourListMenu(LanguageManager languageManager, User author) {
        // Temp/localized variables
        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserPreferencesEmbed.AccentColourList");

        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor(title);
    }

    /**
     * Response when an accent colour has been selected. Updates colour based off passed colourCode.
     *
     * @param author User updating their colour
     * @param colourCode colour code selected (e.g. #5865F2)
     * @return embed
     */
    public static EmbedBuilder getAcknowledgeAccentColourChange(LanguageManager languageManager, User author, String colourCode) {
        // Temp/localized variables
        Color color = Color.decode(colourCode);

        try {
            APIUser apiUser = new APIUser(author.getIdAsString());
            apiUser.getUser();
            apiUser.updateUserColour(colourCode);

            String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserPreferencesEmbed.AccentColourChanged");
            String desc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserPreferencesEmbed.AccentColourChangedDesc");

            return new EmbedBuilder()
                    .setColor(color)
                    .setAuthor(title)
                    .setDescription(desc);
        } catch (Exception e) {
            return ErrorEmbed.getError(languageManager, Main.getErrorCode("updateAccentColour"));
        }
    }

    public static EmbedBuilder getLanguageMenu(LanguageManager languageManager, User author) {
        // Temp/localized variables
        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserPreferencesEmbed.LanguageMenuTitle");
        String desc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserPreferencesEmbed.LanguageMenuDesc");

        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor(title)
                .setDescription(desc);
    }

    /**
     * Response when a language has been selected. Updates language based on the selected locale tag.
     *
     * @param author User updating their language
     * @param languageTag IETF BCP 47 tag (e.g., en-US)
     * @return embed
     */
    public static EmbedBuilder getAcknowledgeLanguageChange(LanguageManager languageManager, User author, String languageTag) {
        try {
            APIUser apiUser = new APIUser(author.getIdAsString());
            apiUser.getUser();
            apiUser.updateUserLanguage(languageTag);

            String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserPreferencesEmbed.LanguageChanged");
            String desc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.UserPreferencesEmbed.LanguageChangedDesc");

            return new EmbedBuilder()
                    .setColor(Main.getColor(author.getIdAsString()))
                    .setAuthor(title)
                    .setDescription(desc);
        } catch (Exception e) {
            return ErrorEmbed.getError(languageManager, Main.getErrorCode("updateLanguage"));
        }
    }
}
