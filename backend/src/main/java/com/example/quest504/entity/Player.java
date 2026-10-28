package com.example.quest504.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ColumnDefault("0")
    @Column
    private long gold = 0;

    protected Player() {
        // JPA에서 사용
    }

    public Player(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public long  getGold() {
        return gold;
    }

    public void addGold(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("보상은 0보다 커야 합니다.");
        }
        this.gold += amount;
    }
}
