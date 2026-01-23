package com.taxi_brousse.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taxi_brousse.dto.PaiementPubliciteDTO;
import com.taxi_brousse.dto.PaiementPubliciteResumeDTO;
import com.taxi_brousse.service.PaiementPubliciteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/paiements-publicite")
@RequiredArgsConstructor
public class PaiementPubliciteController {

    private final PaiementPubliciteService paiementPubliciteService;

    @GetMapping("/societe/{societeId}")
    public ResponseEntity<List<PaiementPubliciteDTO>> listBySociete(
            @PathVariable Long societeId,
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        if (mois != null && annee != null) {
            return ResponseEntity.ok(paiementPubliciteService.listBySocieteAndPeriode(societeId, mois, annee));
        }
        return ResponseEntity.ok(paiementPubliciteService.listBySociete(societeId));
    }

    @GetMapping("/societe/{societeId}/resume")
    public ResponseEntity<PaiementPubliciteResumeDTO> getResume(
            @PathVariable Long societeId,
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        if (mois != null && annee != null) {
            return ResponseEntity.ok(paiementPubliciteService.getResumeByPeriode(societeId, mois, annee));
        }
        return ResponseEntity.ok(paiementPubliciteService.getResume(societeId));
    }

    @GetMapping("/societe/{societeId}/diffusions")
    public ResponseEntity<List<com.taxi_brousse.dto.DepartPubliciteDTO>> listDiffusions(
            @PathVariable Long societeId,
            @RequestParam Integer mois,
            @RequestParam Integer annee) {
        return ResponseEntity.ok(paiementPubliciteService.listDiffusionsBySocieteAndPeriode(societeId, mois, annee));
    }

    @PostMapping
    public ResponseEntity<PaiementPubliciteDTO> create(@Valid @RequestBody PaiementPubliciteDTO dto) {
        PaiementPubliciteDTO created = paiementPubliciteService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
