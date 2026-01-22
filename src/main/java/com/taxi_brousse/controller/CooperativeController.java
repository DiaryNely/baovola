package com.taxi_brousse.controller;

import com.taxi_brousse.dto.CooperativeDTO;
import com.taxi_brousse.service.CooperativeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cooperatives")
@RequiredArgsConstructor
public class CooperativeController {

    private final CooperativeService cooperativeService;

    @GetMapping
    public ResponseEntity<List<CooperativeDTO>> getAllCooperatives() {
        List<CooperativeDTO> cooperatives = cooperativeService.findAll();
        return ResponseEntity.ok(cooperatives);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CooperativeDTO> getCooperativeById(@PathVariable Long id) {
        CooperativeDTO cooperative = cooperativeService.findById(id);
        return ResponseEntity.ok(cooperative);
    }

    @PostMapping
    public ResponseEntity<CooperativeDTO> createCooperative(@Valid @RequestBody CooperativeDTO cooperativeDTO) {
        CooperativeDTO createdCooperative = cooperativeService.create(cooperativeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCooperative);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CooperativeDTO> updateCooperative(@PathVariable Long id, @Valid @RequestBody CooperativeDTO cooperativeDTO) {
        CooperativeDTO updatedCooperative = cooperativeService.update(id, cooperativeDTO);
        return ResponseEntity.ok(updatedCooperative);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCooperative(@PathVariable Long id) {
        cooperativeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
