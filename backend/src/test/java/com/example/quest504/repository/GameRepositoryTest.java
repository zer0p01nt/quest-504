package com.example.quest504.repository;

import com.example.quest504.entity.Game;
import com.example.quest504.entity.GameStatus;
import com.example.quest504.entity.Player;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@ActiveProfiles("test")
class GameRepositoryTest {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saveAndFindGameTest() {
        // Player 저장
        Player player = new Player("player-" + UUID.randomUUID(), "dummy-hash");
        playerRepository.saveAndFlush(player);

        // 저장한 Player로 Game 생성
        Game game = new Game(player, "123");

        // Game 저장
        gameRepository.saveAndFlush(game);

        // 1차 캐시 비우고 Game 다시 조회
        entityManager.clear();
        Game testGame = gameRepository.findById(game.getId()).orElseThrow();

        assertEquals(GameStatus.PLAYING, testGame.getStatus());
        assertEquals(player.getId(), testGame.getPlayer().getId());
    }
}
