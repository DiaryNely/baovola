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

import com.taxi_brousse.dto.TarifDTO;
import com.taxi_brousse.service.TarifService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tarifs")
@RequiredArgsConstructor
public class TarifController {

    private final TarifService tarifService;

    @GetMapping
    public ResponseEntity<List<TarifDTO>> getAllTarifs() {
        List<TarifDTO> tarifs = tarifService.getAllTarifs();
        return ResponseEntity.ok(tarifs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifDTO> getTarifById(@PathVariable Long id) {
        TarifDTO tarif = tarifService.getTarifById(id);
        return ResponseEntity.ok(tarif);
    }

    @PostMapping
    public ResponseEntity<TarifDTO> createTarif(@RequestBody TarifDTO tarifDTO) {
        try {
            TarifDTO created = tarifService.createTarif(tarifDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarifDTO> updateTarif(@PathVariable Long id, @RequestBody TarifDTO tarifDTO) {
        try {
            TarifDTO updated = tarifService.updateTarif(id, tarifDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarif(@PathVariable Long id) {
        try {
            tarifService.deleteTarif(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
