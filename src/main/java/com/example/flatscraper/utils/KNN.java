package com.example.flatscraper.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KNN {

    private int k; // Liczba sąsiadów
    private List<double[]> xTrain; // Dane treningowe (cechy)
    private List<Double> yTrain; // Dane treningowe (wartości)

    public KNN(int k) {
        this.k = k;
    }

    // Funkcja do obliczania euklidesowej odległości między dwoma punktami
    private double euclideanDistance(double[] point1, double[] point2) {
        double sum = 0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow(point1[i] - point2[i], 2);
        }
        return Math.sqrt(sum);
    }

    // Funkcja do trenowania modelu KNN
    public void fit(List<double[]> xTrain, List<Double> yTrain) {
        this.xTrain = xTrain;
        this.yTrain = yTrain;
    }

    // Funkcja do przewidywania wartości dla nowego punktu
    public double predictPrice(double[] flatFeatures) {
        List<Double> distances = new ArrayList<>();
        for (double[] features : xTrain) {
            distances.add(euclideanDistance(flatFeatures, features));
        }
        // Sortowanie punktów po odległości (rosnąco)
        List<Integer> sortedIndices = new ArrayList<>();
        for (int i = 0; i < distances.size(); i++) {
            sortedIndices.add(i);
        }
        sortedIndices.sort(Comparator.comparing(distances::get));

        // Wybranie k najbliższych sąsiadów
        double sum = 0;
        for (int i = 0; i < k; i++) {
            int index = sortedIndices.get(i);
            sum += yTrain.get(index);
        }

        // Przewidywanie wartości
        return sum / k;
    }

    public static void main(String[] args) {
        // Przykładowe dane: powierzchnia (area), liczba pokoi (rooms), piętro (floor), rok budowy (yearOfConstruction)
        List<double[]> X_train = new ArrayList<>();
        X_train.add(new double[]{50, 2, 3, 1990});
        X_train.add(new double[]{45, 2, 5, 1985});
        X_train.add(new double[]{60, 3, 2, 2005});
        X_train.add(new double[]{55, 3, 6, 1990});

        // Ceny mieszkań
        List<Double> y_train = new ArrayList<>();
        y_train.add(300000.0);
        y_train.add(350000.0);
        y_train.add(250000.0);
        y_train.add(400000.0);
        y_train.add(450000.0);

        KNN knn = new KNN(3);
        knn.fit(X_train, y_train);

        // Prognoza ceny dla nowego mieszkania
        double[] newFlat = {75, 3, 6, 1990};  // Nowe mieszkanie: 75m², 3 pokoje, 6. piętro, 1990
        double predictedPrice = knn.predictPrice(newFlat);
        System.out.println("Prognozowana cena dla nowego mieszkania: " + predictedPrice);
    }
}
