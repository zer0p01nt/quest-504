package com.example.quest504.service;

import com.example.quest504.entity.Game;
import com.example.quest504.entity.Player;
import com.example.quest504.entity.RewardHistory;
import com.example.quest504.repository.GameRepository;
import com.example.quest504.repository.PlayerRepository;
import com.example.quest504.repository.RewardHistoryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class RewardServiceTest {
    @Autowired
    private RewardService rewardService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RewardHistoryRepository rewardHistoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void claimRewardSuccessTest() {
        // 1. Player 생성 및 저장
        String username = "player-"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Player player = new Player(username, "dummy-hash");
        playerRepository.saveAndFlush(player);
        Long playerId = player.getId();

        // 2. Game 생성 및 저장
        Game game = new Game(player, "427");
        gameRepository.saveAndFlush(game);
        Long gameId = game.getId();

        // 3. game.solve()로 SOLVED 처리
        game.solve();

        // 4. rewardService.claimReward() 호출
        long rewardAmount = rewardService.claimReward(playerId, gameId);

        // 5. 지급 금액이 100인지 확인
        assertEquals(100L, rewardAmount);
        assertEquals(100L, player.getGold());

        // 6. flush(), clear()
        entityManager.flush();
        entityManager.clear();

        // 7. Player 다시 조회 → gold = 100 확인
        Player testPlayer = playerRepository.findById(playerId).orElseThrow();
        assertEquals(100L, testPlayer.getGold());

        // 8. 저장된 Game 다시 조회
        Game savedGame = gameRepository.findById(gameId).orElseThrow();

        // 9. RewardHistory 조회
        RewardHistory history = rewardHistoryRepository
                .findByGame(savedGame)
                .orElseThrow();

        // 10. 지급 금액 검증
        assertEquals(100L, history.getRewardAmount());

        // 11. 보상 이력이 정확히 1개인지 검증
        assertEquals(1L, rewardHistoryRepository.countByGame(savedGame));
    }

    @Test
    void claimRewardBeforeSolvedTest() {
        // 1. Player 생성 및 저장
        String username = "player-"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Player player = new Player(username, "dummy-hash");
        playerRepository.saveAndFlush(player);
        Long playerId = player.getId();

        // 2. PLAYING 상태의 Game 생성
        Game game = new Game(player, "427");
        gameRepository.saveAndFlush(game);
        Long gameId = game.getId();

        // 3. claimReward() 실행 시 IllegalStateException 검증
        assertThrows(
                IllegalStateException.class,
                () -> rewardService.claimReward(playerId, gameId));

        // 4. 골드가 0인지 확인
        assertEquals(0L, player.getGold());

        // 5. 보상 이력이 없는지 확인
        assertEquals(0L, rewardHistoryRepository.countByGame(game));
    }

    @Test
    void claimRewardTwiceTest() {
        // 1. Player 생성 및 저장
        String username = "player-"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Player player = new Player(username, "dummy-hash");
        playerRepository.saveAndFlush(player);
        Long playerId = player.getId();

        // 2. Game 생성 후 SOLVED 처리
        Game game = new Game(player, "427");
        gameRepository.saveAndFlush(game);
        Long gameId = game.getId();

        game.solve();

        // 3. 첫 번째 claimReward() 성공
        rewardService.claimReward(playerId, gameId);

        // 4. 두 번째 claimReward()에서 IllegalStateException 검증
        assertThrows(
                IllegalStateException.class,
                () -> rewardService.claimReward(playerId, gameId)
        );

        // 5. 최종 골드가 100인지 검증
        assertEquals(100L, player.getGold());

        // 6. 보상 이력이 하나인지 검증
        assertEquals(1, rewardHistoryRepository.countByGame(game));
    }
}
