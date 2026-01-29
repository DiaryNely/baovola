package com.taxi_brousse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiffreAffairesStatsDTO {
    // CA Réservations
    private BigDecimal caReservationsTheorique;
    private BigDecimal caReservationsReel;
    
    // CA Diffusions Publicité
    private BigDecimal caDiffusionsTheorique;
    private BigDecimal caDiffusionsReel;
    
    // CA Ventes Produits
    private BigDecimal caVentesProduitsTheorique;
    private BigDecimal caVentesProduitsReel;
    
    // Totaux
    private BigDecimal totalTheorique;
    private BigDecimal totalReel;
    
    // Période
    private Integer mois;
    private Integer annee;
    private String deviseCode;
    
    // Détails par départ
    private List<DepartDTO> departs;
}
