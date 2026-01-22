package com.taxi_brousse.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementRequest {

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal montant;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private String modePaiement; // ESPECES, MOBILE_MONEY, CARTE_BANCAIRE, CHEQUE

    private String referenceTransaction; // Pour mobile money / carte bancaire

    private String numeroTelephone; // Pour mobile money

    private String notes;
}
