package com.taxi_brousse.controller;

import com.taxi_brousse.dto.ChauffeurDTO;
import com.taxi_brousse.service.ChauffeurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chauffeurs")
@RequiredArgsConstructor
public class ChauffeurController {

    private final ChauffeurService chauffeurService;

    @GetMapping
    public ResponseEntity<List<ChauffeurDTO>> getAllChauffeurs() {
        List<ChauffeurDTO> chauffeurs = chauffeurService.findAll();
        return ResponseEntity.ok(chauffeurs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChauffeurDTO> getChauffeurById(@PathVariable Long id) {
        ChauffeurDTO chauffeur = chauffeurService.findById(id);
        return ResponseEntity.ok(chauffeur);
    }

    @PostMapping
    public ResponseEntity<ChauffeurDTO> createChauffeur(@Valid @RequestBody ChauffeurDTO chauffeurDTO) {
        ChauffeurDTO createdChauffeur = chauffeurService.create(chauffeurDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChauffeur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChauffeurDTO> updateChauffeur(@PathVariable Long id, @Valid @RequestBody ChauffeurDTO chauffeurDTO) {
        ChauffeurDTO updatedChauffeur = chauffeurService.update(id, chauffeurDTO);
        return ResponseEntity.ok(updatedChauffeur);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChauffeur(@PathVariable Long id) {
        chauffeurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
