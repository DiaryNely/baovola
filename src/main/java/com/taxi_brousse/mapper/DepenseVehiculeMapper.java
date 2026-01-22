package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.DepenseVehiculeDTO;
import com.taxi_brousse.entity.DepenseVehicule;
import org.springframework.stereotype.Component;

@Component
public class DepenseVehiculeMapper {

    public DepenseVehiculeDTO toDTO(DepenseVehicule entity) {
        if (entity == null) {
            return null;
        }
        
        DepenseVehiculeDTO dto = new DepenseVehiculeDTO();
        dto.setId(entity.getId());
        dto.setMontant(entity.getMontant());
        dto.setDateDepense(entity.getDateDepense());
        dto.setDescription(entity.getDescription());
        dto.setNumeroPiece(entity.getNumeroPiece());
        dto.setCreatedAt(entity.getCreatedAt());
        
        if (entity.getVehicule() != null) {
            dto.setVehiculeId(entity.getVehicule().getId());
            dto.setVehiculeImmatriculation(entity.getVehicule().getImmatriculation());
        }
        
        if (entity.getCooperative() != null) {
            dto.setCooperativeId(entity.getCooperative().getId());
            dto.setCooperativeNom(entity.getCooperative().getNom());
        }
        
        if (entity.getRefTypeDepense() != null) {
            dto.setRefTypeDepenseId(entity.getRefTypeDepense().getId());
            dto.setRefTypeDepenseLibelle(entity.getRefTypeDepense().getLibelle());
        }
        
        if (entity.getRefDevise() != null) {
            dto.setRefDeviseId(entity.getRefDevise().getId());
            dto.setRefDeviseCode(entity.getRefDevise().getCode());
        }
        
        return dto;
    }

    public DepenseVehicule toEntity(DepenseVehiculeDTO dto) {
        if (dto == null) {
            return null;
        }
        
        DepenseVehicule entity = new DepenseVehicule();
        entity.setId(dto.getId());
        entity.setMontant(dto.getMontant());
        entity.setDateDepense(dto.getDateDepense());
        entity.setDescription(dto.getDescription());
        entity.setNumeroPiece(dto.getNumeroPiece());
        
        return entity;
    }
}
