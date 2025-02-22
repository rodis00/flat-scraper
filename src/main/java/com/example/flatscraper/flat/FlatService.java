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

    public void saveAll(List<Flat> flats) {
        flatRepository.saveAll(flats);
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

        for (Flat flat : flats) {
            xTrain.add(new double[]{
                    (flat.getArea() != null) ? parseToDouble(flat.getArea()) : averageArea, // Imputacja brakujących danych
                    (flat.getRooms() != null) ? parseToDouble(flat.getRooms()) : averageRooms,
                    (flat.getFloor() != null) ? parseToDouble(flat.getFloor()) : averageFloor,
                    (flat.getYearOfConstruction() != null) ? parseToDouble(flat.getYearOfConstruction()) : averageYearOfConstruction
            });
            yTrain.add(flat.getPrice() != null ? parseToDouble(flat.getPrice()) : averagePrice);
        }

        knn.fit(xTrain, yTrain);

        // Przewidywanie ceny dla nowego mieszkania
        double[] newApartment = {
                flatRequest.area(),
                flatRequest.rooms(),
                flatRequest.floor(),
                flatRequest.yearOfConstruction()
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
                    .mapToDouble(flat -> parseToDouble(flat.getArea()))
                    .average()
                    .orElse(0);
            case ROOMS -> flats.stream()
                    .filter(flat -> flat.getRooms() != null)
                    .mapToDouble(flat -> parseToDouble(flat.getRooms()))
                    .average()
                    .orElse(0);
            case FLOOR -> flats.stream()
                    .filter(flat -> flat.getFloor() != null)
                    .mapToDouble(flat -> parseToDouble(flat.getFloor()))
                    .average()
                    .orElse(0);
            case YEAR_OF_CONSTRUCTION -> flats.stream()
                    .filter(flat -> flat.getYearOfConstruction() != null)
                    .mapToInt(flat -> parseToInt(flat.getYearOfConstruction()))
                    .average()
                    .orElse(0);
            case PRICE -> flats.stream()
                    .filter(flat -> flat.getPrice() != null)
                    .mapToDouble(flat -> parseToDouble(flat.getPrice()))
                    .average()
                    .orElse(0);
        };
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
                .map(v -> v.replaceAll("[^0-9]", "")) // Usuwa znaki inne niż cyfry
                .filter(v -> !v.isEmpty())
                .map(Integer::parseInt)
                .orElse(0);
    }
}
