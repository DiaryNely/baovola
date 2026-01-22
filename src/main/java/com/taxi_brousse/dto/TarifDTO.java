package com.taxi_brousse.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifDTO {
    
    private Long id;
    
    // Relations IDs
    private Long cooperativeId;
    private Long trajetId;
    private Long refVehiculeTypeId;
    private Long refDeviseId;
    
    // Données enrichies pour affichage
    private String cooperativeNom;
    private String trajetLibelle;
    private String trajetCode;
    private String vehiculeTypeLibelle;
    private String deviseCode;
    private String deviseSymbole;
    
    // Données du tarif
    private BigDecimal montant;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    
    // Indicateur
    private Boolean actif; // true si le tarif est applicable aujourd'hui
}
