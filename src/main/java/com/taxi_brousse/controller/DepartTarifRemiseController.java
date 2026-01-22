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

import com.taxi_brousse.dto.DepartTarifRemiseDTO;
import com.taxi_brousse.service.DepartTarifRemiseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tarifs-remises")
@RequiredArgsConstructor
public class DepartTarifRemiseController {

    private final DepartTarifRemiseService departTarifRemiseService;

    @GetMapping
    public ResponseEntity<List<DepartTarifRemiseDTO>> getAll() {
        return ResponseEntity.ok(departTarifRemiseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartTarifRemiseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departTarifRemiseService.getById(id));
    }

    @GetMapping("/depart/{departId}")
    public ResponseEntity<List<DepartTarifRemiseDTO>> getByDepart(@PathVariable Long departId) {
        return ResponseEntity.ok(departTarifRemiseService.getRemisesByDepart(departId));
    }

    @PostMapping
    public ResponseEntity<DepartTarifRemiseDTO> create(@Valid @RequestBody DepartTarifRemiseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departTarifRemiseService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartTarifRemiseDTO> update(@PathVariable Long id, @Valid @RequestBody DepartTarifRemiseDTO dto) {
        return ResponseEntity.ok(departTarifRemiseService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departTarifRemiseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
