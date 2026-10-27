package com.example.quest504.controller;

import com.example.quest504.domain.baseball.JudgeResult;
import com.example.quest504.dto.StartGameRequest;
import com.example.quest504.dto.StartGameResponse;
import com.example.quest504.dto.SubmitGuessRequest;
import com.example.quest504.dto.SubmitGuessResponse;
import com.example.quest504.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<StartGameResponse> startGame(@RequestBody StartGameRequest request) {
        // 1. request.playerId() 가져오기
        Long playerId = request.playerId();

        // 2. gameService.startGame() 호출
        Long gameId = gameService.startGame(playerId);

        // 3. 생성된 gameId를 StartGameResponse에 담아 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new StartGameResponse(gameId));
    }

    @PostMapping("/{gameId}/guesses")
    public SubmitGuessResponse submitGuess(
            @PathVariable Long gameId,
            @RequestBody SubmitGuessRequest request
    ) {
        // 1. GameService.submitGuess() 호출 (playerId, gameId, guess 전달)
        JudgeResult result = gameService.submitGuess(request.playerId(), gameId, request.guess());

        // 2. JudgeResult에서 strikes와 balls 가져오기
        int strikes = result.strikes();
        int balls = result.balls();

        // 3. strikes가 3인지 확인해 solved 결정
        boolean solved = strikes == 3;

        // 4. SubmitGuessResponse 반환
        return new SubmitGuessResponse(strikes, balls, solved);
    }
}
