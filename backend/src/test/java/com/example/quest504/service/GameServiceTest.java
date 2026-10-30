package com.example.quest504.service;

import com.example.quest504.domain.baseball.JudgeResult;
import com.example.quest504.entity.Game;
import com.example.quest504.entity.GameStatus;
import com.example.quest504.entity.Player;
import com.example.quest504.repository.GameRepository;
import com.example.quest504.repository.PlayerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void startGameTest() {
        // 1. 테스트용 Player 생성 및 저장
        Player player = new Player("player-" + UUID.randomUUID(), "dummy-hash");
        Player testPlayer = playerRepository.saveAndFlush(player);

        // 2. gameService.startGame(playerId) 호출
        Long gameId = gameService.startGame(testPlayer.getId());

        // DB와 동기화하고 1차 캐시 비우기
        entityManager.flush();
        entityManager.clear();

        // 3. 반환된 gameId로 Game 조회
        Game game = gameRepository.findById(gameId).orElseThrow();

        // 4. Game 상태가 PLAYING인지
        assertEquals(GameStatus.PLAYING, game.getStatus());

        // 5. Game의 Player ID가 일치하는지 검증
        assertEquals(player.getId(), game.getPlayer().getId());
    }

    @Test
    void startGameWithUnknownPlayerTest() {
        Long invalidPlayerId = -1L;

        assertThrows(NoSuchElementException.class, () -> gameService.startGame(invalidPlayerId));
    }

    @Test
    void submitCorrectGuessTest() {
        // 1. Player 생성 및 저장
        Player player = new Player("player-" + UUID.randomUUID(), "dummy-hash");
        playerRepository.saveAndFlush(player);

        // 2. 정답이 427인 Game 생성 및 저장
        Game game = new Game(player, "427");
        gameRepository.saveAndFlush(game);

        // 3. submitGuess() 실행
        JudgeResult result = gameService.submitGuess(player.getId(), game.getId(), "427");

        // 4. JudgeResult가 3S 0B인지 검증
        assertEquals(3, result.strikes());
        assertEquals(0, result.balls());

        // 5. flush()와 clear() 실행
        entityManager.flush();
        entityManager.clear();

        // 6. Game을 다시 조회해서 SOLVED인지 검증
        Game savedGame = gameRepository.findById(game.getId()).orElseThrow();
        assertEquals(GameStatus.SOLVED, savedGame.getStatus());
        assertNotNull(savedGame.getSolvedAt());
    }

    @Test
    void submitIncorrectGuessTest() {
        // 1. Player 생성 및 저장
        Player player = new Player("player-" + UUID.randomUUID(), "dummy-hash");
        playerRepository.saveAndFlush(player);

        // 2. 정답이 427인 Game 생성 및 저장
        Game game = new Game(player, "427");
        gameRepository.saveAndFlush(game);

        // 3. submitGuess() 실행
        JudgeResult result = gameService.submitGuess(player.getId(), game.getId(), "472");

        // 4. JudgeResult가 1S 2B인지 검증
        assertEquals(1, result.strikes());
        assertEquals(2, result.balls());

        // 5. flush()와 clear() 실행
        entityManager.flush();
        entityManager.clear();

        // 6. Game을 다시 조회해서 PLAYING인지 검증
        Game savedGame = gameRepository.findById(game.getId()).orElseThrow();
        assertEquals(GameStatus.PLAYING, savedGame.getStatus());
        assertNull(savedGame.getSolvedAt());
    }

    @Test
    void submitGuessToSolvedGameTest() {
        // 1. Player 생성 및 저장
        Player player = new Player("player-" + UUID.randomUUID(), "dummy-hash");
        playerRepository.saveAndFlush(player);

        // 2. Game 생성 및 저장 (정답: "427")
        Game game = new Game(player, "427");
        gameRepository.saveAndFlush(game);

        // 3. 정답 "427"을 제출해 SOLVED 상태로 변경
        gameService.submitGuess(player.getId(), game.getId(), "427");

        // 4. 같은 Game에 다시 "427" 제출 후 assertThrows()로 예외 발생 확인
        assertThrows(IllegalStateException.class,
                () -> gameService.submitGuess(player.getId(), game.getId(), "427"));
    }

    @Test
    void submitGuessByOtherPlayerTest() {
        Player player1 = new Player("player1-" + UUID.randomUUID(), "dummy-hash");
        Player player2 = new Player("player2-" + UUID.randomUUID(), "dummy-hash");

        playerRepository.saveAndFlush(player1);
        playerRepository.saveAndFlush(player2);

        // 1. player1 소유의 Game 생성
        Game game = new Game(player1, "427");
        gameRepository.saveAndFlush(game);

        // 2. player2의 ID로 submitGuess() 호출 후 assertThrows()로 예외 발생 확인
        assertThrows(SecurityException.class,
                () -> gameService.submitGuess(player2.getId(), game.getId(), "427"));
    }
}
