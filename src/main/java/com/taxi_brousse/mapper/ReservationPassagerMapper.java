package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.ReservationPassagerDTO;
import com.taxi_brousse.entity.ReservationPassager;
import org.springframework.stereotype.Component;

@Component
public class ReservationPassagerMapper {

    public ReservationPassagerDTO toDTO(ReservationPassager entity) {
        if (entity == null) {
            return null;
        }
        
        ReservationPassagerDTO dto = new ReservationPassagerDTO();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        dto.setNumeroSiege(entity.getNumeroSiege());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getRefSiegeCategorie() != null) {
            dto.setRefSiegeCategorieId(entity.getRefSiegeCategorie().getId());
            dto.setRefSiegeCategorieCode(entity.getRefSiegeCategorie().getCode());
            dto.setRefSiegeCategorieLibelle(entity.getRefSiegeCategorie().getLibelle());
        }

        if (entity.getRefPassagerCategorie() != null) {
            dto.setRefPassagerCategorieId(entity.getRefPassagerCategorie().getId());
            dto.setRefPassagerCategorieCode(entity.getRefPassagerCategorie().getCode());
            dto.setRefPassagerCategorieLibelle(entity.getRefPassagerCategorie().getLibelle());
        }

        dto.setMontantTarif(entity.getMontantTarif());
        dto.setMontantRemise(entity.getMontantRemise());
        if (entity.getRefDevise() != null) {
            dto.setDeviseCode(entity.getRefDevise().getCode());
        }
        
        if (entity.getReservation() != null) {
            dto.setReservationId(entity.getReservation().getId());
        }
        
        if (entity.getDepart() != null) {
            dto.setDepartId(entity.getDepart().getId());
        }
        
        return dto;
    }

    public ReservationPassager toEntity(ReservationPassagerDTO dto) {
        if (dto == null) {
            return null;
        }
        
        ReservationPassager entity = new ReservationPassager();
        entity.setId(dto.getId());
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setNumeroSiege(dto.getNumeroSiege());
        entity.setMontantTarif(dto.getMontantTarif());
        entity.setMontantRemise(dto.getMontantRemise());
        
        return entity;
    }
}
