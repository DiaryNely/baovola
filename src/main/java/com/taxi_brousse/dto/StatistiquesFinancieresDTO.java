package com.taxi_brousse.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesFinancieresDTO {
    
    // Statistiques globales
    private BigDecimal revenusTotal; // CA + publicités
    private BigDecimal revenusReservations; // CA uniquement
    private BigDecimal revenusPublicites; // Publicités uniquement
    
    private Integer nombreDeparts;
    private Integer nombreReservations;
    private Double tauxRemplissageMoyen; // %
    
    // Évolution mensuelle
    private List<RevenuMensuelDTO> revenusMensuels;
    
    // Rentabilité par trajet
    private List<RentabiliteTrajetDTO> rentabiliteParTrajet;
    
    // Top 5 trajets les plus rentables
    private List<RentabiliteTrajetDTO> top5Trajets;
    
    // Statistiques par statut
    private Long departsEnCours;
    private Long departsTermines;
    private Long departsAnnules;
    private Long deprogrammes;
}
