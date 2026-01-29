package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.RefCategorieProduitDTO;
import com.taxi_brousse.entity.reference.RefCategorieProduit;
import org.springframework.stereotype.Component;

@Component
public class RefCategorieProduitMapper {

    public RefCategorieProduitDTO toDTO(RefCategorieProduit entity) {
        if (entity == null) {
            return null;
        }

        RefCategorieProduitDTO dto = new RefCategorieProduitDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setLibelle(entity.getLibelle());
        dto.setDescription(entity.getDescription());
        dto.setActif(entity.getActif());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public RefCategorieProduit toEntity(RefCategorieProduitDTO dto) {
        if (dto == null) {
            return null;
        }

        RefCategorieProduit entity = new RefCategorieProduit();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setLibelle(dto.getLibelle());
        entity.setDescription(dto.getDescription());
        entity.setActif(dto.getActif());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }
}
