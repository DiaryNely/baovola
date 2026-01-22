package com.taxi_brousse.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartTarifSiegeDTO {
    private Long id;

    @NotNull(message = "Le départ est obligatoire")
    private Long departId;

    @NotNull(message = "La catégorie de siège est obligatoire")
    private Long refSiegeCategorieId;

    private String refSiegeCategorieCode;
    private String refSiegeCategorieLibelle;
    private Integer refSiegeCategorieOrdre;

    @NotNull(message = "La devise est obligatoire")
    private Long refDeviseId;

    private String deviseCode;
    private String deviseSymbole;

    @NotNull(message = "Le montant est obligatoire")
    @Min(value = 1, message = "Le montant doit être positif")
    private BigDecimal montant;
}
