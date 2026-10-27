package com.example.quest504.repository;

import com.example.quest504.entity.Game;
import com.example.quest504.entity.RewardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RewardHistoryRepository extends JpaRepository<RewardHistory, Long> {
    boolean existsByGame(Game game);

    Optional<RewardHistory> findByGame(Game game);

    long countByGame(Game game);
}
