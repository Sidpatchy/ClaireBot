package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Main;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;

import java.io.File;
import java.util.*;

public class UserPreferencesComponents {

    public static ActionRow getMainMenu(LanguageManager languageManager) {
        String placeholder = languageManager.getLocalizedString("ClaireLang.Generic.ClickToDisplaySettings");

        String accentLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.AccentColour");
        String accentDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.AccentColourDescription");

        String languageLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.Language");
        String languageDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.LanguageDescription");

        return ActionRow.of(StringSelectMenu.create("settings").setPlaceholder(placeholder)
                .addOptions(
                        // Keep values stable for interaction handlers
                        SelectOption.of(accentLabel, "Accent Colour Editor").withDescription(accentDesc),
                        SelectOption.of(languageLabel, "Language Editor").withDescription(languageDesc)
                ).build());
    }

    public static ActionRow getAccentColourMenu(LanguageManager languageManager) {
        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.AccentColourPlaceholder");

        String commonLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.SelectCommonColours");
        String commonDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.SelectCommonColoursDescription");

        String hexLabel = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.HexadecimalEntry");
        String hexDesc = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.HexadecimalEntryDescription");

        return ActionRow.of(StringSelectMenu.create("accent-color").setPlaceholder(placeholder)
                .addOptions(
                        // Keep values stable for interaction handlers
                        SelectOption.of(commonLabel, "Select Common Colours").withDescription(commonDesc),
                        SelectOption.of(hexLabel, "Hexadecimal Entry").withDescription(hexDesc)
                ).build());
    }

    public static ActionRow getAccentColourList(LanguageManager languageManager) {
        // English canonical names -> hex codes
        Map<String, String> colours = new LinkedHashMap<>() {{
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
            put("NRAX Orange", "fb7101");
            put("Deep Orange", "b6580b");
            put("White", "ffffff");
            put("Grey", "808080");
            put("Black", "0a0a0a");
        }};

        // Map canonical English names to localization keys
        Map<String, String> colorLocalizationKeys = new HashMap<>() {{
            put("ClaireBot Blue", "ClaireLang.Colors.ClaireBotBlue");
            put("Red", "ClaireLang.Colors.Red");
            put("Pink", "ClaireLang.Colors.Pink");
            put("Purple", "ClaireLang.Colors.Purple");
            put("Deep Purple", "ClaireLang.Colors.DeepPurple");
            put("Indigo", "ClaireLang.Colors.Indigo");
            put("Blue", "ClaireLang.Colors.Blue");
            put("Light Blue", "ClaireLang.Colors.LightBlue");
            put("Cyan", "ClaireLang.Colors.Cyan");
            put("Teal", "ClaireLang.Colors.Teal");
            put("Green", "ClaireLang.Colors.Green");
            put("Olive", "ClaireLang.Colors.Olive");
            put("Light Green", "ClaireLang.Colors.LightGreen");
            put("Lime", "ClaireLang.Colors.Lime");
            put("Yellow", "ClaireLang.Colors.Yellow");
            put("Amber", "ClaireLang.Colors.Amber");
            put("NRAX Orange", "ClaireLang.Colors.NRAXOrange");
            put("Deep Orange", "ClaireLang.Colors.DeepOrange");
            put("White", "ClaireLang.Colors.White");
            put("Grey", "ClaireLang.Colors.Grey");
            put("Black", "ClaireLang.Colors.Black");
        }};

        List<SelectOption> selectMenuOptionList = new ArrayList<>();

        for (Map.Entry<String, String> color : colours.entrySet()) {
            String englishName = color.getKey();
            String hex = color.getValue();

            // Localized display label; fallback to the English name if key missing
            String localizationKey = colorLocalizationKeys.get(englishName);
            String localizedLabel = localizationKey != null
                    ? languageManager.getLocalizedString(localizationKey)
                    : englishName;

            // Keep the value stable as the English canonical name
            selectMenuOptionList.add(SelectOption.of(localizedLabel, englishName).withDescription("#" + hex));
        }

        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.AccentColourPlaceholder");

        return ActionRow.of(StringSelectMenu.create("accent-color").setPlaceholder(placeholder)
                .addOptions(selectMenuOptionList).build());
    }

    public static Label getAccentColourHexEntry(LanguageManager languageManager) {
        String label = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.HexEntryPlaceholder");
        return Label.of(label, TextInput.create("hex-entry-field", TextInputStyle.SHORT).build());
    }

    public static ActionRow getLanguageMenu(LanguageManager languageManager) {
        String placeholder = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.UserPreferences.LanguagePlaceholder");

        // Discover available language files under config/translations
        File dir = new File(Main.getTranslationsPath());
        List<SelectOption> options = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.startsWith("lang_") && name.endsWith(".yml"));
            if (files != null) {
                // Keep deterministic order
                Arrays.sort(files, Comparator.comparing(File::getName));
                for (File f : files) {
                    String name = f.getName();
                    // Example: lang_en-US.yml -> en-US
                    String tag = name.substring("lang_".length(), name.length() - ".yml".length());
                    // Try to read friendly display name from file header or use tag as fallback
                    String label = tag; // Minimal; can be localized in future via file metadata
                    // Value must be stable and languageManager-independent; use tag
                    options.add(SelectOption.of(label, tag));
                }
            }
        }

        if (options.isEmpty()) {
            // Fallback to at least allow selecting fallback locale
            String tag = Main.getFallbackLocale().toLanguageTag();
            options.add(SelectOption.of(tag, tag));
        }

        return ActionRow.of(StringSelectMenu.create("user-language").setPlaceholder(placeholder)
                .addOptions(options).build());
    }
}
