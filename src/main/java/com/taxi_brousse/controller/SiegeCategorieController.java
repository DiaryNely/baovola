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

import com.taxi_brousse.dto.SiegeCategorieDTO;
import com.taxi_brousse.service.SiegeCategorieService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/siege-categories")
@RequiredArgsConstructor
public class SiegeCategorieController {

    private final SiegeCategorieService siegeCategorieService;

    @GetMapping
    public ResponseEntity<List<SiegeCategorieDTO>> getAll() {
        return ResponseEntity.ok(siegeCategorieService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiegeCategorieDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(siegeCategorieService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SiegeCategorieDTO> create(@RequestBody SiegeCategorieDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(siegeCategorieService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SiegeCategorieDTO> update(@PathVariable Long id, @RequestBody SiegeCategorieDTO dto) {
        return ResponseEntity.ok(siegeCategorieService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        siegeCategorieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
