package com.taxi_brousse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DepartPubliciteDTO {
    
    private Long id;
    
    @NotNull(message = "Le départ est obligatoire")
    private Long departId;
    
    @NotNull(message = "La publicité est obligatoire")
    private Long publiciteId;
    
    private Long tarifPubliciteId;
    
    private LocalDateTime dateDiffusion;
    
    // Montant calculé automatiquement par le service selon le tarif en vigueur
    private BigDecimal montantFacture;
    
    // Devise définie automatiquement selon le tarif
    private Long refDeviseId;
    
    private String statutDiffusion;
    
    @NotNull(message = "Le nombre de répétitions est obligatoire")
    @Positive(message = "Le nombre de répétitions doit être positif")
    private Integer nombreRepetitions;
    
    private LocalDateTime createdAt;
    
    // Informations supplémentaires pour l'affichage
    private String departCode;
    private String publiciteTitre;
    private String societePublicitaireNom;
    private String deviseCode;
    private String deviseSymbole;
    private LocalDateTime departDateHeureDepart;
    private String trajetDescription;
}
