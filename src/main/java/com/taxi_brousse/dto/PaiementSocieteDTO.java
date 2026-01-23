package com.taxi_brousse.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementSocieteDTO {
    private Long societeId;
    private String societeCode;
    private String societeNom;
    private BigDecimal montantFacture;      // Total facturé pour ce départ
    private BigDecimal montantPaye;         // Total déjà payé
    private BigDecimal montantRestant;      // Reste à payer
    private String deviseCode;
    private String deviseSymbole;
}
