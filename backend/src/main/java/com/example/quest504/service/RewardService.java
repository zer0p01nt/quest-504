package com.example.quest504.service;

import com.example.quest504.entity.Game;
import com.example.quest504.entity.GameStatus;
import com.example.quest504.entity.Player;
import com.example.quest504.entity.RewardHistory;
import com.example.quest504.repository.GameRepository;
import com.example.quest504.repository.PlayerRepository;
import com.example.quest504.repository.RewardHistoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class RewardService {
    private static final long REWARD_AMOUNT = 100L;

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final RewardHistoryRepository rewardHistoryRepository;

    public RewardService(PlayerRepository playerRepository, GameRepository gameRepository, RewardHistoryRepository rewardHistoryRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.rewardHistoryRepository = rewardHistoryRepository;
    }

    @Transactional
    public long claimReward(Long playerId, Long gameId) {

        // 1. Game 조회 (없으면 예외)
        Game game = gameRepository.findById(gameId).orElseThrow();

        // 2. Game의 소유자 ID 확인
        Long gamePlayerId = game.getPlayer().getId();
        if (!gamePlayerId.equals(playerId)) {
            throw new SecurityException("다른 사용자의 게임입니다.");
        }

        // 3. SOLVED 상태 검증
        if (game.getStatus() != GameStatus.SOLVED) {
            throw new IllegalStateException("아직 완료되지 않은 게임입니다.");
        }

        // 4. PlayerRepository.findByIdForUpdate() 잠금을 획득한 Player 조회
        Player player = playerRepository.findByIdForUpdate(playerId).orElseThrow();

        // 5. RewardHistory 중복 여부 확인
        if (rewardHistoryRepository.existsByGame(game)) {
            throw new IllegalStateException("이미 보상을 수령한 게임입니다.");
        }

        // 6. Player의 gold 증가 (일단은 항상 100 골드)
        player.addGold(REWARD_AMOUNT);

        // 7. RewardHistory 생성 및 저장
        RewardHistory rewardHistory = new RewardHistory(game, REWARD_AMOUNT);
        rewardHistoryRepository.save(rewardHistory);

        // 8. 지급한 보상 금액 반환
        return rewardHistory.getRewardAmount();
    }
}
