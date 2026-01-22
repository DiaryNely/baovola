package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.TrajetDTO;
import com.taxi_brousse.entity.Trajet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TrajetMapper {

    public TrajetDTO toDTO(Trajet entity) {
        if (entity == null) {
            return null;
        }
        
        TrajetDTO dto = new TrajetDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setLibelle(entity.getLibelle());
        dto.setActif(entity.getActif());
        dto.setDureeEstimeeMin(entity.getDureeEstimeeMin());
        dto.setCreatedAt(entity.getCreatedAt());
        
        if (entity.getDistanceKm() != null) {
            dto.setDistanceKm(entity.getDistanceKm().doubleValue());
        }
        
        if (entity.getLieuDepart() != null) {
            dto.setLieuDepartId(entity.getLieuDepart().getId());
            dto.setLieuDepartNom(entity.getLieuDepart().getNom());
        }
        
        if (entity.getLieuArrivee() != null) {
            dto.setLieuArriveeId(entity.getLieuArrivee().getId());
            dto.setLieuArriveeNom(entity.getLieuArrivee().getNom());
        }
        
        return dto;
    }

    public Trajet toEntity(TrajetDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Trajet entity = new Trajet();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setLibelle(dto.getLibelle());
        entity.setActif(dto.getActif());
        entity.setDureeEstimeeMin(dto.getDureeEstimeeMin());
        
        if (dto.getDistanceKm() != null) {
            entity.setDistanceKm(BigDecimal.valueOf(dto.getDistanceKm()));
        }
        
        return entity;
    }
}
