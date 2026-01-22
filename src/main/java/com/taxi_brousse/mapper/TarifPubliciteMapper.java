package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.TarifPubliciteDTO;
import com.taxi_brousse.entity.TarifPublicite;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.repository.RefDeviseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class TarifPubliciteMapper {
    
    private final RefDeviseRepository refDeviseRepository;
    
    public TarifPubliciteDTO toDTO(TarifPublicite entity) {
        if (entity == null) {
            return null;
        }
        
        TarifPubliciteDTO dto = new TarifPubliciteDTO();
        dto.setId(entity.getId());
        dto.setMontant(entity.getMontant());
        dto.setDateDebut(entity.getDateDebut());
        dto.setDateFin(entity.getDateFin());
        dto.setDescription(entity.getDescription());
        dto.setActif(entity.getActif());
        dto.setCreatedAt(entity.getCreatedAt());
        
        // Devise
        if (entity.getRefDevise() != null) {
            dto.setRefDeviseId(entity.getRefDevise().getId());
            dto.setRefDeviseCode(entity.getRefDevise().getCode());
            dto.setRefDeviseLibelle(entity.getRefDevise().getLibelle());
            dto.setRefDeviseSymbole(entity.getRefDevise().getSymbole());
        }
        
        // Champs calculés
        dto.setEstActuel(entity.getDateFin() == null);
        
        LocalDate today = LocalDate.now();
        boolean enVigueur = entity.getActif() && 
                          !entity.getDateDebut().isAfter(today) &&
                          (entity.getDateFin() == null || !entity.getDateFin().isBefore(today));
        dto.setEstEnVigueur(enVigueur);
        
        if (entity.getDateFin() != null) {
            long jours = ChronoUnit.DAYS.between(entity.getDateDebut(), entity.getDateFin());
            dto.setNombreJoursValidite(jours);
        }
        
        return dto;
    }
    
    public TarifPublicite toEntity(TarifPubliciteDTO dto) {
        if (dto == null) {
            return null;
        }
        
        TarifPublicite entity = new TarifPublicite();
        entity.setId(dto.getId());
        entity.setMontant(dto.getMontant());
        entity.setDateDebut(dto.getDateDebut());
        entity.setDateFin(dto.getDateFin());
        entity.setDescription(dto.getDescription());
        entity.setActif(dto.getActif() != null ? dto.getActif() : true);
        
        // Devise
        if (dto.getRefDeviseId() != null) {
            RefDevise devise = refDeviseRepository.findById(dto.getRefDeviseId())
                .orElseThrow(() -> new ResourceNotFoundException("RefDevise", "id", dto.getRefDeviseId()));
            entity.setRefDevise(devise);
        }
        
        return entity;
    }
    
    public void updateEntity(TarifPubliciteDTO dto, TarifPublicite entity) {
        entity.setMontant(dto.getMontant());
        entity.setDateDebut(dto.getDateDebut());
        entity.setDateFin(dto.getDateFin());
        entity.setDescription(dto.getDescription());
        entity.setActif(dto.getActif() != null ? dto.getActif() : true);
        
        // Devise
        if (dto.getRefDeviseId() != null) {
            RefDevise devise = refDeviseRepository.findById(dto.getRefDeviseId())
                .orElseThrow(() -> new ResourceNotFoundException("RefDevise", "id", dto.getRefDeviseId()));
            entity.setRefDevise(devise);
        }
    }
}
