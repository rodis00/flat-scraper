package com.example.flatscraper.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "Username can not be blank.")
        String username,

        @NotBlank(message = "Password can not be blank.")
        String password
) {
}
