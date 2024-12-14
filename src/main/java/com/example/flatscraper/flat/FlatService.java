package com.example.flatscraper.flat;

import org.springframework.stereotype.Service;

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
}
