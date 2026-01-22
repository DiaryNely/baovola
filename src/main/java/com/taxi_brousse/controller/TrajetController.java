package com.taxi_brousse.controller;

import com.taxi_brousse.dto.TrajetDTO;
import com.taxi_brousse.service.TrajetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trajets")
@RequiredArgsConstructor
public class TrajetController {

    private final TrajetService trajetService;

    @GetMapping
    public ResponseEntity<List<TrajetDTO>> getAllTrajets() {
        List<TrajetDTO> trajets = trajetService.findAll();
        return ResponseEntity.ok(trajets);
    }

    @GetMapping("/actifs")
    public ResponseEntity<List<TrajetDTO>> getTrajetsActifs() {
        List<TrajetDTO> trajets = trajetService.findActifs();
        return ResponseEntity.ok(trajets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrajetDTO> getTrajetById(@PathVariable Long id) {
        TrajetDTO trajet = trajetService.findById(id);
        return ResponseEntity.ok(trajet);
    }

    @PostMapping
    public ResponseEntity<TrajetDTO> createTrajet(@Valid @RequestBody TrajetDTO trajetDTO) {
        TrajetDTO createdTrajet = trajetService.create(trajetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTrajet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrajetDTO> updateTrajet(@PathVariable Long id, @Valid @RequestBody TrajetDTO trajetDTO) {
        TrajetDTO updatedTrajet = trajetService.update(id, trajetDTO);
        return ResponseEntity.ok(updatedTrajet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrajet(@PathVariable Long id) {
        trajetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
