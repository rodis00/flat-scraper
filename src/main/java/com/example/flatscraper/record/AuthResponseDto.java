package com.example.flatscraper.record;

public record AuthResponseDto(
        String accessToken,
        String refreshToken
){
}
