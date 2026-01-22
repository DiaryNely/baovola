package com.taxi_brousse.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.taxi_brousse.dto.BilletDTO;
import com.taxi_brousse.service.BilletService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BilletController {

    private final BilletService billetService;

    // Page Web
    @GetMapping("/billets")
    public String showBillets(Model model) {
        return "taxi_brousse/billets";
    }

    // REST API
    @GetMapping("/api/billets")
    public ResponseEntity<List<BilletDTO>> getAllBillets() {
        List<BilletDTO> billets = billetService.findAll();
        return ResponseEntity.ok(billets);
    }

    @GetMapping("/api/billets/{id}")
    public ResponseEntity<BilletDTO> getBillet(@PathVariable Long id) {
        BilletDTO billet = billetService.findById(id);
        return ResponseEntity.ok(billet);
    }

    @GetMapping("/api/billets/reservation/{reservationId}")
    public ResponseEntity<List<BilletDTO>> getBilletsByReservation(@PathVariable Long reservationId) {
        List<BilletDTO> billets = billetService.findByReservationId(reservationId);
        return ResponseEntity.ok(billets);
    }

    @GetMapping("/api/billets/numero/{numero}")
    public ResponseEntity<BilletDTO> getBilletByNumero(@PathVariable String numero) {
        BilletDTO billet = billetService.findByNumero(numero);
        return ResponseEntity.ok(billet);
    }
}
