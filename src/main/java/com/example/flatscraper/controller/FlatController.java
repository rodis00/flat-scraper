package com.example.flatscraper.controller;

import com.example.flatscraper.enums.FieldName;
import com.example.flatscraper.dto.FlatDto;
import com.example.flatscraper.dto.FlatRequestDto;
import com.example.flatscraper.service.FlatService;
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
    public ResponseEntity<Page<FlatDto>> flats(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false, name = "search") String address,
            @RequestParam(required = false) FieldName sort
    ) {
        return ResponseEntity.ok(flatService.flats(page, address, sort));
    }

    @GetMapping("/flat/{id}")
    public ResponseEntity<FlatDto> flat(@PathVariable int id) {
        return ResponseEntity.ok(flatService.findFlatById(id));
    }

    @PostMapping("/predict-price")
    public ResponseEntity<Double> predictFlatPrice(@RequestBody FlatRequestDto flat) {
        return ResponseEntity.ok(flatService.predictFlatPrice(flat));
    }
}
