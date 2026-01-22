package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.CooperativeDTO;
import com.taxi_brousse.entity.Cooperative;
import org.springframework.stereotype.Component;

@Component
public class CooperativeMapper {

    public CooperativeDTO toDTO(Cooperative entity) {
        if (entity == null) {
            return null;
        }
        
        CooperativeDTO dto = new CooperativeDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNom(entity.getNom());
        dto.setTelephone(entity.getTelephone());
        dto.setEmail(entity.getEmail());
        dto.setAdresse(entity.getAdresse());
        dto.setCreatedAt(entity.getCreatedAt());
        
        return dto;
    }

    public Cooperative toEntity(CooperativeDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Cooperative entity = new Cooperative();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setNom(dto.getNom());
        entity.setTelephone(dto.getTelephone());
        entity.setEmail(dto.getEmail());
        entity.setAdresse(dto.getAdresse());
        
        return entity;
    }
}
