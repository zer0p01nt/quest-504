package com.example.quest504.security;

import com.example.quest504.entity.Player;
import com.example.quest504.repository.PlayerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final PlayerRepository playerRepository;

    public CustomUserDetailsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. playerRepository.findByUsername(username)으로 조회
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다.")); // 2. 존재하지 않으면 UsernameNotFoundException 발생

        // 3. 조회한 Player를 Spring Security의 UserDetails로 변환
        return User.withUsername(player.getUsername())
                .password(player.getPasswordHash())
                .roles("USER")
                .build();
    }
}
