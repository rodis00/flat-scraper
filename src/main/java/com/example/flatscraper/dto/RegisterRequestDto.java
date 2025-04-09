package com.example.flatscraper.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "Email shouldn't be empty.")
        @Email(message = "Invalid email address.")
        String email,

        @NotBlank(message = "Username shouldn't be empty.")
        @Size(min = 3, max = 20, message = "Username should be between 3 or 20 characters.")
        String username,

        @NotBlank(message = "Password shouldn't be empty.")
        String password
) {
}
