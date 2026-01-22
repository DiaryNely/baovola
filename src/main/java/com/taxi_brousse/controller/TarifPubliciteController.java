package com.taxi_brousse.controller;

import com.taxi_brousse.dto.TarifPubliciteDTO;
import com.taxi_brousse.service.TarifPubliciteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tarifs-publicite")
@RequiredArgsConstructor
public class TarifPubliciteController {
    
    private final TarifPubliciteService tarifPubliciteService;
    
    /**
     * GET /api/tarifs-publicite
     * Liste tous les tarifs
     */
    @GetMapping
    public ResponseEntity<List<TarifPubliciteDTO>> findAll() {
        return ResponseEntity.ok(tarifPubliciteService.findAll());
    }
    
    /**
     * GET /api/tarifs-publicite/{id}
     * Récupère un tarif par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TarifPubliciteDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tarifPubliciteService.findById(id));
    }
    
    /**
     * GET /api/tarifs-publicite/actuel
     * Récupère le tarif actuel (sans date de fin)
     */
    @GetMapping("/actuel")
    public ResponseEntity<TarifPubliciteDTO> findTarifActuel() {
        return ResponseEntity.ok(tarifPubliciteService.findTarifActuel());
    }
    
    /**
     * GET /api/tarifs-publicite/en-vigueur
     * Récupère le tarif en vigueur à une date donnée (par défaut aujourd'hui)
     */
    @GetMapping("/en-vigueur")
    public ResponseEntity<TarifPubliciteDTO> findTarifEnVigueur(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(tarifPubliciteService.findTarifEnVigueur(date));
    }
    
    /**
     * POST /api/tarifs-publicite
     * Crée un nouveau tarif
     */
    @PostMapping
    public ResponseEntity<TarifPubliciteDTO> create(@Valid @RequestBody TarifPubliciteDTO dto) {
        TarifPubliciteDTO created = tarifPubliciteService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * PUT /api/tarifs-publicite/{id}
     * Met à jour un tarif
     */
    @PutMapping("/{id}")
    public ResponseEntity<TarifPubliciteDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TarifPubliciteDTO dto) {
        return ResponseEntity.ok(tarifPubliciteService.update(id, dto));
    }
    
    /**
     * DELETE /api/tarifs-publicite/{id}
     * Supprime un tarif
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tarifPubliciteService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * PUT /api/tarifs-publicite/{id}/desactiver
     * Désactive un tarif
     */
    @PutMapping("/{id}/desactiver")
    public ResponseEntity<TarifPubliciteDTO> desactiver(@PathVariable Long id) {
        return ResponseEntity.ok(tarifPubliciteService.desactiver(id));
    }
    
    /**
     * PUT /api/tarifs-publicite/actuel/cloturer
     * Clôture le tarif actuel en définissant une date de fin
     */
    @PutMapping("/actuel/cloturer")
    public ResponseEntity<TarifPubliciteDTO> cloturerTarifActuel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return ResponseEntity.ok(tarifPubliciteService.cloturerTarifActuel(dateFin));
    }
}
