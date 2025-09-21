package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.LanguageManager;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;
import org.javacord.api.entity.user.User;

public class SantaModal {

    public static ActionRow getRulesRow(LanguageManager languageManager) {
        String rules = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.SantaModal.rules-row");

        return ActionRow.of(TextInput.create(TextInputStyle.PARAGRAPH, "rules-row", rules));
    }

    public static ActionRow getThemeRow(LanguageManager languageManager) {
        String theme = languageManager.getLocalizedString("ClaireLang.Embed.Commands.Components.Regular.SantaModal.theme-row");

        return ActionRow.of(TextInput.create(TextInputStyle.PARAGRAPH, "theme-row", "Theme..."));
    }

}
