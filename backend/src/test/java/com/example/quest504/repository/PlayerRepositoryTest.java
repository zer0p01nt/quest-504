package com.example.quest504.repository;

import com.example.quest504.entity.Player;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
public class PlayerRepositoryTest {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saveAndFindPlayerTest() {
        // 1. Player 객체 생성
        Player player = new Player("testplayer", "dummy-hash");

        // 2. Repository로 저장
        playerRepository.saveAndFlush(player);
        entityManager.clear();

        // 3. 저장된 Player 조회 및 검증
        Player testPlayer = playerRepository.findById(player.getId()).orElseThrow();
        assertEquals("testplayer", testPlayer.getUsername());
        assertEquals(0, testPlayer.getGold());
    }
}
