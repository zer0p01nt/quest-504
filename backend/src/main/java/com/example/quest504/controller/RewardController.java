package com.example.quest504.controller;

import com.example.quest504.dto.ClaimRewardRequest;
import com.example.quest504.dto.ClaimRewardResponse;
import com.example.quest504.service.RewardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class RewardController {
    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @PostMapping("/{gameId}/reward")
    public ClaimRewardResponse claimReward(
            @PathVariable Long gameId,
            @RequestBody ClaimRewardRequest request
    ) {
        // 1. request.playerId() 가져오기
        Long playerId = request.playerId();

        // 2. rewardService.claimReward() 호출
        long rewardAmount = rewardService.claimReward(playerId, gameId);

        // 3. 반환된 보상 금액을 ClaimRewardResponse에 담아 반환
        return new ClaimRewardResponse(rewardAmount);
    }
}
