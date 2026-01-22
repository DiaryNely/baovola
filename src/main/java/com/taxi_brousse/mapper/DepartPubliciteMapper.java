package com.taxi_brousse.mapper;

import org.springframework.stereotype.Component;

import com.taxi_brousse.dto.DepartPubliciteDTO;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.DepartPublicite;
import com.taxi_brousse.entity.Publicite;
import com.taxi_brousse.entity.TarifPublicite;
import com.taxi_brousse.entity.reference.RefDevise;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DepartPubliciteMapper {
    
    /**
     * Convertit une entité en DTO avec informations supplémentaires
     */
    public DepartPubliciteDTO toDTO(DepartPublicite entity) {
        if (entity == null) {
            return null;
        }
        
        DepartPubliciteDTO dto = new DepartPubliciteDTO();
        dto.setId(entity.getId());
        dto.setDepartId(entity.getDepart().getId());
        dto.setPubliciteId(entity.getPublicite().getId());
        dto.setTarifPubliciteId(entity.getTarifPublicite().getId());
        dto.setDateDiffusion(entity.getDateDiffusion());
        dto.setMontantFacture(entity.getMontantFacture());
        dto.setRefDeviseId(entity.getRefDevise().getId());
        dto.setStatutDiffusion(entity.getStatutDiffusion());
        dto.setNombreRepetitions(entity.getNombreRepetitions());
        dto.setCreatedAt(entity.getCreatedAt());
        
        // Informations supplémentaires
        dto.setDepartCode(entity.getDepart().getCode());
        dto.setPubliciteTitre(entity.getPublicite().getTitre());
        dto.setSocietePublicitaireNom(entity.getPublicite().getSocietePublicitaire().getNom());
        dto.setDeviseCode(entity.getRefDevise().getCode());
        dto.setDeviseSymbole(entity.getRefDevise().getSymbole());
        dto.setDepartDateHeureDepart(entity.getDepart().getDateHeureDepart());
        
        // Description du trajet
        if (entity.getDepart().getTrajet() != null) {
            String trajet = entity.getDepart().getTrajet().getLieuDepart().getNom() + 
                          " → " + 
                          entity.getDepart().getTrajet().getLieuArrivee().getNom();
            dto.setTrajetDescription(trajet);
        }
        
        return dto;
    }
    
    /**
     * Convertit un DTO en entité (pour création)
     */
    public DepartPublicite toEntity(DepartPubliciteDTO dto, Depart depart, 
                                   Publicite publicite, TarifPublicite tarif, 
                                   RefDevise devise) {
        if (dto == null) {
            return null;
        }
        
        DepartPublicite entity = new DepartPublicite();
        entity.setDepart(depart);
        entity.setPublicite(publicite);
        entity.setTarifPublicite(tarif);
        entity.setRefDevise(devise);
        entity.setDateDiffusion(dto.getDateDiffusion());
        entity.setMontantFacture(dto.getMontantFacture());
        entity.setStatutDiffusion(dto.getStatutDiffusion());
        entity.setNombreRepetitions(dto.getNombreRepetitions());
        
        return entity;
    }
    
    /**
     * Met à jour une entité existante
     */
    public void updateEntity(DepartPubliciteDTO dto, DepartPublicite entity) {
        if (dto == null || entity == null) {
            return;
        }
        
        if (dto.getDateDiffusion() != null) {
            entity.setDateDiffusion(dto.getDateDiffusion());
        }
        if (dto.getMontantFacture() != null) {
            entity.setMontantFacture(dto.getMontantFacture());
        }
        if (dto.getStatutDiffusion() != null) {
            entity.setStatutDiffusion(dto.getStatutDiffusion());
        }
        if (dto.getNombreRepetitions() != null) {
            entity.setNombreRepetitions(dto.getNombreRepetitions());
        }
    }
}
