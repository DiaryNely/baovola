package com.taxi_brousse.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreerReservationRequest {
    
    @NotNull(message = "Le client est obligatoire")
    private Long clientId;
    
    @NotNull(message = "Le départ est obligatoire")
    private Long departId;
    
    private String notes;
    
    @NotEmpty(message = "Au moins un passager est requis")
    @Valid
    private List<PassagerRequest> passagers;
    
    // Champs pour le paiement
    @NotNull(message = "Le mode de paiement est obligatoire")
    private String modePaiement; // PAIEMENT_IMMEDIAT, COMPTOIR, EMBARQUEMENT
    
    private PaiementRequest paiementInfo; // Si paiement immédiat
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassagerRequest {
        
        @NotNull(message = "Le nom est obligatoire")
        private String nom;
        
        private String prenom;
        
        @NotNull(message = "Le numéro de siège est obligatoire")
        private Integer numeroSiege;

        @NotNull(message = "La catégorie de passager est obligatoire")
        private Long refPassagerCategorieId;
    }
}
