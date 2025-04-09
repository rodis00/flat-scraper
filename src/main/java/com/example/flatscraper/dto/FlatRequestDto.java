package com.example.flatscraper.dto;

public record FlatRequestDto(
        double area,
        int rooms,
        int floor,
        int yearOfConstruction
) {
}
