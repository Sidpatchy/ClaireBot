package com.sidpatchy.clairebot;

import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VotingTranslationsTest {
    private static final List<String> FILES = List.of("en-US", "en-GB", "en-PIRATE", "en-Corp", "es-ES", "ja-JP");
    private static final List<String> KEYS = List.of("ChoicesPrompt", "AllowMultipleChoices", "ChoicesTitle",
            "AddMoreChoices", "Publish", "Cancel", "ReadyToPublish", "TooFewChoices",
            "DraftUnavailable", "Cancelled", "PublishFailed", "PollPublished");

    @Test
    void votingWizardTranslationsAreCompleteAndFitDiscordComponents() throws IOException {
        YAMLMapper mapper = YAMLMapper.builder().build();
        for (String locale : FILES) {
            try (InputStream input = getClass().getClassLoader()
                    .getResourceAsStream("translations/lang_" + locale + ".yml")) {
                assertNotNull(input, locale);
                Map<String, Object> yaml = mapper.readValue(input, new TypeReference<>() {});
                Map<?, ?> section = yaml;
                for (String key : List.of("ClaireLang", "Embed", "Commands", "Components", "Regular", "Voting")) {
                    section = (Map<?, ?>) section.get(key);
                    assertNotNull(section, locale + ": " + key);
                }
                Map<?, ?> voting = section;
                for (String key : KEYS) {
                    String value = (String) voting.get(key);
                    assertNotNull(value, locale + ": " + key);
                    assertTrue(!value.isBlank(), locale + ": " + key);
                }
                for (String key : List.of("ChoicesTitle", "AddMoreChoices", "Publish", "Cancel")) {
                    assertTrue(((String) voting.get(key)).length() <= 45, locale + ": " + key);
                }
                String optionLabel = ((String) voting.get("OptionLabelTemplate"))
                        .replace("{cb.voting.optionnumber}", "10");
                assertTrue(optionLabel.length() <= 45, locale + ": OptionLabelTemplate");
            }
        }
    }
}
