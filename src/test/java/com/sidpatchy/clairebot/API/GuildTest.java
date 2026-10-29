package com.sidpatchy.clairebot.API;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GuildTest {
    @Test
    void absentOrInvalidEnforcementSettingDefaultsToFalse() {
        Guild guild = new Guild("123");

        assertFalse(guild.isEnforceSeverLanguage());
        guild.guild.set("enforceServerLanguage", "invalid");
        assertFalse(guild.isEnforceSeverLanguage());
        guild.guild.set("enforceServerLanguage", false);
        assertFalse(guild.isEnforceSeverLanguage());
    }

    @Test
    void enforcementSettingAcceptsBooleanAndStoredString() {
        Guild guild = new Guild("123");

        guild.guild.set("enforceServerLanguage", true);
        assertTrue(guild.isEnforceSeverLanguage());
        guild.guild.set("enforceServerLanguage", "true");
        assertTrue(guild.isEnforceSeverLanguage());
        guild.guild.set("enforceServerLanguage", "false");
        assertFalse(guild.isEnforceSeverLanguage());
    }
}
