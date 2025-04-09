package com.example.flatscraper.record;

import jakarta.servlet.http.Cookie;

public record AuthResponseDto(
        String accessToken,
        Cookie cookie
){
}
