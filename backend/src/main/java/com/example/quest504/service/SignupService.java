package com.example.quest504.service;

import com.example.quest504.dto.SignupRequest;
import com.example.quest504.dto.SignupResponse;
import com.example.quest504.entity.Player;
import com.example.quest504.repository.PlayerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public  SignupService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String username = request.username();
        String password = request.password();

        // 1. username 검증
        if (username == null || username.isBlank() || username.length() < 3 || username.length() > 20 || !username.equals(username.strip())) {
            throw new IllegalArgumentException("3자 이상 20자 이하의 아이디를 입력하세요.");
        }

        // 2. password 검증
        if (password == null || password.isBlank() || password.length() < 8 ) {
            throw new IllegalArgumentException("최소 8자 이상의 비밀번호를 입력하세요.");
        }

        // 3. 중복 아이디 검증
        if (playerRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("중복된 아이디입니다.");
        }

        // 4. 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(password);

        // 5. Player 생성
        Player player = new Player(username, encodedPassword);
        playerRepository.save(player);

        return new SignupResponse(player.getUsername());
    }
}
