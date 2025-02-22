package com.example.flatscraper.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken
){
}
