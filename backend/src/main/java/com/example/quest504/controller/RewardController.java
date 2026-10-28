package com.example.quest504.controller;

import com.example.quest504.dto.ClaimRewardResponse;
import com.example.quest504.service.CurrentPlayerService;
import com.example.quest504.service.RewardService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/games")
public class RewardController {
    private final RewardService rewardService;

    private final CurrentPlayerService currentPlayerService;

    public RewardController(RewardService rewardService, CurrentPlayerService currentPlayerService) {
        this.rewardService = rewardService;
        this.currentPlayerService = currentPlayerService;
    }

    @PostMapping("/{gameId}/reward")
    public ClaimRewardResponse claimReward(
            @PathVariable Long gameId,
            Principal principal
    ) {
        // 1. playerId 가져오기
        Long playerId = currentPlayerService.getPlayerId(principal.getName());

        // 2. rewardService.claimReward() 호출
        long rewardAmount = rewardService.claimReward(playerId, gameId);

        // 3. 반환된 보상 금액을 ClaimRewardResponse에 담아 반환
        return new ClaimRewardResponse(rewardAmount);
    }
}
