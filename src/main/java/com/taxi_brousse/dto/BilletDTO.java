package com.taxi_brousse.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BilletDTO {
    private Long id;
    private String numero;
    private String code;
    private Long reservationId;
    private String reservationCode;
    private String passagerNom;
    private String passagerPrenom;
    private Integer numeroSiege;
    private LocalDateTime dateEmission;
    private String contenuQr;
    private Long refBilletStatutId;
    private String refBilletStatutCode;
    private String refBilletStatutLibelle;
    
    // Informations enrichies du départ
    private String departCode;
    private String trajetLibelle;
    private String lieuDepartNom;
    private String lieuArriveeNom;
    private LocalDateTime dateHeureDepart;
    private String cooperativeNom;
    private String vehiculeImmatriculation;
}
