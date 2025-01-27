package com.example.flatscraper.flat;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flats")
public class FlatController {

    private final FlatService flatService;

    public FlatController(FlatService flatService) {
        this.flatService = flatService;
    }

    @GetMapping
    public List<Flat> flats() {
        return flatService.flats();
    }

    @PostMapping("/predict-price")
    public double predictFlatPrice(@RequestBody FlatRequest flat) {
        return flatService.predictFlatPrice(flat);
    }
}
