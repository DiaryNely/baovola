package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.DepartDTO;
import com.taxi_brousse.entity.Depart;
import org.springframework.stereotype.Component;

@Component
public class DepartMapper {

    public DepartDTO toDTO(Depart entity) {
        if (entity == null) {
            return null;
        }
        
        DepartDTO dto = new DepartDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setDateHeureDepart(entity.getDateHeureDepart());
        dto.setDateHeureArriveeEstimee(entity.getDateHeureArriveeEstimee());
        dto.setCapaciteOverride(entity.getCapaciteOverride());
        dto.setCreatedAt(entity.getCreatedAt());
        
        if (entity.getCooperative() != null) {
            dto.setCooperativeId(entity.getCooperative().getId());
            dto.setCooperativeNom(entity.getCooperative().getNom());
        }
        
        if (entity.getTrajet() != null) {
            dto.setTrajetId(entity.getTrajet().getId());
            dto.setTrajetLibelle(entity.getTrajet().getLibelle());
        }
        
        if (entity.getVehicule() != null) {
            dto.setVehiculeId(entity.getVehicule().getId());
            dto.setVehiculeImmatriculation(entity.getVehicule().getImmatriculation());
            dto.setNombrePlaces(entity.getCapaciteOverride() != null ? 
                entity.getCapaciteOverride() : entity.getVehicule().getNombrePlaces());
        }
        
        if (entity.getLieuDepart() != null) {
            dto.setLieuDepartId(entity.getLieuDepart().getId());
            dto.setLieuDepartNom(entity.getLieuDepart().getNom());
        }
        
        if (entity.getLieuArrivee() != null) {
            dto.setLieuArriveeId(entity.getLieuArrivee().getId());
            dto.setLieuArriveeNom(entity.getLieuArrivee().getNom());
        }
        
        if (entity.getRefDepartStatut() != null) {
            dto.setRefDepartStatutId(entity.getRefDepartStatut().getId());
            dto.setRefDepartStatutCode(entity.getRefDepartStatut().getCode());
            dto.setRefDepartStatutLibelle(entity.getRefDepartStatut().getLibelle());
        }
        
        return dto;
    }

    public Depart toEntity(DepartDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Depart entity = new Depart();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setDateHeureDepart(dto.getDateHeureDepart());
        entity.setDateHeureArriveeEstimee(dto.getDateHeureArriveeEstimee());
        entity.setCapaciteOverride(dto.getCapaciteOverride());
        
        return entity;
    }
}
