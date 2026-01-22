package com.taxi_brousse.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrajetDTO {
    
    private Long id;
    
    @NotBlank(message = "Le code est obligatoire")
    @Size(max = 80, message = "Le code ne peut pas dépasser 80 caractères")
    private String code;
    
    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 200, message = "Le libellé ne peut pas dépasser 200 caractères")
    private String libelle;
    
    private Long lieuDepartId;
    private String lieuDepartNom;
    
    private Long lieuArriveeId;
    private String lieuArriveeNom;
    
    private Double distanceKm;
    
    private Integer dureeEstimeeMin;
    
    @NotNull(message = "Le statut actif est obligatoire")
    private Boolean actif = true;

    private Long cooperativeId;
    private String cooperativeNom;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime createdAt;
}
