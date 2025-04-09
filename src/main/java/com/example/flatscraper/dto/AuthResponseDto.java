package com.example.flatscraper.dto;

import jakarta.servlet.http.Cookie;

public record AuthResponseDto(
        String accessToken,
        Cookie cookie
){
}
