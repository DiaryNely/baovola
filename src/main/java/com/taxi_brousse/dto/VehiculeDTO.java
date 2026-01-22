package com.taxi_brousse.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeDTO {
    
    private Long id;
    
    @NotBlank(message = "L'immatriculation est obligatoire")
    @Size(max = 30, message = "L'immatriculation ne peut pas dépasser 30 caractères")
    private String immatriculation;
    
    @Size(max = 80, message = "La marque ne peut pas dépasser 80 caractères")
    private String marque;
    
    @Size(max = 80, message = "Le modèle ne peut pas dépasser 80 caractères")
    private String modele;
    
    private Integer annee;
    
    private Integer nombrePlaces;

    private List<VehiculeSiegeConfigDTO> siegeConfigs = new ArrayList<>();
    
    private Long refVehiculeTypeId;
    private String refVehiculeTypeLibelle;
    
    private Long refVehiculeEtatId;
    private String refVehiculeEtatLibelle;

    private Long cooperativeId;
    private String cooperativeNom;

    private Long chauffeurId;
    private String chauffeurNom;
    private String chauffeurPrenom;

    private Long refChauffeurVehiculeRoleId;
    private String refChauffeurVehiculeRoleCode;
    private String refChauffeurVehiculeRoleLibelle;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateMiseEnService;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime createdAt;
}
