package com.taxi_brousse.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PubliciteCaStatsDTO {
    private int mois;
    private int annee;
    private long totalRepetitions;
    private long nombrePublicites;
    private BigDecimal montantTotal;
    private String deviseCode;
    private String deviseSymbole;
    private List<PubliciteCaStatRowDTO> details = new ArrayList<>();
}
