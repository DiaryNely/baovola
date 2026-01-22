package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.ChauffeurDTO;
import com.taxi_brousse.entity.Chauffeur;
import org.springframework.stereotype.Component;

@Component
public class ChauffeurMapper {

    public ChauffeurDTO toDTO(Chauffeur entity) {
        if (entity == null) {
            return null;
        }
        
        ChauffeurDTO dto = new ChauffeurDTO();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        dto.setTelephone(entity.getTelephone());
        dto.setNumeroPermis(entity.getNumeroPermis());
        dto.setDateNaissance(entity.getDateNaissance());
        dto.setDateEmbauche(entity.getDateEmbauche());
        dto.setCreatedAt(entity.getCreatedAt());
        
        return dto;
    }

    public Chauffeur toEntity(ChauffeurDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Chauffeur entity = new Chauffeur();
        entity.setId(dto.getId());
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setTelephone(dto.getTelephone());
        entity.setNumeroPermis(dto.getNumeroPermis());
        entity.setDateNaissance(dto.getDateNaissance());
        entity.setDateEmbauche(dto.getDateEmbauche());
        
        return entity;
    }
}
