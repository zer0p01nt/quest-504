package com.example.quest504.controller;

import com.example.quest504.dto.StartGameRequest;
import com.example.quest504.dto.StartGameResponse;
import com.example.quest504.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<StartGameResponse> startGame(@RequestBody StartGameRequest startGameRequest) {
        // 1. request.playerId() 가져오기
        Long playerId = startGameRequest.playerId();

        // 2. gameService.startGame() 호출
        Long gameId = gameService.startGame(playerId);

        // 3. 생성된 gameId를 StartGameResponse에 담아 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new StartGameResponse(gameId));
    }
}
