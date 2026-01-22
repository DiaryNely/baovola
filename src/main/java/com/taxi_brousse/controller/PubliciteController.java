package com.taxi_brousse.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taxi_brousse.dto.PubliciteDTO;
import com.taxi_brousse.repository.PubliciteRepository;
import com.taxi_brousse.service.PubliciteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/publicites")
@RequiredArgsConstructor
public class PubliciteController {
    private final PubliciteRepository publiciteRepository;
    private final PubliciteService publiciteService;
    
    /**
     * Récupère toutes les publicités actives
     */
    @GetMapping("/actives")
    public ResponseEntity<List<PubliciteDTO>> getPublicitesActives() {
        return ResponseEntity.ok(publiciteService.findAll().stream()
            .filter(p -> Boolean.TRUE.equals(p.getActif()))
            .toList());
    }
    
    /**
     * Récupère les publicités valides à une date donnée
     */
    @GetMapping("/valides")
    public ResponseEntity<List<PubliciteDTO>> getPublicitesValides(
            @RequestParam(required = false) String date) {
        LocalDate dateRecherche = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(publiciteRepository.findValidesADate(dateRecherche).stream()
            .map(p -> publiciteService.findById(p.getId()))
            .toList());
    }
    
    /**
     * Récupère une publicité par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PubliciteDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(publiciteService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<PubliciteDTO>> getAll() {
        return ResponseEntity.ok(publiciteService.findAll());
    }

    @GetMapping("/societe/{societeId}")
    public ResponseEntity<List<PubliciteDTO>> getBySociete(@PathVariable Long societeId) {
        return ResponseEntity.ok(publiciteService.findBySocieteId(societeId));
    }

    @PostMapping
    public ResponseEntity<PubliciteDTO> create(@Valid @RequestBody PubliciteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publiciteService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PubliciteDTO> update(@PathVariable Long id, @Valid @RequestBody PubliciteDTO dto) {
        return ResponseEntity.ok(publiciteService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        publiciteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
