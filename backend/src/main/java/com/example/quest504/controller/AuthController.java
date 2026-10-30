package com.example.quest504.controller;

import com.example.quest504.dto.MeResponse;
import com.example.quest504.entity.Player;
import com.example.quest504.service.CurrentPlayerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CurrentPlayerService currentPlayerService;

    public AuthController(CurrentPlayerService currentPlayerService) {
        this.currentPlayerService = currentPlayerService;
    }

    @GetMapping("/me")
    public MeResponse me(Principal principal) {
        String username = principal.getName();
        Player player = currentPlayerService.getPlayer(username);

        return new MeResponse(player.getUsername(), player.getGold());
    }
}
