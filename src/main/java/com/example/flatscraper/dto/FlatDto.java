package com.example.flatscraper.dto;

import com.example.flatscraper.entity.FlatEntity;

public record FlatDto(
        Integer id,
        String title,
        String url,
        String imageUrl,
        Double price,
        Double recommendedPrice,
        Double referralPercent,
        Double pricePerMeter,
        String address,
        Double area,
        Integer rooms,
        String heating,
        Integer floor,
        Double rent,
        String stateOfFinishing,
        String market,
        String formOfOwnership,
        String availableFrom,
        String advertiserType,
        String additionalInfo,
        Integer yearOfConstruction,
        String elevator,
        String buildingType,
        String buildingMaterial,
        String security,
        String media,
        String windows,
        String energyCertificate,
        String safety
) {
    public static FlatDto toFlatDto(
            FlatEntity flat,
            Double recommendedPrice,
            Double referralPercent
    ) {
        return new FlatDto(
                flat.getId(),
                flat.getTitle(),
                flat.getUrl(),
                flat.getImageUrl(),
                flat.getPrice(),
                recommendedPrice,
                referralPercent,
                flat.getPricePerMeter(),
                flat.getAddress(),
                flat.getArea(),
                flat.getRooms(),
                flat.getHeating(),
                flat.getFloor(),
                flat.getRent(),
                flat.getStateOfFinishing(),
                flat.getMarket(),
                flat.getFormOfOwnership(),
                flat.getAvailableFrom(),
                flat.getAdvertiserType(),
                flat.getAdditionalInfo(),
                flat.getYearOfConstruction(),
                flat.getElevator(),
                flat.getBuildingType(),
                flat.getBuildingMaterial(),
                flat.getSecurity(),
                flat.getMedia(),
                flat.getWindows(),
                flat.getEnergyCertificate(),
                flat.getSafety()
        );
    }
}
