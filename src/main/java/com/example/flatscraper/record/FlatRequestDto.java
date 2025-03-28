package com.example.flatscraper.record;

public record FlatRequestDto(
        double area,
        int rooms,
        int floor,
        int yearOfConstruction
) {
}
