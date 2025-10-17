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
}
