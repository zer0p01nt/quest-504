package com.example.quest504.repository;

import com.example.quest504.entity.Player;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 조회한 행에 쓰기 잠금 요청
    @Query("SELECT p from Player p WHERE p.id = :id") // JPQL로 Player 조회 (:id = 쿼리에 전달할 파라미터)
    Optional<Player> findByIdForUpdate(@Param("id") Long id); // @Param : Java 매개변수와 JPQL 파라미터 연결
}
