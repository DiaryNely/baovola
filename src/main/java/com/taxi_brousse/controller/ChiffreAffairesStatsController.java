package com.taxi_brousse.controller;

import com.taxi_brousse.dto.ChiffreAffairesStatsDTO;
import com.taxi_brousse.service.ChiffreAffairesStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/statistiques/chiffre-affaires")
public class ChiffreAffairesStatsController {

    @Autowired
    private ChiffreAffairesStatsService statsService;

    @GetMapping
    public String index(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee,
            Model model) {

        // Valeurs par défaut : mois et année actuels
        LocalDate now = LocalDate.now();
        if (mois == null) {
            mois = now.getMonthValue();
        }
        if (annee == null) {
            annee = now.getYear();
        }

        // Récupérer les statistiques
        ChiffreAffairesStatsDTO stats = statsService.getStatistiquesMois(mois, annee);

        model.addAttribute("stats", stats);
        model.addAttribute("selectedMois", mois);
        model.addAttribute("selectedAnnee", annee);

        return "taxi_brousse/statistiques/chiffre_affaires";
    }
}
