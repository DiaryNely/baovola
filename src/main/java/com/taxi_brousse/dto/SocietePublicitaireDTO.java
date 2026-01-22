package com.taxi_brousse.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SocietePublicitaireDTO {
    private Long id;
    private String code;
    private String nom;
    private String telephone;
    private String email;
    private String adresse;
    private String contactNom;
    private String contactTelephone;
    private Boolean actif;
    private LocalDateTime createdAt;
}
