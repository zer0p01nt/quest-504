package com.example.quest504.service;

import com.example.quest504.entity.Game;
import com.example.quest504.entity.Player;
import com.example.quest504.repository.GameRepository;
import com.example.quest504.repository.PlayerRepository;
import com.example.quest504.repository.RewardHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class RewardConcurrencyTest {
    @Autowired
    private RewardService rewardService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RewardHistoryRepository rewardHistoryRepository;

    @Test
    void concurrentRewardClaimsTest() throws Exception {
        // 1. Player 생성 및 저장
        String username = "player-"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        Player player = new Player(username, "dummy-hash");
        playerRepository.saveAndFlush(player);
        Long playerId = player.getId();

        // 2. 서로 다른 Game 2개 생성, 둘 다 SOLVED로 변경하고 저장
        Game game1 = new Game(player, "427");
        Game game2 = new Game(player, "742");

        game1.solve();
        game2.solve();

        gameRepository.save(game1);
        gameRepository.save(game2);

        // @GeneratedValue(strategy = IDENTITY)로 DB에서 생성해주는 값이므로, 저장된 다음 가져와야 함
        Long game1Id = game1.getId();
        Long game2Id = game2.getId();

        // 3. CountDownLatch로 동시 시작 준비
        CountDownLatch startLatch = new CountDownLatch(1);

        // 4. ExecutorService로 작업을 실행할 스레드 2개 준비
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            // 5. 각 스레드에서 claimReward() 호출
            // 첫 번째 스레드에 작업 맡기기 (game1)
            Future<Long> future1 = executor.submit(() -> {
                startLatch.await(); // 출발 신호 기다림
                return rewardService.claimReward(playerId, game1Id); // 결과는 future1.get()으로 받음
            });

            // 두 번째 스레드에 작업 맡기기 (game2)
            Future<Long> future2 = executor.submit(() -> {
                startLatch.await(); // 출발 신호 기다림
                return rewardService.claimReward(playerId, game2Id); // 결과는 future2.get()으로 받음
            });

            // 두 스레드에게 출발 신호 보내기
            startLatch.countDown();

            // 6. 두 작업이 끝날 때까지 기다리고 결과 확인 (get()은 작업 끝날 때까지 기다림)
            assertEquals(100L, future1.get());
            assertEquals(100L, future2.get());
        }

        // 7. Player를 다시 조회해서 gold = 200 확인
        Player testPlayer = playerRepository.findById(playerId).orElseThrow();
        assertEquals(200L, testPlayer.getGold());

        // 8. 두 Game의 보상 이력이 각각 1개인지 확인
        assertEquals(1L, rewardHistoryRepository.countByGame(game1));
        assertEquals(1L, rewardHistoryRepository.countByGame(game2));
    }
}
