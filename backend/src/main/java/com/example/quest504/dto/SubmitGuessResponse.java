package com.example.quest504.dto;

public record SubmitGuessResponse(
        int strikes,
        int balls,
        boolean solved
) {
}
