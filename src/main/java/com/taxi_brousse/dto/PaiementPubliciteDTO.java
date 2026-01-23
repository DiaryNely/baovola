package com.taxi_brousse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaiementPubliciteDTO {
    private Long id;

    @NotNull(message = "La société publicitaire est obligatoire")
    private Long societePublicitaireId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private Long refDeviseId;
    private LocalDateTime datePaiement;
    private Integer factureMois;
    private Integer factureAnnee;
    private String note;
    private LocalDateTime createdAt;

    // Affichage
    private String societePublicitaireNom;
    private String deviseCode;
    private String deviseSymbole;
}
