package com.taxi_brousse.service;

import com.taxi_brousse.dto.ChiffreAffairesStatsDTO;
import com.taxi_brousse.dto.DepartDTO;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.StockDepartRepository;
import com.taxi_brousse.repository.VenteProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChiffreAffairesStatsService {

    @Autowired
    private DepartRepository departRepository;
    
    @Autowired
    private DepartService departService;

    @Autowired
    private VenteProduitRepository venteProduitRepository;
    
    @Autowired
    private StockDepartRepository stockDepartRepository;

    public ChiffreAffairesStatsDTO getStatistiquesMois(Integer mois, Integer annee) {
        // Calculer les dates de début et fin du mois
        YearMonth yearMonth = YearMonth.of(annee, mois);
        LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        ChiffreAffairesStatsDTO stats = new ChiffreAffairesStatsDTO();
        stats.setMois(mois);
        stats.setAnnee(annee);
        stats.setDeviseCode("MGA");

        // Récupérer tous les départs du mois (même logique que la page /departs)
        List<DepartDTO> departs = departRepository.findByDateRange(startDate, endDate).stream()
                .map(d -> departService.findById(d.getId()))
                .toList();

        // 1. CA Réservations
        // Théorique = somme du CA maximum de tous les départs (chiffreAffairesMax)
        BigDecimal caReservationsTheorique = departs.stream()
                .map(d -> d.getChiffreAffairesMax() != null ? d.getChiffreAffairesMax() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setCaReservationsTheorique(caReservationsTheorique);

        // Réel = somme du CA réel de tous les départs (chiffreAffaires = montants payés)
        BigDecimal caReservationsReel = departs.stream()
                .map(d -> d.getChiffreAffaires() != null ? d.getChiffreAffaires() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setCaReservationsReel(caReservationsReel);

        // 2. CA Diffusions Publicité
        // Théorique = somme des montants facturés pour les diffusions (montantDiffusionsPublicite)
        BigDecimal caDiffusionsTheorique = departs.stream()
                .map(d -> d.getMontantDiffusionsPublicite() != null ? d.getMontantDiffusionsPublicite() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setCaDiffusionsTheorique(caDiffusionsTheorique);

        // Réel = somme des paiements publicité effectués (montantPublicitesPaye)
        BigDecimal caDiffusionsReel = departs.stream()
                .map(d -> d.getMontantPublicitesPaye() != null ? d.getMontantPublicitesPaye() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setCaDiffusionsReel(caDiffusionsReel);

        // 3. CA Ventes Produits
        // Théorique = valeur du stock initial (quantité initiale * prix unitaire)
        BigDecimal caVentesProduitsTheorique = stockDepartRepository.calculateTotalStockValueByDateRange(startDate, endDate);
        stats.setCaVentesProduitsTheorique(caVentesProduitsTheorique != null ? caVentesProduitsTheorique : BigDecimal.ZERO);
        
        // Réel = somme des ventes effectuées (montant total des ventes)
        BigDecimal caVentesProduitsReel = venteProduitRepository.findByDateRange(startDate, endDate)
                .stream()
                .map(v -> v.getMontantTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setCaVentesProduitsReel(caVentesProduitsReel);

        // Totaux
        BigDecimal totalTheorique = stats.getCaReservationsTheorique()
                .add(stats.getCaDiffusionsTheorique())
                .add(stats.getCaVentesProduitsTheorique());
        stats.setTotalTheorique(totalTheorique);

        BigDecimal totalReel = stats.getCaReservationsReel()
                .add(stats.getCaDiffusionsReel())
                .add(stats.getCaVentesProduitsReel());
        stats.setTotalReel(totalReel);

        return stats;
    }
}
