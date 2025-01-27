package com.example.flatscraper.flat;

import com.example.flatscraper.utils.KNN;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<Flat> flats() {
        return flatRepository.findAll();
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
                    (flat.getArea() != null) ? flat.getArea() : averageArea, // Imputacja brakujących danych
                    (flat.getRooms() != null) ? flat.getRooms() : averageRooms,
                    (flat.getFloor() != null) ? flat.getFloor() : averageFloor,
                    (flat.getYearOfConstruction() != null) ? flat.getYearOfConstruction() : averageYearOfConstruction
            });
            yTrain.add(flat.getPrice() != null ? flat.getPrice() : averagePrice);
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
                    .mapToDouble(Flat::getArea)
                    .average()
                    .orElse(0);
            case ROOMS -> flats.stream()
                    .filter(flat -> flat.getRooms() != null)
                    .mapToDouble(Flat::getRooms)
                    .average()
                    .orElse(0);
            case FLOOR -> flats.stream()
                    .filter(flat -> flat.getFloor() != null)
                    .mapToDouble(Flat::getFloor)
                    .average()
                    .orElse(0);
            case YEAR_OF_CONSTRUCTION -> flats.stream()
                    .filter(flat -> flat.getYearOfConstruction() != null)
                    .mapToInt(Flat::getYearOfConstruction)
                    .average()
                    .orElse(0);
            case PRICE -> flats.stream()
                    .filter(flat -> flat.getPrice() != null)
                    .mapToDouble(Flat::getPrice)
                    .average()
                    .orElse(0);
        };
    }
}
