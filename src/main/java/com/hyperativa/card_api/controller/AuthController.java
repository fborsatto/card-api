package com.hyperativa.card_api.controller;

import com.hyperativa.card_api.dto.LoginDTO;
import com.hyperativa.card_api.infrastructure.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.login(), request.password())
        );

        var user = userDetailsService.loadUserByUsername(request.login());
        var token = tokenService.generateToken(user);
        
        return ResponseEntity.ok(Map.of("token", token));
    }
}