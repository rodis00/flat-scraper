package com.example.flatscraper.controller;

import com.example.flatscraper.dto.AuthRequestDto;
import com.example.flatscraper.dto.AuthResponseDto;
import com.example.flatscraper.service.AuthService;
import com.example.flatscraper.dto.RegisterRequestDto;
import com.example.flatscraper.dto.RefreshTokenRequestDto;
import com.example.flatscraper.dto.RefreshTokenResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDto request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticate(@RequestBody @Valid AuthRequestDto request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
