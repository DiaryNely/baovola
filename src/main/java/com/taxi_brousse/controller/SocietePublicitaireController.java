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

import com.taxi_brousse.dto.SocietePublicitaireDTO;
import com.taxi_brousse.dto.SocietePublicitaireLiteDTO;
import com.taxi_brousse.service.SocietePublicitaireService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/societes-publicitaires")
@RequiredArgsConstructor
public class SocietePublicitaireController {

    private final SocietePublicitaireService societePublicitaireService;

    @GetMapping("/actives")
    public List<SocietePublicitaireLiteDTO> listActives() {
        return societePublicitaireService.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getActif()))
                .sorted((a, b) -> a.getNom().compareToIgnoreCase(b.getNom()))
                .map(s -> new SocietePublicitaireLiteDTO(s.getId(), s.getCode(), s.getNom()))
                .toList();
    }

    @GetMapping
    public ResponseEntity<List<SocietePublicitaireDTO>> getAll() {
        return ResponseEntity.ok(societePublicitaireService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SocietePublicitaireDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(societePublicitaireService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SocietePublicitaireDTO> create(@Valid @RequestBody SocietePublicitaireDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(societePublicitaireService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SocietePublicitaireDTO> update(@PathVariable Long id,
            @Valid @RequestBody SocietePublicitaireDTO dto) {
        return ResponseEntity.ok(societePublicitaireService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        societePublicitaireService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
