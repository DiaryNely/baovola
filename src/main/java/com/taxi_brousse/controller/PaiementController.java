package com.taxi_brousse.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi_brousse.dto.PaiementDTO;
import com.taxi_brousse.service.PaiementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    @GetMapping
    public ResponseEntity<List<PaiementDTO>> getAll() {
        return ResponseEntity.ok(paiementService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaiementDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paiementService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PaiementDTO> create(@Valid @RequestBody PaiementDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paiementService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaiementDTO> update(@PathVariable Long id, @Valid @RequestBody PaiementDTO dto) {
        return ResponseEntity.ok(paiementService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paiementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
