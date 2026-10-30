package com.example.quest504.service;

import com.example.quest504.dto.SignupRequest;
import com.example.quest504.dto.SignupResponse;
import com.example.quest504.entity.Player;
import com.example.quest504.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SignupServiceTest {
    @Autowired
    private SignupService signupService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String newUsername() {
        return "player-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    @Test
    void signupSuccessTest() {
        String username = newUsername();
        String password = "test-password-123";
        SignupResponse response = signupService.signup(new SignupRequest(username, password));

        Player saved = playerRepository.findByUsername(username).orElseThrow();

        assertEquals(username, response.username());
        assertEquals(username, saved.getUsername());
        assertEquals(0L, saved.getGold());

        // 평문으로 저장되지 않았는지 확인
        assertNotEquals(password, saved.getPasswordHash());

        // 저장된 해시로 원래 비밀번호를 검증할 수 있는지 확인
        assertTrue(passwordEncoder.matches(
                password, saved.getPasswordHash()
        ));
    }

    @Test
    void duplicateUsernameTest() {
        String username = newUsername();

        signupService.signup(new SignupRequest(username, "password123"));

        assertThrows(IllegalStateException.class, () ->
                signupService.signup(new SignupRequest(username, "password123")));
    }

    @Test
    void invalidUsernameTest() {
        assertThrows(IllegalArgumentException.class, () ->
                signupService.signup(
                        new SignupRequest("ab", "password123")));

        assertThrows(IllegalArgumentException.class, () ->
                signupService.signup(
                        new SignupRequest("   ", "password123")));
    }

    @Test
    void invalidPasswordTest() {
        assertThrows(IllegalArgumentException.class, () ->
                signupService.signup(
                        new SignupRequest(newUsername(), "short")));

        assertThrows(IllegalArgumentException.class, () ->
                signupService.signup(
                        new SignupRequest(newUsername(), "        ")));
    }
}
