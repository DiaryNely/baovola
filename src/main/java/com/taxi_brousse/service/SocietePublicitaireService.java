package com.taxi_brousse.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.SocietePublicitaireDTO;
import com.taxi_brousse.entity.SocietePublicitaire;
import com.taxi_brousse.exception.BadRequestException;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.repository.PubliciteRepository;
import com.taxi_brousse.repository.SocietePublicitaireRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SocietePublicitaireService {

    private final SocietePublicitaireRepository societePublicitaireRepository;
    private final PubliciteRepository publiciteRepository;

    @Transactional(readOnly = true)
    public List<SocietePublicitaireDTO> findAll() {
        return societePublicitaireRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SocietePublicitaireDTO findById(Long id) {
        SocietePublicitaire societe = societePublicitaireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SocietePublicitaire", "id", id));
        return toDTO(societe);
    }

    public SocietePublicitaireDTO create(SocietePublicitaireDTO dto) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new BadRequestException("Le code est obligatoire");
        }
        if (dto.getNom() == null || dto.getNom().isBlank()) {
            throw new BadRequestException("Le nom est obligatoire");
        }
        if (societePublicitaireRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Le code existe déjà");
        }

        SocietePublicitaire entity = new SocietePublicitaire();
        updateEntity(entity, dto);
        entity = societePublicitaireRepository.save(entity);
        return toDTO(entity);
    }

    public SocietePublicitaireDTO update(Long id, SocietePublicitaireDTO dto) {
        SocietePublicitaire entity = societePublicitaireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SocietePublicitaire", "id", id));

        if (dto.getCode() != null && !dto.getCode().equals(entity.getCode())) {
            if (societePublicitaireRepository.existsByCode(dto.getCode())) {
                throw new BadRequestException("Le code existe déjà");
            }
            entity.setCode(dto.getCode());
        }

        updateEntity(entity, dto);
        entity = societePublicitaireRepository.save(entity);
        return toDTO(entity);
    }

    public void delete(Long id) {
        SocietePublicitaire entity = societePublicitaireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SocietePublicitaire", "id", id));

        if (!publiciteRepository.findBySocietePublicitaireId(id).isEmpty()) {
            throw new BadRequestException("Impossible de supprimer: la société possède des publicités");
        }
        societePublicitaireRepository.delete(entity);
    }

    private void updateEntity(SocietePublicitaire entity, SocietePublicitaireDTO dto) {
        if (dto.getCode() != null) {
            entity.setCode(dto.getCode());
        }
        if (dto.getNom() != null) {
            entity.setNom(dto.getNom());
        }
        entity.setTelephone(dto.getTelephone());
        entity.setEmail(dto.getEmail());
        entity.setAdresse(dto.getAdresse());
        entity.setContactNom(dto.getContactNom());
        entity.setContactTelephone(dto.getContactTelephone());
        if (dto.getActif() != null) {
            entity.setActif(dto.getActif());
        }
    }

    private SocietePublicitaireDTO toDTO(SocietePublicitaire entity) {
        SocietePublicitaireDTO dto = new SocietePublicitaireDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNom(entity.getNom());
        dto.setTelephone(entity.getTelephone());
        dto.setEmail(entity.getEmail());
        dto.setAdresse(entity.getAdresse());
        dto.setContactNom(entity.getContactNom());
        dto.setContactTelephone(entity.getContactTelephone());
        dto.setActif(entity.getActif());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
