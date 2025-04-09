package com.example.flatscraper.record;

import com.example.flatscraper.entity.FlatEntity;

public record FlatDto(
        Integer id,
        String title,
        String url,
        String imageUrl,
        Double price,
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
        String equipment,
        String security,
        String media
) {
    public static FlatDto toFlatDto(FlatEntity flat) {
        return new FlatDto(
                flat.getId(),
                flat.getTitle(),
                flat.getUrl(),
                flat.getImageUrl(),
                flat.getPrice(),
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
                flat.getEquipment(),
                flat.getSecurity(),
                flat.getMedia()
        );
    }
}
