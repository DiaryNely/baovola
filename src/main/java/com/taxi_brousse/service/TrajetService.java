package com.taxi_brousse.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.TrajetDTO;
import com.taxi_brousse.entity.CooperativeTrajet;
import com.taxi_brousse.entity.Lieu;
import com.taxi_brousse.entity.Trajet;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.TrajetMapper;
import com.taxi_brousse.repository.CooperativeTrajetRepository;
import com.taxi_brousse.repository.LieuRepository;
import com.taxi_brousse.repository.TrajetRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TrajetService {

    private final TrajetRepository trajetRepository;
    private final LieuRepository lieuRepository;
    private final TrajetMapper trajetMapper;
    private final CooperativeTrajetRepository cooperativeTrajetRepository;

    public List<TrajetDTO> findAll() {
        return trajetRepository.findAll().stream()
                .map(trajetMapper::toDTO)
                .map(this::enrichWithCooperative)
                .collect(Collectors.toList());
    }

    public List<TrajetDTO> findActifs() {
        return trajetRepository.findAll().stream()
                .filter(Trajet::getActif)
                .map(trajetMapper::toDTO)
                .map(this::enrichWithCooperative)
                .collect(Collectors.toList());
    }

    public TrajetDTO findById(Long id) {
        Trajet trajet = trajetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", id));
        return enrichWithCooperative(trajetMapper.toDTO(trajet));
    }

    public TrajetDTO create(TrajetDTO trajetDTO) {
        Trajet trajet = trajetMapper.toEntity(trajetDTO);
        
        if (trajetDTO.getLieuDepartId() != null) {
            Lieu lieuDepart = lieuRepository.findById(trajetDTO.getLieuDepartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lieu", "id", trajetDTO.getLieuDepartId()));
            trajet.setLieuDepart(lieuDepart);
        }
        
        if (trajetDTO.getLieuArriveeId() != null) {
            Lieu lieuArrivee = lieuRepository.findById(trajetDTO.getLieuArriveeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lieu", "id", trajetDTO.getLieuArriveeId()));
            trajet.setLieuArrivee(lieuArrivee);
        }
        
        Trajet savedTrajet = trajetRepository.save(trajet);
        linkCooperative(savedTrajet.getId(), trajetDTO.getCooperativeId());
        return enrichWithCooperative(trajetMapper.toDTO(savedTrajet));
    }

    public TrajetDTO update(Long id, TrajetDTO trajetDTO) {
        Trajet existingTrajet = trajetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", id));

        existingTrajet.setCode(trajetDTO.getCode());
        existingTrajet.setLibelle(trajetDTO.getLibelle());
        existingTrajet.setActif(trajetDTO.getActif());
        existingTrajet.setDureeEstimeeMin(trajetDTO.getDureeEstimeeMin());
        
        if (trajetDTO.getDistanceKm() != null) {
            existingTrajet.setDistanceKm(java.math.BigDecimal.valueOf(trajetDTO.getDistanceKm()));
        }
        
        if (trajetDTO.getLieuDepartId() != null) {
            Lieu lieuDepart = lieuRepository.findById(trajetDTO.getLieuDepartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lieu", "id", trajetDTO.getLieuDepartId()));
            existingTrajet.setLieuDepart(lieuDepart);
        }
        
        if (trajetDTO.getLieuArriveeId() != null) {
            Lieu lieuArrivee = lieuRepository.findById(trajetDTO.getLieuArriveeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lieu", "id", trajetDTO.getLieuArriveeId()));
            existingTrajet.setLieuArrivee(lieuArrivee);
        }

        Trajet updatedTrajet = trajetRepository.save(existingTrajet);
        linkCooperative(updatedTrajet.getId(), trajetDTO.getCooperativeId());
        return enrichWithCooperative(trajetMapper.toDTO(updatedTrajet));
    }

    public void delete(Long id) {
        Trajet trajet = trajetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", id));
        trajetRepository.delete(trajet);
    }

    private TrajetDTO enrichWithCooperative(TrajetDTO dto) {
        if (dto == null || dto.getId() == null) {
            return dto;
        }
        cooperativeTrajetRepository.findFirstByTrajetIdAndActifTrueOrderByCooperativeIdAsc(dto.getId())
            .ifPresent(link -> {
                dto.setCooperativeId(link.getCooperativeId());
                if (link.getCooperative() != null) {
                    dto.setCooperativeNom(link.getCooperative().getNom());
                }
            });
        return dto;
    }

    private void linkCooperative(Long trajetId, Long cooperativeId) {
        if (trajetId == null || cooperativeId == null) {
            return;
        }
        cooperativeTrajetRepository.findByTrajetId(trajetId)
            .forEach(existing -> cooperativeTrajetRepository.delete(existing));

        CooperativeTrajet link = new CooperativeTrajet();
        link.setTrajetId(trajetId);
        link.setCooperativeId(cooperativeId);
        link.setActif(true);
        cooperativeTrajetRepository.save(link);
    }
}
