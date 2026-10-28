package com.example.quest504.controller;

import com.example.quest504.dto.MeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class AuthController {

    @GetMapping("/api/me")
    public MeResponse me(Principal principal) {
        return new MeResponse(principal.getName());
    }
}
