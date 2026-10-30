package com.sidpatchy.clairebot.MessageComponents.Regular;

import com.sidpatchy.clairebot.Lang.ContextManager;
import com.sidpatchy.clairebot.Lang.LanguageManager;
import com.sidpatchy.clairebot.Lang.PlaceholderHandler;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.TextInput;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VotingComponentsTest {
    private static final String PREFIX = "ClaireLang.Embed.Commands.Components.Regular.Voting.";

    @Test
    void pollSetupUsesLocalizedButtonsWithStableIds() {
        LanguageManager languageManager = languageManager();

        ActionRow choices = VotingComponents.getChoicesButtons(languageManager, "draft");
        ActionRow multiple = VotingComponents.getAllowMultipleChoicesButtons(languageManager, "draft");

        assertEquals("vote-choices-yes:draft", ((Button) choices.getComponents().get(0)).getCustomId().orElseThrow());
        assertEquals("Custom choices", ((Button) choices.getComponents().get(0)).getLabel().orElseThrow());
        assertEquals("vote-choices-no:draft", ((Button) choices.getComponents().get(1)).getCustomId().orElseThrow());
        assertEquals("👍/👎 poll", ((Button) choices.getComponents().get(1)).getLabel().orElseThrow());
        assertEquals("vote-cancel:draft", ((Button) choices.getComponents().get(2)).getCustomId().orElseThrow());
        assertEquals("vote-multi-yes:draft", ((Button) multiple.getComponents().get(0)).getCustomId().orElseThrow());
        assertEquals("vote-multi-no:draft", ((Button) multiple.getComponents().get(1)).getCustomId().orElseThrow());
        assertEquals("Select multiple", ((Button) multiple.getComponents().get(0)).getLabel().orElseThrow());
        assertEquals("One choice only", ((Button) multiple.getComponents().get(1)).getLabel().orElseThrow());
        assertEquals("vote-cancel:draft", ((Button) multiple.getComponents().get(2)).getCustomId().orElseThrow());
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
                PREFIX + "OptionLabelTemplate", "Option #{cb.voting.optionnumber}",
                PREFIX + "Cancel", "Cancel",
                PREFIX + "CustomChoicesButton", "Custom choices",
                PREFIX + "ThumbsPollButton", "👍/👎 poll",
                PREFIX + "MultipleSelectionButton", "Select multiple",
                PREFIX + "SingleSelectionButton", "One choice only"
        );
        return new LanguageManager(Locale.US, context) {
            @Override
            public String getLocalizedString(String key) {
                return new PlaceholderHandler(context).process(translations.get(key));
            }
        };
    }
}
