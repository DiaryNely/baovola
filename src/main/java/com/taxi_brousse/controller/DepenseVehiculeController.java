package com.taxi_brousse.controller;

import com.taxi_brousse.dto.DepenseVehiculeDTO;
import com.taxi_brousse.service.DepenseVehiculeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/depenses-vehicule")
@RequiredArgsConstructor
public class DepenseVehiculeController {

    private final DepenseVehiculeService depenseVehiculeService;

    @GetMapping
    public ResponseEntity<List<DepenseVehiculeDTO>> getAllDepenses() {
        List<DepenseVehiculeDTO> depenses = depenseVehiculeService.findAll();
        return ResponseEntity.ok(depenses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepenseVehiculeDTO> getDepenseById(@PathVariable Long id) {
        DepenseVehiculeDTO depense = depenseVehiculeService.findById(id);
        return ResponseEntity.ok(depense);
    }

    @GetMapping("/vehicule/{vehiculeId}")
    public ResponseEntity<List<DepenseVehiculeDTO>> getDepensesByVehicule(@PathVariable Long vehiculeId) {
        List<DepenseVehiculeDTO> depenses = depenseVehiculeService.findByVehiculeId(vehiculeId);
        return ResponseEntity.ok(depenses);
    }

    @GetMapping("/cooperative/{cooperativeId}")
    public ResponseEntity<List<DepenseVehiculeDTO>> getDepensesByCooperative(@PathVariable Long cooperativeId) {
        List<DepenseVehiculeDTO> depenses = depenseVehiculeService.findByCooperativeId(cooperativeId);
        return ResponseEntity.ok(depenses);
    }

    @GetMapping("/periode")
    public ResponseEntity<List<DepenseVehiculeDTO>> getDepensesByPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        List<DepenseVehiculeDTO> depenses = depenseVehiculeService.findByDateRange(dateDebut, dateFin);
        return ResponseEntity.ok(depenses);
    }

    @GetMapping("/vehicule/{vehiculeId}/periode")
    public ResponseEntity<List<DepenseVehiculeDTO>> getDepensesByVehiculeAndPeriode(
            @PathVariable Long vehiculeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        List<DepenseVehiculeDTO> depenses = depenseVehiculeService.findByVehiculeIdAndDateRange(vehiculeId, dateDebut, dateFin);
        return ResponseEntity.ok(depenses);
    }

    @PostMapping
    public ResponseEntity<DepenseVehiculeDTO> createDepense(@Valid @RequestBody DepenseVehiculeDTO depenseDTO) {
        DepenseVehiculeDTO createdDepense = depenseVehiculeService.create(depenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepenseVehiculeDTO> updateDepense(@PathVariable Long id, @Valid @RequestBody DepenseVehiculeDTO depenseDTO) {
        DepenseVehiculeDTO updatedDepense = depenseVehiculeService.update(id, depenseDTO);
        return ResponseEntity.ok(updatedDepense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepense(@PathVariable Long id) {
        depenseVehiculeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
