package com.sidpatchy.clairebot.Embed;

import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Called when ClaireBot encounters (and catches) an error, ideally never.
 */
public class ErrorEmbed {
    public static EmbedBuilder getError(LanguageManager languageManager, String errorCode) {
        // Temp/localized variables
        List<String> errorGifs = new ArrayList<>(Main.getErrorGifs());
        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ErrorEmbed.Error");

        languageManager.addContext(ContextManager.ContextType.GENERIC, "errorcode", errorCode);
        String description = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ErrorEmbed.GenericDescription");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getErrorColor())
                .setAuthor(title)
                .setDescription(description);

        if (!errorGifs.isEmpty()) {
            int rand = new Random().nextInt(errorGifs.size());
            embed.setImage(errorGifs.get(rand));
        }

        return embed;
    }

    public static EmbedBuilder getError(LanguageManager languageManager, String errorCode, String customMessage) {
        // Temp/localized variables
        List<String> errorGifs = new ArrayList<>(Main.getErrorGifs());
        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ErrorEmbed.Error");

        languageManager.addContext(ContextManager.ContextType.GENERIC, "errorcode", errorCode);
        String generic = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ErrorEmbed.GenericDescription");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getErrorColor())
                .setAuthor(title)
                .setDescription(customMessage + "\n\n" + generic);

        if (!errorGifs.isEmpty()) {
            int rand = new Random().nextInt(errorGifs.size());
            embed.setImage(errorGifs.get(rand));
        }

        return embed;
    }

    public static EmbedBuilder getCustomError(LanguageManager languageManager, String errorCode, String message) {
        // Temp/localized variables
        List<String> errorGifs = new ArrayList<>(Main.getErrorGifs());
        String title = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Regular.ErrorEmbed.Error");

        languageManager.addContext(ContextManager.ContextType.GENERIC, "errorcode", errorCode);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Main.getErrorColor())
                .setAuthor(title)
                .setDescription(message);

        if (!errorGifs.isEmpty()) {
            int rand = new Random().nextInt(errorGifs.size());
            embed.setImage(errorGifs.get(rand));
        }

        return embed;
    }

    public static EmbedBuilder getLackingPermissions(LanguageManager languageManager, String message) {
        // Use a specific error code key and localize like a custom error
        String errorCode = Main.getErrorCode("noPerms");
        return getCustomError(languageManager, errorCode, message);
    }

    // TODO - remove these legacy methods

    @Deprecated
    public static EmbedBuilder getError(String errorCode) {
        ArrayList<String> errorGifs = (ArrayList<String>) Main.getErrorGifs();
        int rand = new Random().nextInt(errorGifs.size());

        return new EmbedBuilder()
                .setColor(Main.getErrorColor())
                .setAuthor("ERROR")
                .setDescription("It appears that I've encountered an error, oops! Please try running the command once more and if that doesn't work, join my [Discord server](https://support.clairebot.net/) and let us know about the issue."
                        + "\n\nPlease include the following error code: " + errorCode)
                .setImage(errorGifs.get(rand));
    }

    @Deprecated
    public static EmbedBuilder getError(String errorCode, String customMessage) {
        return getError(errorCode).setDescription(customMessage + "\n\nPlease try running the command once more and if that doesn't work, join my [Discord server](https://support.clairebot.net/) and let us know about the issue."
                + "\n\nPlease include the following error code: " + errorCode);
    }

    @Deprecated
    public static EmbedBuilder getCustomError(String errorCode, String message) {
        return getError(errorCode).setDescription(message);
    }

    @Deprecated
    public static EmbedBuilder getLackingPermissions(String message) {
        // Fixed: remove accidental double getErrorCode call
        return getCustomError(Main.getErrorCode("noPerms"), message);
    }
}
