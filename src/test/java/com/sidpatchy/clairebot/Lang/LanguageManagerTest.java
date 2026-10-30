package com.sidpatchy.clairebot.Lang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageManagerTest {
    private static final Locale EN_US = Locale.forLanguageTag("en-US");

    @Test
    void nullLikeDatabaseValuesUseFallbackLocale() {
        for (String value : new String[]{null, "", " null ", "NULL", "und"}) {
            assertEquals(EN_US, LanguageManager.normalizeLocale(value, EN_US));
        }
        assertEquals(Locale.forLanguageTag("ja-JP"), LanguageManager.normalizeLocale("ja_JP", EN_US));
    }

    @Test
    void unknownOrNullLocaleUsesExistingFallbackFile(@TempDir Path translations) throws IOException {
        Files.writeString(translations.resolve("lang_en-US.yml"),
                "Greeting: Hello\nChoices:\n  - Yes\n  - No\n");
        ContextManager context = new ContextManager(null, null, null, null, null);
        LanguageManager manager = new LanguageManager(translations.toString(), EN_US, context);

        assertEquals("Hello", manager.getLocalizedString("Greeting"));
        assertEquals(List.of("Yes", "No"), manager.getLocalizedList("Choices"));
        assertEquals("Hello", manager.getLangFileByLocale(Locale.forLanguageTag("null")).getString("Greeting"));
        assertEquals("Hello", manager.getLangFileByLocale(Locale.forLanguageTag("fr-CA")).getString("Greeting"));
    }

    @Test
    void invalidConfiguredFallbackDefaultsToEnglish(@TempDir Path translations) throws IOException {
        Files.writeString(translations.resolve("lang_en-US.yml"), "Greeting: Hello\n");
        ContextManager context = new ContextManager(null, null, null, null, null);
        LanguageManager manager = new LanguageManager(translations.toString(), Locale.forLanguageTag("null"), context);

        assertEquals(EN_US, manager.getEffectiveLocale());
        assertEquals("Hello", manager.getLocalizedString("Greeting"));
    }
}
