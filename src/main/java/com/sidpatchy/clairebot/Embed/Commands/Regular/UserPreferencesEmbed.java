package com.sidpatchy.clairebot.Embed.Commands.Regular;

import com.sidpatchy.clairebot.API.APIUser;
import com.sidpatchy.clairebot.Embed.ErrorEmbed;
import com.sidpatchy.clairebot.Main;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class UserPreferencesEmbed {

    /**
     *
     * @param author
     * @return
     */
    public static EmbedBuilder getMainMenu(User author) {
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor("User Preferences Editor");
    }

    public static EmbedBuilder getAccentColourMenu(User author) {
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor("Accent Colour Editor");
    }

    public static EmbedBuilder getAccentColourListMenu(User author) {
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor("Accent Colour List");
    }

    /**
     * Response when an accent colour has been selected. Updates colour based off passed colourCode.
     *
     * @param author User updating their colour
     * @param colourCode colour code selected
     * @return embed
     */
    public static EmbedBuilder getAcknowledgeAccentColourChange(User author, String colourCode) {
        Color color = Color.decode(colourCode);

        try {
            APIUser user = new APIUser(author.getIdAsString());
            user.getUser();
            user.updateUserColour(colourCode);

            return new EmbedBuilder()
                    .setColor(color)
                    .setAuthor("Accent Colour Changed!")
                    .setDescription("Your accent colour has been changed to " + colourCode);
        }
        catch (Exception e){
            e.printStackTrace();
            return ErrorEmbed.getError(Main.getErrorCode("updateAccentColour"));
        }


    }

    public static EmbedBuilder getLanguageMenu(User author) {
        return new EmbedBuilder()
                .setColor(Main.getColor(author.getIdAsString()))
                .setAuthor("NYI")
                .setDescription("Sorry, this feature is not yet implemented.");
    }
}
