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
                    (flat.getArea() != null) ? parseArea(flat.getArea()) : averageArea, // Imputacja brakujących danych
                    (flat.getRooms() != null) ? parseRooms(flat.getRooms()) : averageRooms,
                    (flat.getFloor() != null) ? parseFloor(flat.getFloor()) : averageFloor,
                    (flat.getYearOfConstruction() != null) ? parseYearOfConstruction(flat.getYearOfConstruction()) : averageYearOfConstruction
            });
            yTrain.add(flat.getPrice() != null ? parsePrice(flat.getPrice()) : averagePrice);
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
                    .mapToDouble(flat -> parseArea(flat.getArea()))
                    .average()
                    .orElse(0);
            case ROOMS -> flats.stream()
                    .filter(flat -> flat.getRooms() != null)
                    .mapToDouble(flat -> parseRooms(flat.getRooms()))
                    .average()
                    .orElse(0);
            case FLOOR -> flats.stream()
                    .filter(flat -> flat.getFloor() != null)
                    .mapToDouble(flat -> parseFloor(flat.getFloor()))
                    .average()
                    .orElse(0);
            case YEAR_OF_CONSTRUCTION -> flats.stream()
                    .filter(flat -> flat.getYearOfConstruction() != null)
                    .mapToInt(flat -> parseYearOfConstruction(flat.getYearOfConstruction()))
                    .average()
                    .orElse(0);
            case PRICE -> flats.stream()
                    .filter(flat -> flat.getPrice() != null)
                    .mapToDouble(flat -> parsePrice(flat.getPrice()))
                    .average()
                    .orElse(0);
        };
    }

    private double parseArea(String area) {
        try {
            area = area.substring(0, area.length() - 2);
            return Double.parseDouble(area);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseRooms(String rooms) {
        try {
            rooms = rooms.substring(0, 1);
            return Double.parseDouble(rooms);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseFloor(String floor) {
        floor = floor.substring(0, 1);
        try {
            return Double.parseDouble(floor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int parseYearOfConstruction(String yearOfConstruction) {
        try {
            return Integer.parseInt(yearOfConstruction);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parsePrice(String price) {
        try {
            price = price.replaceAll(" ", "");
            price = price.substring(0, price.length() - 2);
            return Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
