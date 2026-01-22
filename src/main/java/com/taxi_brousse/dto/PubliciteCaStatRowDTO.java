package com.taxi_brousse.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PubliciteCaStatRowDTO {
    private Long publiciteId;
    private String publiciteTitre;
    private String societeNom;
    private Long totalRepetitions;
    private BigDecimal tarifUnitaire;
    private BigDecimal montantTotal;
    private String deviseCode;
    private String deviseSymbole;
}
