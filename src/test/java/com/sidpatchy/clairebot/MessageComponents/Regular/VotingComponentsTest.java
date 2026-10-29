package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Lang.PlaceholderHandler;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.TextInput;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VotingComponentsTest {
    private static final String PREFIX = "ClaireLang.Embed.Commands.Components.Regular.Voting.";

    @Test
    void pollMenuResolvesCommandNameBeforeReadingTranslation() {
        LanguageManager languageManager = languageManager();

        SelectMenu menu = (SelectMenu) VotingComponents.getMultipleChoicesRow(languageManager, "poll", "draft")
                .getComponents().getFirst();

        assertEquals("Submits poll after clicking submit",
                menu.getOptions().get(1).getDescription().orElseThrow());
    }

    @Test
    void optionModalResolvesNumberBeforeReadingTranslation() {
        LanguageManager languageManager = languageManager();

        TextInput input = (TextInput) VotingComponents.getOptionActionRow(10, languageManager, false)
                .getComponents().getFirst();

        assertEquals("Option #10", input.getLabel().orElseThrow());
    }

    private static LanguageManager languageManager() {
        ContextManager context = new ContextManager(null, null, null, null, null, new HashMap<>());
        Map<String, String> translations = Map.of(
                PREFIX + "MultipleChoicePlaceholder", "Choose an option",
                PREFIX + "MultipleChoiceYesDescription", "Add choices",
                PREFIX + "MultipleChoiceNoDescription", "Submits {cb.commandname} after clicking submit",
                PREFIX + "OptionLabelTemplate", "Option #{cb.voting.optionnumber}",
                "ClaireLang.Generic.Yes", "Yes",
                "ClaireLang.Generic.No", "No"
        );
        return new LanguageManager(Locale.US, context) {
            @Override
            public String getLocalizedString(String key) {
                return new PlaceholderHandler(context).process(translations.get(key));
            }
        };
    }
}
