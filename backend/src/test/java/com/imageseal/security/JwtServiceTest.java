package com.imageseal.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {
    @Test void issuedTokenContainsUserIdentity() {
        JwtService service = new JwtService("test-secret-that-is-at-least-32-characters-long", 60_000);
        var claims = service.parse(service.issue("user-123", "person@example.com"));
        assertEquals("user-123", claims.getSubject());
        assertEquals("person@example.com", claims.get("email", String.class));
    }
}
