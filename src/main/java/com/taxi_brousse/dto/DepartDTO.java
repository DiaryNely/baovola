package com.taxi_brousse.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartDTO {
    
    private Long id;
    
    @Size(max = 80, message = "Le code ne peut pas dépasser 80 caractères")
    private String code;
    
    @NotNull(message = "La coopérative est obligatoire")
    private Long cooperativeId;
    private String cooperativeNom;
    
    @NotNull(message = "Le trajet est obligatoire")
    private Long trajetId;
    private String trajetLibelle;
    
    @NotNull(message = "Le véhicule est obligatoire")
    private Long vehiculeId;
    private String vehiculeImmatriculation;
    
    private Long lieuDepartId;
    private String lieuDepartNom;
    
    private Long lieuArriveeId;
    private String lieuArriveeNom;
    
    @NotNull(message = "Le statut du départ est obligatoire")
    private Long refDepartStatutId;
    private String refDepartStatutCode;
    private String refDepartStatutLibelle;
    
    @NotNull(message = "La date et heure de départ sont obligatoires")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateHeureDepart;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateHeureArriveeEstimee;
    
    private Integer capaciteOverride;
    
    private Integer nombrePlaces;
    private Integer placesOccupees;
    private Integer placesDisponibles;

    private List<VehiculeSiegeConfigDTO> siegeConfigs = new ArrayList<>();
    private List<DepartTarifSiegeDTO> tarifsSieges = new ArrayList<>();
    private List<ChiffreAffairesCategorieDTO> chiffreAffairesParCategorie = new ArrayList<>();
    
    private java.math.BigDecimal chiffreAffaires;

    private java.math.BigDecimal montantDiffusionsPublicite;
    private String montantDiffusionsPubliciteDeviseCode;
    private String montantDiffusionsPubliciteDeviseSymbole;

    private List<PaiementSocieteDTO> paiementsParSociete = new ArrayList<>();

    private java.math.BigDecimal chiffreAffairesMax;
    private String chiffreAffairesMaxDeviseCode;
    private String chiffreAffairesMaxDeviseSymbole;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
