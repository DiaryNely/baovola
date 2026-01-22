package com.taxi_brousse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    
    private Long id;
    
    @NotBlank(message = "Le code est obligatoire")
    @Size(max = 80, message = "Le code ne peut pas dépasser 80 caractères")
    private String code;
    
    @NotNull(message = "Le client est obligatoire")
    private Long clientId;
    private String clientNom;
    private String clientPrenom;
    
    @NotNull(message = "Le départ est obligatoire")
    private Long departId;
    private String departCode;
    private String trajetLibelle;
    private LocalDateTime dateHeureDepart;
    
    // Informations du trajet
    private String lieuDepartNom;
    private String lieuArriveeNom;
    
    // Informations du véhicule et coopérative
    private String vehiculeImmatriculation;
    private String cooperativeNom;
    
    @NotNull(message = "Le statut de réservation est obligatoire")
    private Long refReservationStatutId;
    private String refReservationStatutLibelle;
    
    // Statut de paiement
    private String paiementStatutLibelle;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreation;
    
    private String notes;
    
    private BigDecimal montantTotal;
    private BigDecimal montantPaye;
    private BigDecimal resteAPayer;
    
    @NotEmpty(message = "Au moins un passager est requis")
    @Valid
    private List<ReservationPassagerDTO> passagers = new ArrayList<>();
}
