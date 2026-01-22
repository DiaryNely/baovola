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
public class DepartTarifRemiseDTO {
    private Long id;

    @NotNull(message = "Le départ est obligatoire")
    private Long departId;

    @NotNull(message = "La catégorie de siège est obligatoire")
    private Long refSiegeCategorieId;
    private String refSiegeCategorieCode;
    private String refSiegeCategorieLibelle;

    @NotNull(message = "La catégorie de passager est obligatoire")
    private Long refPassagerCategorieId;
    private String refPassagerCategorieCode;
    private String refPassagerCategorieLibelle;

    @NotNull(message = "Le type de remise est obligatoire")
    private String typeRemise; // VALEUR | POURCENT

    @NotNull(message = "Le montant de remise est obligatoire")
    @Min(value = 0, message = "Le montant doit être positif")
    private BigDecimal montant;
}
