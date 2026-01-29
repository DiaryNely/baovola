package com.taxi_brousse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDTO {
    private Long id;
    private String code;
    private String nom;
    private String description;
    private Long refCategorieProduitId;
    private String refCategorieProduitLibelle;
    private Long refDeviseId;
    private String refDeviseCode;
    private BigDecimal prixVente;
    private Boolean actif;
    private LocalDateTime createdAt;
}
