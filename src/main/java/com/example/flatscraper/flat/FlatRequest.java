package com.example.flatscraper.flat;

public record FlatRequest(
        double area,
        int rooms,
        int floor,
        int yearOfConstruction
) {
}
