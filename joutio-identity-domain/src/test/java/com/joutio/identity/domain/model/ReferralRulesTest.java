package com.joutio.identity.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReferralRulesTest {

    @Test
    void newCode_usesUnambiguousAlphabet() {
        String code = ReferralRules.newCode(new Random(42));
        assertEquals(ReferralRules.CODE_LENGTH, code.length());
        assertTrue(code.chars().allMatch(c -> ReferralRules.ALPHABET.indexOf(c) >= 0));
    }

    @Test
    void normalizeCode_acceptsLowercaseAndSpaces() {
        assertEquals("NADJ7K", ReferralRules.normalizeCode(" nad j7k "));
    }

    @Test
    void normalizeCode_rejectsAmbiguousOrWrongLength() {
        assertNull(ReferralRules.normalizeCode("NADJ0K"));
        assertNull(ReferralRules.normalizeCode("NADJ7"));
        assertNull(ReferralRules.normalizeCode(null));
    }
}
