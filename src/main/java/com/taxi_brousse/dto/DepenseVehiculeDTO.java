package com.taxi_brousse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepenseVehiculeDTO {
    
    private Long id;
    
    @NotNull(message = "Le véhicule est obligatoire")
    private Long vehiculeId;
    private String vehiculeImmatriculation;
    
    @NotNull(message = "La coopérative est obligatoire")
    private Long cooperativeId;
    private String cooperativeNom;
    
    @NotNull(message = "Le type de dépense est obligatoire")
    private Long refTypeDepenseId;
    private String refTypeDepenseLibelle;
    
    @NotNull(message = "La devise est obligatoire")
    private Long refDeviseId;
    private String refDeviseCode;
    
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le montant doit avoir au maximum 10 chiffres entiers et 2 décimales")
    private BigDecimal montant;
    
    @NotNull(message = "La date de dépense est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDepense;
    
    private String description;
    
    @Size(max = 100, message = "Le numéro de pièce ne peut pas dépasser 100 caractères")
    private String numeroPiece;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime createdAt;
}
