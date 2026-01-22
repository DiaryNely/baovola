package com.taxi_brousse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    
    // Statistiques des départs
    private Long departsAujourdhui;
    private Long departsCetteSemaine;
    private Long departsCeMois;
    
    // Statistiques des réservations
    private Long reservationsEnCours;
    private Long reservationsConfirmees;
    private Long reservationsAujourdhui;
    
    // Statistiques des véhicules
    private Long totalVehicules;
    private Long vehiculesActifs;
    private Long vehiculesEnPanne;
    private Double tauxRemplissageMoyen;
    
    // Chiffre d'affaires
    private BigDecimal chiffreAffairesAujourdhui;
    private BigDecimal chiffreAffairesSemaine;
    private BigDecimal chiffreAffairesMois;
    
    // Alertes
    private List<AlerteDTO> alertes;
    
    // Prochains départs (24h)
    private List<DepartDTO> prochainsDeparts;
    
    // Statistiques générales
    private Long totalClients;
    private Long totalChauffeurs;
    private Long totalCooperatives;
    private Long totalTrajets;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlerteDTO {
        private String type; // "warning", "danger", "info"
        private String titre;
        private String message;
        private String lien;
    }
}
