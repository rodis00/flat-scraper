package com.example.flatscraper.service;

import com.example.flatscraper.entity.FlatEntity;
import com.example.flatscraper.enums.FieldName;
import com.example.flatscraper.exception.EntityNotFoundException;
import com.example.flatscraper.model.Flat;
import com.example.flatscraper.dto.FlatDto;
import com.example.flatscraper.dto.FlatRequestDto;
import com.example.flatscraper.repository.FlatRepository;
import com.example.flatscraper.utils.KNN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FlatService {

    private final FlatRepository flatRepository;

    public FlatService(FlatRepository flatRepository) {
        this.flatRepository = flatRepository;
    }

    public void saveAll(List<Flat> flats) {
        List<FlatEntity> cleanedFlatEntities = new ArrayList<>();

        for (Flat flat : flats) {
            cleanedFlatEntities.add(cleanFlatData(flat));
        }

        flatRepository.saveAll(cleanedFlatEntities);
    }

    public boolean checkIfFlatExists(String url) {
        return flatRepository.existsByUrl(url);
    }

    public Page<FlatDto> flats(
            int page,
            String address,
            FieldName sortBy
    ) {
        Pageable pageable = PageRequest.of(page, 15, getSort(sortBy));

        if (address != null) {
            return flatRepository.findAllByAddressContainsIgnoreCase(address, pageable)
                    .map(flatEntity -> {
                        double predictedPrice = predictFlatPrice(new FlatRequestDto(
                                flatEntity.getArea(),
                                flatEntity.getRooms(),
                                flatEntity.getFloor(),
                                flatEntity.getYearOfConstruction())
                        );
                        double referralPercent = calculateReferralPercent(flatEntity.getPrice(), predictedPrice);

                        return FlatDto.toFlatDto(flatEntity, predictedPrice, referralPercent);
                    });
        }
        return flatRepository.findAll(pageable)
                .map(flatEntity -> {
                    double predictedPrice = predictFlatPrice(new FlatRequestDto(
                            flatEntity.getArea(),
                            flatEntity.getRooms(),
                            flatEntity.getFloor(),
                            flatEntity.getYearOfConstruction())
                    );
                    double referralPercent = calculateReferralPercent(flatEntity.getPrice(), predictedPrice);

                    return FlatDto.toFlatDto(flatEntity, predictedPrice, referralPercent);
                });
    }

    private double calculateReferralPercent(double originalPrice, double predictedPrice) {
        double result = ((predictedPrice - originalPrice) / predictedPrice) * 100;
        return Math.round(result * 100.0) / 100.0;
    }

    private Sort getSort(FieldName sortBy) {
        return switch (sortBy) {
            case PRICE -> Sort.by(Sort.Direction.ASC, "price");
            case PRICE_PER_METER -> Sort.by(Sort.Direction.ASC, "pricePerMeter");
            case AREA -> Sort.by(Sort.Direction.ASC, "area");
            case ROOMS -> Sort.by(Sort.Direction.ASC, "rooms");
            case FLOOR -> Sort.by(Sort.Direction.ASC, "floor");
            case YEAR_OF_CONSTRUCTION -> Sort.by(Sort.Direction.ASC, "yearOfConstruction");
            case null, default -> Sort.unsorted();
        };
    }

    public double predictFlatPrice(FlatRequestDto flatRequestDto) {
        KNN knn = new KNN(3);
        List<double[]> xTrain = new ArrayList<>();
        List<Double> yTrain = new ArrayList<>();

        List<FlatEntity> flatEntities = flatRepository.findAll();

        // Obliczanie średnich dla imputacji brakujących danych
        double averageArea = calculateAverage(FieldName.AREA, flatEntities);
        double averageRooms = calculateAverage(FieldName.ROOMS, flatEntities);
        double averageFloor = calculateAverage(FieldName.FLOOR, flatEntities);
        double averageYearOfConstruction = calculateAverage(FieldName.YEAR_OF_CONSTRUCTION, flatEntities);
        double averagePrice = calculateAverage(FieldName.PRICE, flatEntities);
        double averagePricePerMeter = calculateAverage(FieldName.PRICE_PER_METER, flatEntities);

        for (FlatEntity flatEntity : flatEntities) {
            xTrain.add(new double[]{
                    (flatEntity.getArea() != null) ? flatEntity.getArea() : averageArea, // Imputacja brakujących danych
                    (flatEntity.getRooms() != null) ? flatEntity.getRooms() : averageRooms,
                    (flatEntity.getFloor() != null) ? flatEntity.getFloor() : averageFloor,
                    (flatEntity.getYearOfConstruction() != null) ? flatEntity.getYearOfConstruction() : averageYearOfConstruction,
                    (flatEntity.getPricePerMeter() != null) ? flatEntity.getPricePerMeter() : averagePricePerMeter
            });
            yTrain.add(flatEntity.getPrice() != null ? flatEntity.getPrice() : averagePrice);
        }

        knn.fit(xTrain, yTrain);

        // Przewidywanie ceny dla nowego mieszkania
        double[] newApartment = {
                flatRequestDto.area(),
                flatRequestDto.rooms(),
                flatRequestDto.floor(),
                flatRequestDto.yearOfConstruction(),
        };

        return knn.predictPrice(newApartment);
    }

    private double calculateAverage(
            FieldName fieldName,
            List<FlatEntity> flatEntities
    ) {
        return switch (fieldName) {
            case AREA -> flatEntities.stream()
                    .filter(flatEntity -> flatEntity.getArea() != null)
                    .mapToDouble(flatEntity -> flatEntity.getArea())
                    .average()
                    .orElse(0);
            case ROOMS -> flatEntities.stream()
                    .filter(flatEntity -> flatEntity.getRooms() != null)
                    .mapToDouble(flatEntity -> flatEntity.getRooms())
                    .average()
                    .orElse(0);
            case FLOOR -> flatEntities.stream()
                    .filter(flatEntity -> flatEntity.getFloor() != null)
                    .mapToDouble(flatEntity -> flatEntity.getFloor())
                    .average()
                    .orElse(0);
            case YEAR_OF_CONSTRUCTION -> flatEntities.stream()
                    .filter(flatEntity -> flatEntity.getYearOfConstruction() != null)
                    .mapToInt(flatEntity -> flatEntity.getYearOfConstruction())
                    .average()
                    .orElse(0);
            case PRICE -> flatEntities.stream()
                    .filter(flatEntity -> flatEntity.getPrice() != null)
                    .mapToDouble(flatEntity -> flatEntity.getPrice())
                    .average()
                    .orElse(0);
            case PRICE_PER_METER -> flatEntities.stream()
                    .filter(flatEntity -> flatEntity.getPricePerMeter() != null)
                    .mapToDouble(flatEntity -> flatEntity.getPricePerMeter())
                    .average()
                    .orElse(0);
        };
    }

    private FlatEntity cleanFlatData(Flat flat) {
        FlatEntity flatEntity = new FlatEntity();
        flatEntity.setTitle(flat.getTitle());
        flatEntity.setUrl(flat.getUrl());
        flatEntity.setImageUrl(flat.getImageUrl());
        flatEntity.setPrice(parseToDouble(flat.getPrice()));
        flatEntity.setPricePerMeter(parseToDouble(flat.getPricePerMeter()));
        flatEntity.setAddress(flat.getAddress());
        flatEntity.setArea(parseToDouble(flat.getArea()));
        flatEntity.setRooms(parseToInt(flat.getRooms()));
        flatEntity.setHeating(flat.getHeating());
        flatEntity.setFloor(parseFloorToInt(flat.getFloor()));
        flatEntity.setRent(parseToDouble(flat.getRent()));
        flatEntity.setStateOfFinishing(flat.getStateOfFinishing());
        flatEntity.setMarket(flat.getMarket());
        flatEntity.setFormOfOwnership(flat.getFormOfOwnership());
        flatEntity.setAvailableFrom(flat.getAvailableFrom());
        flatEntity.setAdvertiserType(flat.getAdvertiserType());
        flatEntity.setAdditionalInfo(flat.getAdditionalInfo());
        flatEntity.setYearOfConstruction(parseToInt(flat.getYearOfConstruction()));
        flatEntity.setElevator(flat.getElevator());
        flatEntity.setBuildingType(flat.getBuildingType());
        flatEntity.setBuildingMaterial(flat.getBuildingMaterial());
        flatEntity.setWindows(flat.getWindows());
        flatEntity.setEnergyCertificate(flat.getEnergyCertificate());
        flatEntity.setSafety(flat.getSafety());
        flatEntity.setSecurity(flat.getSecurity());
        flatEntity.setMedia(flat.getMedia());
        return flatEntity;
    }

    private double parseToDouble(String value) {
        return Optional.ofNullable(value)
                .map(v -> v.replaceAll("[^0-9.]", "")) // Usuwa znaki inne niż cyfry i kropka
                .filter(v -> !v.isEmpty())
                .map(Double::parseDouble)
                .orElse(0.0);
    }

    private int parseToInt(String value) {
        return Optional.ofNullable(value)
                .map(v -> v.replaceAll("[^0-9]", ""))
                .filter(v -> !v.isEmpty())
                .map(Integer::parseInt)
                .orElse(0);
    }

    private int parseFloorToInt(String value) {
        if (value == null) return 0;
        try {
            value = value.split("/")[0];
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public FlatDto findFlatById(int id) {
        FlatEntity flatEntity = flatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flat not found"));

        double predictedPrice = predictFlatPrice(new FlatRequestDto(
                flatEntity.getArea(),
                flatEntity.getRooms(),
                flatEntity.getFloor(),
                flatEntity.getYearOfConstruction())
        );

        double referralPercent = calculateReferralPercent(flatEntity.getPrice(), predictedPrice);

        return FlatDto.toFlatDto(flatEntity, predictedPrice, referralPercent);
    }
}
