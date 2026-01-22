package com.taxi_brousse.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.taxi_brousse.dto.DepartDTO;
import com.taxi_brousse.dto.DepartTarifSiegeDTO;
import com.taxi_brousse.dto.DepartTarifRemiseDTO;
import com.taxi_brousse.dto.SeatInfoDTO;
import com.taxi_brousse.service.DepartService;
import com.taxi_brousse.service.DepartTarifRemiseService;
import com.taxi_brousse.service.DepartTarifSiegeService;
import com.taxi_brousse.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departs")
@RequiredArgsConstructor
public class DepartController {

    private final DepartService departService;
    private final DepartTarifSiegeService departTarifSiegeService;
    private final DepartTarifRemiseService departTarifRemiseService;
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<DepartDTO>> getAllDeparts() {
        List<DepartDTO> departs = departService.findAll();
        return ResponseEntity.ok(departs);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<DepartDTO>> getDepartsDisponibles() {
        List<DepartDTO> departs = departService.findDepartsDisponibles();
        return ResponseEntity.ok(departs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartDTO> getDepartById(@PathVariable Long id) {
        DepartDTO depart = departService.findById(id);
        return ResponseEntity.ok(depart);
    }

    @GetMapping("/trajet/{trajetId}")
    public ResponseEntity<List<DepartDTO>> getDepartsByTrajet(
            @PathVariable Long trajetId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateHeure) {
        
        LocalDateTime searchDate = dateHeure != null ? dateHeure : LocalDateTime.now();
        List<DepartDTO> departs = departService.findByTrajetIdAndDateHeureAfter(trajetId, searchDate);
        return ResponseEntity.ok(departs);
    }
    
    @GetMapping("/par-date")
    public ResponseEntity<List<DepartDTO>> getDepartsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String dateFin) {
        
        LocalDateTime debut = LocalDateTime.parse(dateDebut + "T00:00:00");
        LocalDateTime fin = LocalDateTime.parse(dateFin + "T23:59:59");
        List<DepartDTO> departs = departService.findByDateRange(debut, fin);
        return ResponseEntity.ok(departs);
    }
    
    @GetMapping("/par-jour")
    public ResponseEntity<List<DepartDTO>> getDepartsByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String date) {
        
        LocalDateTime debut = LocalDateTime.parse(date + "T00:00:00");
        LocalDateTime fin = LocalDateTime.parse(date + "T23:59:59");
        List<DepartDTO> departs = departService.findByDateRange(debut, fin);
        return ResponseEntity.ok(departs);
    }

    @PostMapping
    public ResponseEntity<DepartDTO> createDepart(@Valid @RequestBody DepartDTO departDTO) {
        DepartDTO createdDepart = departService.create(departDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepart);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartDTO> updateDepart(@PathVariable Long id, @Valid @RequestBody DepartDTO departDTO) {
        DepartDTO updatedDepart = departService.update(id, departDTO);
        return ResponseEntity.ok(updatedDepart);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepart(@PathVariable Long id) {
        departService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    // @GetMapping("/{id}/tarif")
    // public ResponseEntity<TarifDTO> getTarifByDepart(@PathVariable Long id) {
    //     Tarif tarif = departService.getTarifByDepartId(id);
        
    //     TarifDTO tarifDTO = new TarifDTO();
    //     tarifDTO.setId(tarif.getId());
    //     tarifDTO.setTrajetId(tarif.getTrajet().getId());
    //     tarifDTO.setTrajetLibelle(tarif.getTrajet().getLibelle());
    //     tarifDTO.setMontant(tarif.getMontant());
    //     tarifDTO.setDeviseCode(tarif.getRefDevise().getCode());
    //     tarifDTO.setDeviseSymbole(tarif.getRefDevise().getSymbole());
        
    //     return ResponseEntity.ok(tarifDTO);
    // }

    @GetMapping("/{id}/tarifs-sieges")
    public ResponseEntity<List<DepartTarifSiegeDTO>> getTarifsSieges(@PathVariable Long id) {
        return ResponseEntity.ok(departTarifSiegeService.getTarifsByDepart(id));
    }

    @GetMapping("/{id}/remises-passagers")
    public ResponseEntity<List<DepartTarifRemiseDTO>> getRemisesPassagers(@PathVariable Long id) {
        return ResponseEntity.ok(departTarifRemiseService.getRemisesByDepart(id));
    }

    @PutMapping("/{id}/tarifs-sieges")
    public ResponseEntity<List<DepartTarifSiegeDTO>> saveTarifsSieges(
            @PathVariable Long id,
            @Valid @RequestBody List<DepartTarifSiegeDTO> tarifs) {
        return ResponseEntity.ok(departTarifSiegeService.saveTarifs(id, tarifs));
    }

    @GetMapping("/{id}/seat-map")
    public ResponseEntity<List<SeatInfoDTO>> getSeatMap(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getSeatMap(id));
    }
}
