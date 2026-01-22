package com.taxi_brousse.mapper;

import org.springframework.stereotype.Component;

import com.taxi_brousse.dto.DepartTarifSiegeDTO;
import com.taxi_brousse.entity.DepartTarifSiege;

@Component
public class DepartTarifSiegeMapper {

    public DepartTarifSiegeDTO toDTO(DepartTarifSiege entity) {
        if (entity == null) {
            return null;
        }

        DepartTarifSiegeDTO dto = new DepartTarifSiegeDTO();
        dto.setId(entity.getId());
        dto.setDepartId(entity.getDepart() != null ? entity.getDepart().getId() : null);
        if (entity.getRefSiegeCategorie() != null) {
            dto.setRefSiegeCategorieId(entity.getRefSiegeCategorie().getId());
            dto.setRefSiegeCategorieCode(entity.getRefSiegeCategorie().getCode());
            dto.setRefSiegeCategorieLibelle(entity.getRefSiegeCategorie().getLibelle());
            dto.setRefSiegeCategorieOrdre(entity.getRefSiegeCategorie().getOrdre());
        }
        if (entity.getRefDevise() != null) {
            dto.setRefDeviseId(entity.getRefDevise().getId());
            dto.setDeviseCode(entity.getRefDevise().getCode());
            dto.setDeviseSymbole(entity.getRefDevise().getSymbole());
        }
        dto.setMontant(entity.getMontant());
        return dto;
    }
}
