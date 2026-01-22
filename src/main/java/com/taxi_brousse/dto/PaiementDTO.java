package com.taxi_brousse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementDTO {
    private Long id;

    private Long reservationId;
    private String reservationCode;
    private String clientNom;
    private String clientPrenom;
    private String departCode;

    private BigDecimal montant;
    private Long refDeviseId;
    private String deviseCode;
    private String deviseSymbole;

    private Long refPaiementMethodeId;
    private String refPaiementMethodeCode;
    private String refPaiementMethodeLibelle;

    private Long refPaiementStatutId;
    private String refPaiementStatutCode;
    private String refPaiementStatutLibelle;

    private String referenceExterne;
    private String notes;
    private LocalDateTime datePaiement;
    private LocalDateTime createdAt;
}
