package com.sidpatchy.clairebot.Embed;

import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;
import java.util.Random;

/**
 * Called when ClaireBot encounters (and catches) an error, ideally never.
 */
public class ErrorEmbed {

    public static EmbedBuilder getError(String errorCode) {

        ArrayList<String> errorGifs = (ArrayList<String>) Main.getErrorGifs();

        Random random = new Random();
        int rand = random.nextInt(errorGifs.size());

        return new EmbedBuilder()
                .setColor(Main.getErrorColor())
                .setAuthor("ERROR")
                .setDescription("It appears that I've encountered an error, oops! Please try running the command once more and if that doesn't work, join my [Discord server](https://support.clairebot.net/) and let us know about the issue."
                + "\n\nPlease include the following error code: " + errorCode)
                .setImage(errorGifs.get(rand));
    }

    public static EmbedBuilder getError(String errorCode, String customMessage) {
        return getError(errorCode).setDescription(customMessage + "\n\nPlease try running the command once more and if that doesn't work, join my [Discord server](https://support.clairebot.net/) and let us know about the issue."
                + "\n\nPlease include the following error code: " + errorCode);
    }

    public static EmbedBuilder getCustomError(String errorCode, String message) {
        return getError(errorCode).setDescription(message);
    }
}
