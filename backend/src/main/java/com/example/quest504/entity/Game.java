package com.example.quest504.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false, length = 3)
    private String secret;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.PLAYING;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime solvedAt;

    protected Game() {
        // JPA에서 사용
    }

    public Game(Player player, String secret) {
        this.player = player;
        this.secret = secret;
    }

    public Long getId() {
        return id;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void solve() {
        if (this.status == GameStatus.PLAYING) {
            this.status = GameStatus.SOLVED;
            this.solvedAt = LocalDateTime.now();
        }
    }
}
