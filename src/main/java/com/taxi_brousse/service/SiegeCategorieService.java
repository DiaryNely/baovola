package com.taxi_brousse.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.SiegeCategorieDTO;
import com.taxi_brousse.entity.reference.RefSiegeCategorie;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.repository.RefSiegeCategorieRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiegeCategorieService {

    private final RefSiegeCategorieRepository refSiegeCategorieRepository;

    @Transactional(readOnly = true)
    public List<SiegeCategorieDTO> getAll() {
        return refSiegeCategorieRepository.findAllByOrderByOrdreAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SiegeCategorieDTO getById(Long id) {
        RefSiegeCategorie categorie = refSiegeCategorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RefSiegeCategorie", "id", id));
        return toDTO(categorie);
    }

    @Transactional
    public SiegeCategorieDTO create(SiegeCategorieDTO dto) {
        RefSiegeCategorie entity = new RefSiegeCategorie();
        entity.setCode(dto.getCode());
        entity.setLibelle(dto.getLibelle());
        entity.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : 1);
        entity.setActif(dto.getActif() != null ? dto.getActif() : Boolean.TRUE);
        return toDTO(refSiegeCategorieRepository.save(entity));
    }

    @Transactional
    public SiegeCategorieDTO update(Long id, SiegeCategorieDTO dto) {
        RefSiegeCategorie entity = refSiegeCategorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RefSiegeCategorie", "id", id));

        if (dto.getCode() != null) {
            entity.setCode(dto.getCode());
        }
        if (dto.getLibelle() != null) {
            entity.setLibelle(dto.getLibelle());
        }
        if (dto.getOrdre() != null) {
            entity.setOrdre(dto.getOrdre());
        }
        if (dto.getActif() != null) {
            entity.setActif(dto.getActif());
        }

        return toDTO(refSiegeCategorieRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!refSiegeCategorieRepository.existsById(id)) {
            throw new ResourceNotFoundException("RefSiegeCategorie", "id", id);
        }
        refSiegeCategorieRepository.deleteById(id);
    }

    private SiegeCategorieDTO toDTO(RefSiegeCategorie entity) {
        SiegeCategorieDTO dto = new SiegeCategorieDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setLibelle(entity.getLibelle());
        dto.setOrdre(entity.getOrdre());
        dto.setActif(entity.getActif());
        return dto;
    }
}
