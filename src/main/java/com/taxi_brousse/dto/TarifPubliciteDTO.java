package com.taxi_brousse.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TarifPubliciteDTO {
    
    private Long id;
    
    @NotNull(message = "La devise est obligatoire")
    private Long refDeviseId;
    
    private String refDeviseCode;
    private String refDeviseLibelle;
    private String refDeviseSymbole;
    
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Format du montant invalide")
    private BigDecimal montant;
    
    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;
    
    private LocalDate dateFin;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    private Boolean actif = true;
    
    private LocalDateTime createdAt;
    
    // Champs calculés
    private Boolean estActuel; // true si date_fin = NULL
    private Boolean estEnVigueur; // true si date actuelle est dans la période
    private Long nombreJoursValidite; // nombre de jours entre debut et fin
}
