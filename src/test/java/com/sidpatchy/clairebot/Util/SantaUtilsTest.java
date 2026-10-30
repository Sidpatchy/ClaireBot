package com.sidpatchy.clairebot.Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SantaUtilsTest {

    @Test
    void extractsReceiverFromCurrentAndLegacyMentions() {
        assertEquals("704244031772950528", SantaUtils.getReceiverID("<@123456789012345678> → <@704244031772950528>"));
        assertEquals("704244031772950528", SantaUtils.getReceiverID("<@!123456789012345678> → <@!704244031772950528>"));
        assertEquals("704244031772950528", SantaUtils.getReceiverID("<@123456789012345678> → 704244031772950528"));
    }

    @Test
    void rejectsMalformedPairs() {
        assertThrows(IllegalArgumentException.class, () -> SantaUtils.getReceiverID("<@123> → <@<@704244031772950528>"));
        assertThrows(IllegalArgumentException.class, () -> SantaUtils.getReceiverID("<@123> <@704244031772950528>"));
    }
}
