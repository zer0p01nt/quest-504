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

    public Player getPlayer(String username) {
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    }

    public Long getPlayerId(String username) {
        return getPlayer(username).getId();
    }
}
