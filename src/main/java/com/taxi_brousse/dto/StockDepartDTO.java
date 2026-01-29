package com.taxi_brousse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDepartDTO {
    private Long id;
    private Long departId;
    private String departCode;
    private Long produitId;
    private String produitCode;
    private String produitNom;
    private String produitCategorie;
    private Integer quantiteInitiale;
    private Integer quantiteVendue;
    private Integer quantiteDisponible;
    private BigDecimal prixUnitaire;
    private Long refDeviseId;
    private String refDeviseCode;
    private String notes;
    private LocalDateTime createdAt;
    
    // Champ calculé
    private BigDecimal valeurStockDisponible;
}
