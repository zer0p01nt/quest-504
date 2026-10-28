package com.example.quest504.service;

import com.example.quest504.entity.Player;
import com.example.quest504.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CurrentPlayerService {
    private final PlayerRepository playerRepository;

    public CurrentPlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Long getPlayerId(String username) {
        // 1. username으로 Player 조회
        Player player = playerRepository.findByUsername(username).orElseThrow(() ->
                new NoSuchElementException("사용자를 찾을 수 없습니다.") // 2. 없으면 NoSuchElementException 발생
        );

        // 3. Player의 id 반환
        return player.getId();
    }
}
