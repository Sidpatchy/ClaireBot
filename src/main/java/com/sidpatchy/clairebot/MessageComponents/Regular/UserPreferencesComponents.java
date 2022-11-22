package com.sidpatchy.clairebot.MessageComponents.Regular;

import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.util.*;

public class UserPreferencesComponents {

    public static ActionRow getMainMenu() {
        return new ActionRowBuilder()
                .addComponents(
                        SelectMenu.create("settings", "Click to display settings", 1, 1,
                                Arrays.asList(SelectMenuOption.create("Accent Colour", "Accent Colour Editor", "For those who hate the colour blue"),
                                        SelectMenuOption.create("Language", "Language Editor", "If you're offended by english")))
                ).build();
    }

    public static ActionRow getAccentColourMenu() {
        return new ActionRowBuilder()
                .addComponents(
                        SelectMenu.create("accent-color", "Click to choose option", 1, 1,
                                Arrays.asList(SelectMenuOption.create("Select Common Colours", "Select Common Colours", "Select from a list of common colours"),
                                        SelectMenuOption.create("Hexadecimal Entry", "Hexadecimal Entry", "Enter any hexadecimal colour")))
                ).build();
    }

    public static ActionRow getAccentColourList() {
        HashMap<String, String> colours = new HashMap<>() {{
            put("ClaireBot Blue", "3498db");
            put("Red", "f44336");
            put("Pink", "e81e63");
            put("Purple", "9c27b0");
            put("Deep Purple", "673ab7");
            put("Indigo", "3f51b5");
            put("Blue", "2196f3");
            put("Light Blue", "03a9f4");
            put("Cyan", "00bcd4");
            put("Teal", "009688");
            put("Green", "4caf50");
            put("Olive", "7d9632");
            put("Light Green", "8bc34a");
            put("Lime", "cddc39");
            put("Yellow", "ffeb3b");
            put("Amber", "ffc107");
            put("Orange", "ff9800");
            put("Deep Orange", "ff5722");
            put("White", "ffffff");
            put("Grey", "808080");
            put("Black", "0a0a0a");
        }};

        List<SelectMenuOption> selectMenuOptionList = new ArrayList<>();

        for (Map.Entry<String, String> color : colours.entrySet()) {
            selectMenuOptionList.add(SelectMenuOption.create(color.getKey(), color.getKey(), "#" + color.getValue()));
        }

        return new ActionRowBuilder()
                .addComponents(
                        SelectMenu.create("accent-color", "Click to choose option", 1, 1, selectMenuOptionList)).build();
    }

    public static ActionRow getAccentColourHexEntry() {
        return new ActionRowBuilder()
                .addComponents(TextInput.create(TextInputStyle.SHORT, "hex-entry-field", "Enter a hex colour ex. #ff8800"))
                .build();
    }
}
