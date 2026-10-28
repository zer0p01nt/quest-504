package com.example.quest504.controller;

import com.example.quest504.domain.baseball.JudgeResult;
import com.example.quest504.dto.StartGameResponse;
import com.example.quest504.dto.SubmitGuessRequest;
import com.example.quest504.dto.SubmitGuessResponse;
import com.example.quest504.service.CurrentPlayerService;
import com.example.quest504.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    private final CurrentPlayerService currentPlayerService;

    public GameController(GameService gameService, CurrentPlayerService currentPlayerService) {
        this.gameService = gameService;
        this.currentPlayerService = currentPlayerService;
    }

    @PostMapping
    public ResponseEntity<StartGameResponse> startGame(Principal principal) {
        // 1. principal.getName()으로 username 가져오기
        String username = principal.getName();

        // 2. CurrentPlayerService로 playerId 조회
        Long playerId = currentPlayerService.getPlayerId(username);

        // 3. GameService.startGame(playerId) 호출
        Long gameId = gameService.startGame(playerId);

        // 4. 기존처럼 201 Created 반환
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StartGameResponse(gameId));
    }

    @PostMapping("/{gameId}/guesses")
    public SubmitGuessResponse submitGuess(
            @PathVariable Long gameId,
            @RequestBody SubmitGuessRequest request,
            Principal principal
    ) {


        // 1. principal에서 username 가져오기
        String username = principal.getName();

        // 2. CurrentPlayerService로 playerId 가져오기
        Long playerId = currentPlayerService.getPlayerId(username);

        // 3. gameService.submitGuess(playerId, gameId, request.guess())
        JudgeResult result = gameService.submitGuess(playerId, gameId, request.guess());

        // 4. JudgeResult에서 strikes와 balls 가져오기
        int strikes = result.strikes();
        int balls = result.balls();

        // 5. strikes가 3인지 확인해 solved 결정
        boolean solved = strikes == 3;

        // 6. SubmitGuessResponse 반환
        return new SubmitGuessResponse(strikes, balls, solved);
    }
}
