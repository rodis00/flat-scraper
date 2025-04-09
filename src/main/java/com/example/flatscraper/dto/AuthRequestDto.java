package com.example.flatscraper.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDto(
        @NotBlank(message = "Username can not be blank.")
        String username,

        @NotBlank(message = "Password can not be blank.")
        String password
) {
}
