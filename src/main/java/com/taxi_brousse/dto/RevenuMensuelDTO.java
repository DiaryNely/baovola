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
public class RevenuMensuelDTO {
    
    private Integer annee;
    private Integer mois;
    private String moisLabel; // Ex: "Janvier 2026"
    
    private BigDecimal revenusTotal;
    private BigDecimal revenusReservations;
    private BigDecimal revenusPublicites;
    
    private Integer nombreDeparts;
    private Integer nombreReservations;
    private Double tauxRemplissage; // %
}
