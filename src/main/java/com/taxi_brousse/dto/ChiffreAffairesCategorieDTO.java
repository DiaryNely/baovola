package com.taxi_brousse.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiffreAffairesCategorieDTO {
    private Long refSiegeCategorieId;
    private String refSiegeCategorieCode;
    private String refSiegeCategorieLibelle;
    private Integer nbPassagers;
    private BigDecimal montantTotal;
    private String deviseCode;
    private String deviseSymbole;
}
