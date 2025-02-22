package com.example.flatscraper.flat;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flats")
public class FlatController {

    private final FlatService flatService;

    public FlatController(FlatService flatService) {
        this.flatService = flatService;
    }

    @GetMapping
    public ResponseEntity<Page<Flat>> flats(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) FieldName filterBy
    ) {
        return ResponseEntity.ok(flatService.flats(page, filterBy));
    }

    @PostMapping("/predict-price")
    public ResponseEntity<Double> predictFlatPrice(@RequestBody FlatRequest flat) {
        return ResponseEntity.ok(flatService.predictFlatPrice(flat));
    }
}
