package com.example.quest504.service;

import com.example.quest504.domain.baseball.JudgeResult;
import com.example.quest504.domain.baseball.NumberBaseballGenerator;
import com.example.quest504.domain.baseball.NumberBaseballJudge;
import com.example.quest504.entity.Game;
import com.example.quest504.entity.GameStatus;
import com.example.quest504.entity.Player;
import com.example.quest504.repository.GameRepository;
import com.example.quest504.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class GameService {
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    private final NumberBaseballGenerator generator =  new NumberBaseballGenerator();

    private final NumberBaseballJudge  judge = new NumberBaseballJudge();

    public GameService(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    // 로그인 기능 추가 후 매개변수는 사용자로 변경
    @Transactional
    public Long startGame(Long playerId) {
        // Player 조회
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() ->
                        new NoSuchElementException("플레이어를 찾을 수 없습니다.")
                );

        // 숫자야구 정답 생성
        String secret = generator.generate();

        // Game 객체 생성
        Game game = new Game(player, secret);

        // Game 저장
        Game savedGame = gameRepository.save(game);

        // 저장된 Game의 Id 반환
        return savedGame.getId();
    }

    @Transactional
    public JudgeResult submitGuess(Long playerId, Long gameId, String guess) {
        // 1. gameRepository에서 Game 조회 (존재하지 않으면 예외 발생)
        Game game = gameRepository.findById(gameId).orElseThrow(() ->
                new NoSuchElementException("존재하지 않는 게임입니다.")
        );

        // 2. 해당 게임의 playerId가 요청한 playerId와 같은지 확인 (다르면 예외 발생)
        if (!game.getPlayer().getId().equals(playerId)) {
            throw new SecurityException("자신의 게임만 참여할 수 있습니다.");
        }

        // 3. GameStatus가 PLAYING인지 확인 (SOLVED면 예외 발생)
        if (game.getStatus() != GameStatus.PLAYING) {
            throw new IllegalStateException("이미 완료된 게임입니다.");
        }

        // 4. NumberBaseballJudge로 판정 (secret과 guess 전달)
        JudgeResult result = judge.judge(game.getSecret(), guess);

        // 5. 3 Strike라면 game.solve() 호출
        if (result.strikes() == 3) {
            game.solve();
        }

        // 6. JudgeResult 반환
        return result;
    }
}
