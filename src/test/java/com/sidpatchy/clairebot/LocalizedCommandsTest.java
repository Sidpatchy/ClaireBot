package com.sidpatchy.clairebot;

import com.sidpatchy.Robin.Discord.Command;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizedCommandsTest {

    private static final List<String> COMMAND_FILES = List.of(
            "commands_en-US.yml",
            "commands_en-GB.yml",
            "commands_en-PIRATE.yml",
            "commands_en-Corp.yml"
    );

    private static final List<String> COMMAND_KEYS = List.of(
            "avatar", "config", "eightball", "help", "info", "leaderboard", "level",
            "poll", "quote", "request", "santa", "server", "user"
    );

    private final YAMLMapper yamlMapper = YAMLMapper.builder().build();

    @Test
    void localizedFilesParseAndKeepCommandNamesStable() throws IOException {
        Commands english = load("commands/commands_en-US.yml");
        Map<String, Command> englishByKey = english.getCommandsByKey();

        for (String fileName : COMMAND_FILES) {
            Commands localized = load("commands/" + fileName);
            Map<String, Command> localizedByKey = localized.getCommandsByKey();
            assertEquals(englishByKey.keySet(), localizedByKey.keySet(), fileName + " command keys differ");

            for (Map.Entry<String, Command> entry : englishByKey.entrySet()) {
                Command translated = localizedByKey.get(entry.getKey());
                assertEquals(entry.getValue().getName(), translated.getName(), fileName + " translated a command name");
                assertFalse(translated.getHelp().isBlank(), fileName + " has empty help for " + entry.getKey());
                assertFalse(translated.getOverview().isBlank(), fileName + " has empty overview for " + entry.getKey());
                assertFalse(translated.getUsage().isBlank(), fileName + " has empty usage for " + entry.getKey());
                assertTrue(translated.getOverview().length() <= 100,
                        fileName + " description exceeds Discord's 100-character limit for " + entry.getKey());
            }
        }
    }

    @Test
    void translatorTemplateHasBlankHelpAndOverview() throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("commands/commands_TEMPLATE.yml")) {
            assertNotNull(input, "Missing command translator template");
            Map<String, Object> template = yamlMapper.readValue(input, new TypeReference<>() {});
            for (String commandKey : COMMAND_KEYS) {
                Object commandValue = template.get(commandKey);
                assertTrue(commandValue instanceof Map<?, ?>, "Missing command entry: " + commandKey);
                Map<?, ?> command = (Map<?, ?>) commandValue;
                assertEquals("", command.get("help"), commandKey + " template help should be blank");
                assertEquals("", command.get("overview"), commandKey + " template overview should be blank");
            }
        }
    }

    private Commands load(String resourcePath) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            assertNotNull(input, "Missing command resource: " + resourcePath);
            return yamlMapper.readValue(input, Commands.class);
        }
    }
}
