package com.taxi_brousse.controller;

import java.util.List;
import java.util.Map;

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

import com.taxi_brousse.dto.CreerReservationRequest;
import com.taxi_brousse.dto.PaiementRequest;
import com.taxi_brousse.dto.ReservationDTO;
import com.taxi_brousse.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.findAll();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        ReservationDTO reservation = reservationService.findById(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByClient(@PathVariable Long clientId) {
        List<ReservationDTO> reservations = reservationService.findByClientId(clientId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/depart/{departId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByDepart(@PathVariable Long departId) {
        List<ReservationDTO> reservations = reservationService.findByDepartId(departId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/depart/{departId}/places-disponibles")
    public ResponseEntity<List<Integer>> getPlacesDisponibles(@PathVariable Long departId) {
        List<Integer> places = reservationService.getPlacesDisponibles(departId);
        return ResponseEntity.ok(places);
    }

    @PostMapping
    public ResponseEntity<ReservationDTO> creerReservation(@Valid @RequestBody CreerReservationRequest request) {
        ReservationDTO reservation = reservationService.creerReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody CreerReservationRequest request) {
        ReservationDTO reservation = reservationService.updateReservation(id, request);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<ReservationDTO> updateStatut(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        Long nouveauStatutId = body.get("statutId");
        ReservationDTO reservation = reservationService.updateStatut(id, nouveauStatutId);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerReservation(@PathVariable Long id) {
        reservationService.annulerReservation(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/payer")
    public ResponseEntity<ReservationDTO> enregistrerPaiement(
            @PathVariable Long id,
            @Valid @RequestBody PaiementRequest paiementRequest) {
        ReservationDTO reservation = reservationService.enregistrerPaiement(id, paiementRequest);
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
