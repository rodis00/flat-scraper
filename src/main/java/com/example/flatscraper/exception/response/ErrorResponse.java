package com.example.flatscraper.exception.response;

public record ErrorResponse(
        String error,
        String message
) {
}
