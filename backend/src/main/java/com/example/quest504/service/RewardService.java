package com.example.quest504.service;

import com.example.quest504.entity.Game;
import com.example.quest504.entity.GameStatus;
import com.example.quest504.entity.Player;
import com.example.quest504.entity.RewardHistory;
import com.example.quest504.repository.GameRepository;
import com.example.quest504.repository.RewardHistoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RewardService {
    private static final long REWARD_AMOUNT = 100L;

    private final GameRepository gameRepository;
    private final RewardHistoryRepository rewardHistoryRepository;

    public RewardService(GameRepository gameRepository, RewardHistoryRepository rewardHistoryRepository) {
        this.gameRepository = gameRepository;
        this.rewardHistoryRepository = rewardHistoryRepository;
    }

    @Transactional
    public long claimReward(Long playerId, Long gameId) {

        // 1. Game 조회 (없으면 예외)
        Game game = gameRepository.findById(gameId).orElseThrow();

        // 2. 게임 소유자 검증
        Player player = game.getPlayer();
        if (!player.getId().equals(playerId)) {
            throw new SecurityException("다른 사용자의 게임입니다.");
        }

        // 3. SOLVED 상태 검증
        if (game.getStatus() != GameStatus.SOLVED) {
            throw new IllegalStateException("아직 완료되지 않은 게임입니다.");
        }

        // 4. 이미 보상을 수령했는지 확인
        if (rewardHistoryRepository.existsByGame(game)) {
            throw new IllegalStateException("이미 보상을 수령한 게임입니다.");
        }

        // 5. Player의 gold 증가 (일단은 항상 100 골드)
        player.addGold(REWARD_AMOUNT);

        // 6. RewardHistory 생성 및 저장
        RewardHistory rewardHistory = new RewardHistory(game, REWARD_AMOUNT);
        rewardHistoryRepository.save(rewardHistory);

        // 7. 지급한 보상 금액 반환
        return rewardHistory.getRewardAmount();
    }
}
