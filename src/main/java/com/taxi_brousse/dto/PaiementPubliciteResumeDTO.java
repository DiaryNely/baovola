package com.taxi_brousse.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaiementPubliciteResumeDTO {
    private Long societePublicitaireId;
    private String societePublicitaireNom;
    private BigDecimal montantTotalFacture;
    private BigDecimal montantTotalPaye;
    private BigDecimal montantRestant;
    private String deviseCode;
    private String deviseSymbole;
}
