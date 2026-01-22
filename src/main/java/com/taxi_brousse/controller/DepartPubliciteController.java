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

import com.taxi_brousse.dto.DepartPubliciteDTO;
import com.taxi_brousse.service.DepartPubliciteService;
import com.taxi_brousse.service.DepartPubliciteService.DepartPubliciteStatsDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/depart-publicites")
@RequiredArgsConstructor
public class DepartPubliciteController {
    
    private final DepartPubliciteService departPubliciteService;
    
    /**
     * Récupère toutes les diffusions d'un départ
     */
    @GetMapping("/depart/{departId}")
    public ResponseEntity<List<DepartPubliciteDTO>> getByDepartId(@PathVariable Long departId) {
        List<DepartPubliciteDTO> diffusions = departPubliciteService.findByDepartId(departId);
        return ResponseEntity.ok(diffusions);
    }
    
    /**
     * Récupère toutes les diffusions d'une publicité
     */
    @GetMapping("/publicite/{publiciteId}")
    public ResponseEntity<List<DepartPubliciteDTO>> getByPubliciteId(@PathVariable Long publiciteId) {
        List<DepartPubliciteDTO> diffusions = departPubliciteService.findByPubliciteId(publiciteId);
        return ResponseEntity.ok(diffusions);
    }
    
    /**
     * Récupère une diffusion par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartPubliciteDTO> getById(@PathVariable Long id) {
        DepartPubliciteDTO diffusion = departPubliciteService.findById(id);
        return ResponseEntity.ok(diffusion);
    }
    
    /**
     * Crée une nouvelle diffusion
     */
    @PostMapping
    public ResponseEntity<DepartPubliciteDTO> create(@Valid @RequestBody DepartPubliciteDTO dto) {
        DepartPubliciteDTO created = departPubliciteService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Supprime une diffusion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departPubliciteService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Met à jour le statut d'une diffusion
     */
    @PutMapping("/{id}/statut/{statut}")
    public ResponseEntity<DepartPubliciteDTO> updateStatut(
            @PathVariable Long id, 
            @PathVariable String statut) {
        DepartPubliciteDTO updated = departPubliciteService.updateStatut(id, statut);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Récupère les statistiques d'un départ
     */
    @GetMapping("/depart/{departId}/stats")
    public ResponseEntity<DepartPubliciteStatsDTO> getStatsByDepart(@PathVariable Long departId) {
        DepartPubliciteStatsDTO stats = departPubliciteService.getStatsByDepart(departId);
        return ResponseEntity.ok(stats);
    }
}
