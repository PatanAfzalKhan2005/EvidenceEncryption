package com.imageseal.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Sha256ServiceTest {
    @Test void exactCopyMatchesButModifiedBytesDoNot() {
        var sha = new Sha256Service();
        String original = sha.hash("original image bytes".getBytes());
        assertTrue(sha.matches(original, sha.hash("original image bytes".getBytes())));
        assertFalse(sha.matches(original, sha.hash("modified image bytes".getBytes())));
        assertEquals(64, original.length());
    }
}
