package com.taxi_brousse.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PubliciteDTO {
    
    private Long id;
    private String code;
    private Long societePublicitaireId;
    private String societePublicitaireNom;
    private String titre;
    private String description;
    private String urlVideo;
    private Integer dureeSecondes;
    private LocalDate dateDebutValidite;
    private LocalDate dateFinValidite;
    private Boolean actif;
    private LocalDateTime createdAt;
}
