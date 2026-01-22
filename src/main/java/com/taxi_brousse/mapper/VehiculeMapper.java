package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.VehiculeDTO;
import com.taxi_brousse.entity.Vehicule;
import org.springframework.stereotype.Component;

@Component
public class VehiculeMapper {

    public VehiculeDTO toDTO(Vehicule entity) {
        if (entity == null) {
            return null;
        }
        
        VehiculeDTO dto = new VehiculeDTO();
        dto.setId(entity.getId());
        dto.setImmatriculation(entity.getImmatriculation());
        dto.setMarque(entity.getMarque());
        dto.setModele(entity.getModele());
        dto.setAnnee(entity.getAnnee());
        dto.setNombrePlaces(entity.getNombrePlaces());
        dto.setDateMiseEnService(entity.getDateMiseEnService());
        dto.setCreatedAt(entity.getCreatedAt());
        
        if (entity.getRefVehiculeType() != null) {
            dto.setRefVehiculeTypeId(entity.getRefVehiculeType().getId());
            dto.setRefVehiculeTypeLibelle(entity.getRefVehiculeType().getLibelle());
        }
        
        if (entity.getRefVehiculeEtat() != null) {
            dto.setRefVehiculeEtatId(entity.getRefVehiculeEtat().getId());
            dto.setRefVehiculeEtatLibelle(entity.getRefVehiculeEtat().getLibelle());
        }
        
        return dto;
    }

    public Vehicule toEntity(VehiculeDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Vehicule entity = new Vehicule();
        entity.setId(dto.getId());
        entity.setImmatriculation(dto.getImmatriculation());
        entity.setMarque(dto.getMarque());
        entity.setModele(dto.getModele());
        entity.setAnnee(dto.getAnnee());
        entity.setNombrePlaces(dto.getNombrePlaces());
        entity.setDateMiseEnService(dto.getDateMiseEnService());
        
        return entity;
    }
}
