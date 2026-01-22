package com.taxi_brousse.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatInfoDTO {
    private Integer numeroSiege;
    private Boolean disponible;

    private Long refSiegeCategorieId;
    private String refSiegeCategorieCode;
    private String refSiegeCategorieLibelle;

    private BigDecimal montant;
    private String deviseCode;
    private String deviseSymbole;
}
