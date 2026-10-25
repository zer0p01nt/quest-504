package com.example.quest504.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class RewardHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false, unique = true)
    private Game game;

    @Column(nullable = false)
    private long rewardAmount;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime awardedAt;

    protected RewardHistory() {
        // JPA에서 사용
    }

    public RewardHistory(Game game, long rewardAmount) {
        if (game == null) {
            throw new IllegalArgumentException("게임 정보가 필요합니다.");
        }

        if (game.getStatus() != GameStatus.SOLVED) {
            throw new IllegalStateException("게임이 완료되지 않았습니다.");
        }

        if (rewardAmount <= 0) {
            throw new IllegalArgumentException("보상은 0보다 커야 합니다.");
        }

        this.game = game;
        this.rewardAmount = rewardAmount;
    }

    public Long getId() {
        return id;
    }

    public long getRewardAmount() {
        return rewardAmount;
    }
}
