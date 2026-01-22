package com.taxi_brousse.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChauffeurDTO {
    
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;
    
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;
    
    @Size(max = 30, message = "Le téléphone ne peut pas dépasser 30 caractères")
    private String telephone;
    
    @NotBlank(message = "Le numéro de permis est obligatoire")
    @Size(max = 60, message = "Le numéro de permis ne peut pas dépasser 60 caractères")
    private String numeroPermis;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEmbauche;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime createdAt;

    private Long vehiculeId;
    private String vehiculeImmatriculation;

    private Long refChauffeurVehiculeRoleId;
    private String refChauffeurVehiculeRoleCode;
    private String refChauffeurVehiculeRoleLibelle;
}
