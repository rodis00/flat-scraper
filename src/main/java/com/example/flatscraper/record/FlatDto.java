package com.example.flatscraper.record;

public record FlatDto(
        String title,
        String url,
        String imageUrl,
        String price,
        String pricePerMeter,
        String address,
        String area,
        String rooms,
        String heating,
        String floor,
        String rent,
        String stateOfFinishing,
        String market,
        String formOfOwnership,
        String availableFrom,
        String advertiserType,
        String additionalInfo,
        String yearOfConstruction,
        String elevator,
        String buildingType,
        String buildingMaterial,
        String equipment,
        String security,
        String media
) {
}
