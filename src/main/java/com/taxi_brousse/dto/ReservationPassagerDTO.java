package com.taxi_brousse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPassagerDTO {
    
    private Long id;
    
    @NotNull(message = "La réservation est obligatoire")
    private Long reservationId;
    
    @NotNull(message = "Le départ est obligatoire")
    private Long departId;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 120, message = "Le nom ne peut pas dépasser 120 caractères")
    private String nom;
    
    @Size(max = 120, message = "Le prénom ne peut pas dépasser 120 caractères")
    private String prenom;
    
    @NotNull(message = "Le numéro de siège est obligatoire")
    @Min(value = 1, message = "Le numéro de siège doit être supérieur à 0")
    private Integer numeroSiege;

    private Long refSiegeCategorieId;
    private String refSiegeCategorieCode;
    private String refSiegeCategorieLibelle;

    private Long refPassagerCategorieId;
    private String refPassagerCategorieCode;
    private String refPassagerCategorieLibelle;

    private BigDecimal montantTarif;
    private BigDecimal montantRemise;
    private String deviseCode;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime createdAt;
}
