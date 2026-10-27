package com.example.quest504.dto;

public record SubmitGuessRequest(
        Long playerId,
        String guess
) {
}
