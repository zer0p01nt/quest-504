package com.example.quest504.security;

import com.example.quest504.entity.Player;
import com.example.quest504.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class DevPlayerSeedTest {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createDevPlayer() {
        String username = "devplayer";
        String password = "test1234";

        if (playerRepository.findByUsername(username).isEmpty()) {
            String passwordHash = passwordEncoder.encode(password);

            Player player = new Player(username, passwordHash);
            playerRepository.save(player);
        }
    }
}
