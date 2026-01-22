package com.taxi_brousse.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.taxi_brousse.dto.ReservationDTO;
import com.taxi_brousse.entity.Reservation;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class ReservationMapper {
    

    public ReservationDTO toDTO(Reservation entity) {
        if (entity == null) {
            return null;
        }
        
        ReservationDTO dto = new ReservationDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setDateCreation(entity.getDateCreation());
        dto.setNotes(entity.getNotes());
        
        if (entity.getClient() != null) {
            dto.setClientId(entity.getClient().getId());
            dto.setClientNom(entity.getClient().getNom());
            dto.setClientPrenom(entity.getClient().getPrenom());
        }
        
        if (entity.getDepart() != null) {
            dto.setDepartId(entity.getDepart().getId());
            dto.setDepartCode(entity.getDepart().getCode());
            dto.setDateHeureDepart(entity.getDepart().getDateHeureDepart());
            
            if (entity.getDepart().getTrajet() != null) {
                dto.setTrajetLibelle(entity.getDepart().getTrajet().getLibelle());
                
                // Lieux de départ et d'arrivée
                if (entity.getDepart().getTrajet().getLieuDepart() != null) {
                    dto.setLieuDepartNom(entity.getDepart().getTrajet().getLieuDepart().getNom());
                }
                if (entity.getDepart().getTrajet().getLieuArrivee() != null) {
                    dto.setLieuArriveeNom(entity.getDepart().getTrajet().getLieuArrivee().getNom());
                }
            }
            
            // Véhicule
            if (entity.getDepart().getVehicule() != null) {
                dto.setVehiculeImmatriculation(entity.getDepart().getVehicule().getImmatriculation());
            }
            
            // Coopérative directement depuis le départ
            if (entity.getDepart().getCooperative() != null) {
                dto.setCooperativeNom(entity.getDepart().getCooperative().getNom());
            }
        }
        
        if (entity.getRefReservationStatut() != null) {
            dto.setRefReservationStatutId(entity.getRefReservationStatut().getId());
            dto.setRefReservationStatutLibelle(entity.getRefReservationStatut().getLibelle());
        }
        
        // Déterminer le statut de paiement
        dto.setMontantTotal(entity.getMontantTotal());
        dto.setMontantPaye(entity.getMontantPaye());
        dto.setResteAPayer(entity.getResteAPayer());
        
        if (entity.getMontantTotal() != null && entity.getMontantPaye() != null) {
            if (entity.getMontantPaye().compareTo(BigDecimal.ZERO) == 0) {
                dto.setPaiementStatutLibelle("NON_PAYE");
            } else if (entity.getResteAPayer() != null && entity.getResteAPayer().compareTo(BigDecimal.ZERO) == 0) {
                dto.setPaiementStatutLibelle("PAYE");
            } else {
                dto.setPaiementStatutLibelle("PARTIEL");
            }
        }
        
        return dto;
    }

    public Reservation toEntity(ReservationDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Reservation entity = new Reservation();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setNotes(dto.getNotes());
        
        return entity;
    }
}
