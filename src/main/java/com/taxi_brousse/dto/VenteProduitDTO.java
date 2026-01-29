package com.taxi_brousse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenteProduitDTO {
    private Long id;
    private Long stockDepartId;
    private Long departId;
    private String departCode;
    private Long produitId;
    private String produitNom;
    private Long clientId;
    private String clientNom;
    private String clientPrenom;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal montantTotal;
    private Long refDeviseId;
    private String refDeviseCode;
    private LocalDateTime dateVente;
    private String notes;
    private LocalDateTime createdAt;
}
