package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;

public class SantaModal {

    public static Label getRulesRow(LanguageManager languageManager) {
        String rules = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.SantaModal.rules-row");

        return Label.of(rules, TextInput.create("rules-row", TextInputStyle.PARAGRAPH).build());
    }

    public static Label getThemeRow(LanguageManager languageManager) {
        String theme = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.SantaModal.theme-row");

        return Label.of("Theme...", TextInput.create("theme-row", TextInputStyle.PARAGRAPH).build());
    }

}
