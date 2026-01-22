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

import com.taxi_brousse.dto.DepartTarifSiegeDTO;
import com.taxi_brousse.service.DepartTarifSiegeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tarifs-sieges")
@RequiredArgsConstructor
public class DepartTarifSiegeController {

    private final DepartTarifSiegeService departTarifSiegeService;

    @GetMapping
    public ResponseEntity<List<DepartTarifSiegeDTO>> getAll() {
        return ResponseEntity.ok(departTarifSiegeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartTarifSiegeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departTarifSiegeService.getById(id));
    }

    @GetMapping("/depart/{departId}")
    public ResponseEntity<List<DepartTarifSiegeDTO>> getByDepart(@PathVariable Long departId) {
        return ResponseEntity.ok(departTarifSiegeService.getTarifsByDepart(departId));
    }

    @PostMapping
    public ResponseEntity<DepartTarifSiegeDTO> create(@Valid @RequestBody DepartTarifSiegeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departTarifSiegeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartTarifSiegeDTO> update(@PathVariable Long id, @Valid @RequestBody DepartTarifSiegeDTO dto) {
        return ResponseEntity.ok(departTarifSiegeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departTarifSiegeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
