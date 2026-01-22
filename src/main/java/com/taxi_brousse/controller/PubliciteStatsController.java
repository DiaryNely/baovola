package com.taxi_brousse.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taxi_brousse.dto.PubliciteCaStatsDTO;
import com.taxi_brousse.service.PubliciteStatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/statistiques-publicite")
@RequiredArgsConstructor
public class PubliciteStatsController {

    private final PubliciteStatsService publiciteStatsService;

    @GetMapping("/ca")
    public ResponseEntity<PubliciteCaStatsDTO> getCaParMois(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        if (mois == null || annee == null) {
            LocalDate now = LocalDate.now();
            mois = now.getMonthValue();
            annee = now.getYear();
        }

        PubliciteCaStatsDTO stats = publiciteStatsService.getCaParMois(mois, annee);
        return ResponseEntity.ok(stats);
    }
}
