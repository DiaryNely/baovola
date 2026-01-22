package com.taxi_brousse.mapper;

import org.springframework.stereotype.Component;

import com.taxi_brousse.dto.VehiculeSiegeConfigDTO;
import com.taxi_brousse.entity.VehiculeSiegeConfig;

@Component
public class VehiculeSiegeConfigMapper {

    public VehiculeSiegeConfigDTO toDTO(VehiculeSiegeConfig entity) {
        if (entity == null) {
            return null;
        }

        VehiculeSiegeConfigDTO dto = new VehiculeSiegeConfigDTO();
        dto.setId(entity.getId());
        dto.setVehiculeId(entity.getVehicule() != null ? entity.getVehicule().getId() : null);
        dto.setRefSiegeCategorieId(entity.getRefSiegeCategorie() != null ? entity.getRefSiegeCategorie().getId() : null);
        dto.setRefSiegeCategorieCode(entity.getRefSiegeCategorie() != null ? entity.getRefSiegeCategorie().getCode() : null);
        dto.setRefSiegeCategorieLibelle(entity.getRefSiegeCategorie() != null ? entity.getRefSiegeCategorie().getLibelle() : null);
        dto.setRefSiegeCategorieOrdre(entity.getRefSiegeCategorie() != null ? entity.getRefSiegeCategorie().getOrdre() : null);
        dto.setNbPlaces(entity.getNbPlaces());
        return dto;
    }
}
