package com.taxi_brousse.controller;

import com.taxi_brousse.dto.VehiculeDTO;
import com.taxi_brousse.dto.VehiculeSiegeConfigDTO;
import com.taxi_brousse.service.VehiculeService;
import com.taxi_brousse.service.VehiculeSiegeConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final VehiculeService vehiculeService;
    private final VehiculeSiegeConfigService vehiculeSiegeConfigService;

    @GetMapping
    public ResponseEntity<List<VehiculeDTO>> getAllVehicules() {
        List<VehiculeDTO> vehicules = vehiculeService.findAll();
        return ResponseEntity.ok(vehicules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeDTO> getVehiculeById(@PathVariable Long id) {
        VehiculeDTO vehicule = vehiculeService.findById(id);
        return ResponseEntity.ok(vehicule);
    }

    @PostMapping
    public ResponseEntity<VehiculeDTO> createVehicule(@Valid @RequestBody VehiculeDTO vehiculeDTO) {
        VehiculeDTO createdVehicule = vehiculeService.create(vehiculeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeDTO> updateVehicule(@PathVariable Long id, @Valid @RequestBody VehiculeDTO vehiculeDTO) {
        VehiculeDTO updatedVehicule = vehiculeService.update(id, vehiculeDTO);
        return ResponseEntity.ok(updatedVehicule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicule(@PathVariable Long id) {
        vehiculeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/sieges-config")
    public ResponseEntity<List<VehiculeSiegeConfigDTO>> getSiegeConfigs(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculeSiegeConfigService.getConfigsByVehicule(id));
    }

    @PutMapping("/{id}/sieges-config")
    public ResponseEntity<List<VehiculeSiegeConfigDTO>> saveSiegeConfigs(
            @PathVariable Long id,
            @Valid @RequestBody List<VehiculeSiegeConfigDTO> configs) {
        return ResponseEntity.ok(vehiculeSiegeConfigService.saveConfigs(id, configs));
    }
}
