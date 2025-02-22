package com.example.flatscraper.flat;

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

    public void saveAll(List<FlatRecord> flats) {
        List<Flat> cleanedFlats = new ArrayList<>();

        for (FlatRecord flatRecord: flats) {
            cleanedFlats.add(cleanFlatData(flatRecord));
        }

        flatRepository.saveAll(cleanedFlats);
    }

    public boolean checkIfFlatExists(String url) {
        return flatRepository.existsByUrl(url);
    }

    public Page<Flat> flats(int page, FieldName filterBy) {
        Pageable pageable = PageRequest.of(page, 20, getSort(filterBy));
        return flatRepository.findAll(pageable);
    }

    private Sort getSort(FieldName filterBy) {
        return switch (filterBy) {
            case PRICE -> Sort.by(Sort.Direction.ASC, "price");
            case PRICE_PER_METER -> Sort.by(Sort.Direction.ASC, "pricePerMeter");
            case AREA -> Sort.by(Sort.Direction.ASC, "area");
            case ROOMS -> Sort.by(Sort.Direction.ASC, "rooms");
            case FLOOR -> Sort.by(Sort.Direction.ASC, "floor");
            case YEAR_OF_CONSTRUCTION -> Sort.by(Sort.Direction.ASC, "yearOfConstruction");
            case null, default -> Sort.unsorted();
        };
    }

    public List<Flat> findAll() {
        return flatRepository.findAll();
    }

    public double predictFlatPrice(FlatRequest flatRequest) {
        KNN knn = new KNN(3);
        List<double[]> xTrain = new ArrayList<>();
        List<Double> yTrain = new ArrayList<>();

        List<Flat> flats = flatRepository.findAll();

        // Obliczanie średnich dla imputacji brakujących danych
        double averageArea = calculateAverage(FieldName.AREA, flats);
        double averageRooms = calculateAverage(FieldName.ROOMS, flats);
        double averageFloor = calculateAverage(FieldName.FLOOR, flats);
        double averageYearOfConstruction = calculateAverage(FieldName.YEAR_OF_CONSTRUCTION, flats);
        double averagePrice = calculateAverage(FieldName.PRICE, flats);
        double averagePricePerMeter = calculateAverage(FieldName.PRICE_PER_METER, flats);

        for (Flat flat : flats) {
            xTrain.add(new double[]{
                    (flat.getArea() != null) ? flat.getArea() : averageArea, // Imputacja brakujących danych
                    (flat.getRooms() != null) ? flat.getRooms() : averageRooms,
                    (flat.getFloor() != null) ? flat.getFloor() : averageFloor,
                    (flat.getYearOfConstruction() != null) ? flat.getYearOfConstruction() : averageYearOfConstruction,
                    (flat.getPricePerMeter() != null) ? flat.getPricePerMeter() : averagePricePerMeter
            });
            yTrain.add(flat.getPrice() != null ? flat.getPrice() : averagePrice);
        }

        knn.fit(xTrain, yTrain);

        // Przewidywanie ceny dla nowego mieszkania
        double[] newApartment = {
                flatRequest.area(),
                flatRequest.rooms(),
                flatRequest.floor(),
                flatRequest.yearOfConstruction(),
        };

        return knn.predictPrice(newApartment);
    }

    private double calculateAverage(
            FieldName fieldName,
            List<Flat> flats
    ) {
        return switch (fieldName) {
            case AREA -> flats.stream()
                    .filter(flat -> flat.getArea() != null)
                    .mapToDouble(flat -> flat.getArea())
                    .average()
                    .orElse(0);
            case ROOMS -> flats.stream()
                    .filter(flat -> flat.getRooms() != null)
                    .mapToDouble(flat -> flat.getRooms())
                    .average()
                    .orElse(0);
            case FLOOR -> flats.stream()
                    .filter(flat -> flat.getFloor() != null)
                    .mapToDouble(flat -> flat.getFloor())
                    .average()
                    .orElse(0);
            case YEAR_OF_CONSTRUCTION -> flats.stream()
                    .filter(flat -> flat.getYearOfConstruction() != null)
                    .mapToInt(flat -> flat.getYearOfConstruction())
                    .average()
                    .orElse(0);
            case PRICE -> flats.stream()
                    .filter(flat -> flat.getPrice() != null)
                    .mapToDouble(flat -> flat.getPrice())
                    .average()
                    .orElse(0);
            case PRICE_PER_METER -> flats.stream()
                    .filter(flat -> flat.getPricePerMeter() != null)
                    .mapToDouble(flat -> flat.getPricePerMeter())
                    .average()
                    .orElse(0);
        };
    }

    private Flat cleanFlatData(FlatRecord record) {
        Flat flat = new Flat();
        flat.setUrl(record.url());
        flat.setImageUrl(record.imageUrl());
        flat.setPrice(parseToDouble(record.price()));
        flat.setPricePerMeter(parseToDouble(record.pricePerMeter()));
        flat.setAddress(record.address());
        flat.setArea(parseToDouble(record.area()));
        flat.setRooms(parseToInt(record.rooms()));
        flat.setHeating(record.heating());
        flat.setFloor(parseFloorToInt(record.floor()));
        flat.setRent(parseToDouble(record.rent()));
        flat.setStateOfFinishing(record.stateOfFinishing());
        flat.setMarket(record.market());
        flat.setFormOfOwnership(record.formOfOwnership());
        flat.setAvailableFrom(record.availableFrom());
        flat.setAdvertiserType(record.advertiserType());
        flat.setAdditionalInfo(record.additionalInfo());
        flat.setYearOfConstruction(parseToInt(record.yearOfConstruction()));
        flat.setElevator(record.elevator());
        flat.setBuildingType(record.buildingType());
        flat.setSafety(record.safety());
        flat.setBuildingMaterial(record.buildingMaterial());
        flat.setWindows(record.windows());
        flat.setEquipment(record.equipment());
        flat.setSecurity(record.security());
        flat.setMedia(record.media());
        return flat;
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
        try {
            value = value.split("/")[0];
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
