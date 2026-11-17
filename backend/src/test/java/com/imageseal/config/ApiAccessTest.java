package com.imageseal.config;

import com.imageseal.controller.AuthController;
import com.imageseal.controller.HealthController;
import com.imageseal.entity.AppUser;
import com.imageseal.repository.UserRepository;
import com.imageseal.security.JwtAuthFilter;
import com.imageseal.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(controllers = {HealthController.class, AuthController.class})
@Import({SecurityConfig.class, CorsConfig.class, JwtAuthFilter.class})
class ApiAccessTest {
    @Autowired MockMvc mvc;
    @Autowired PasswordEncoder passwordEncoder;
    @MockitoBean UserRepository users;
    @MockitoBean JwtService jwt;

    @Test
    void healthIsPublicAndReturnsUp() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void registrationIsPublicAndIssuesToken() throws Exception {
        AppUser savedUser = mock(AppUser.class);
        when(savedUser.getId()).thenReturn("user-id");
        when(savedUser.getEmail()).thenReturn("person@example.com");
        when(users.findByEmail("person@example.com")).thenReturn(Optional.empty());
        when(users.save(any(AppUser.class))).thenReturn(savedUser);
        when(jwt.issue("user-id", "person@example.com")).thenReturn("signed-token");

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"person@example.com\",\"password\":\"safe-password\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("signed-token"));
    }

    @Test
    void loginIsPublicAndRejectsBadCredentialsAtTheAuthEndpoint() throws Exception {
        when(users.findByEmail("person@example.com")).thenReturn(Optional.empty());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"person@example.com\",\"password\":\"bad-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }

    @Test
    void loginCanIssueTokenWithoutAnExistingJwt() throws Exception {
        AppUser user = mock(AppUser.class);
        when(user.getId()).thenReturn("user-id");
        when(user.getEmail()).thenReturn("person@example.com");
        when(user.getPasswordHash()).thenReturn(passwordEncoder.encode("safe-password"));
        when(users.findByEmail("person@example.com")).thenReturn(Optional.of(user));
        when(jwt.issue("user-id", "person@example.com")).thenReturn("signed-token");

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"person@example.com\",\"password\":\"safe-password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("signed-token"));
    }

    @Test
    void protectedApiStillRequiresJwt() throws Exception {
        mvc.perform(get("/api/images"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void frontendOriginIsAllowedByCors() throws Exception {
        mvc.perform(options("/api/health")
                        .header("Origin", "http://localhost:8082")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8082"));
    }
}
