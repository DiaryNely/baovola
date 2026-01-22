package com.taxi_brousse.service;

import com.taxi_brousse.dto.ChauffeurDTO;
import com.taxi_brousse.entity.Chauffeur;
import com.taxi_brousse.entity.ChauffeurVehicule;
import com.taxi_brousse.entity.reference.RefChauffeurVehiculeRole;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.ChauffeurMapper;
import com.taxi_brousse.repository.ChauffeurVehiculeRepository;
import com.taxi_brousse.repository.ChauffeurRepository;
import com.taxi_brousse.repository.RefChauffeurVehiculeRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChauffeurService {

    private final ChauffeurRepository chauffeurRepository;
    private final ChauffeurMapper chauffeurMapper;
    private final ChauffeurVehiculeRepository chauffeurVehiculeRepository;
    private final RefChauffeurVehiculeRoleRepository refChauffeurVehiculeRoleRepository;

    public List<ChauffeurDTO> findAll() {
        return chauffeurRepository.findAll().stream()
                .map(chauffeurMapper::toDTO)
                .map(this::enrichWithVehicule)
                .collect(Collectors.toList());
    }

    public ChauffeurDTO findById(Long id) {
        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur", "id", id));
        return enrichWithVehicule(chauffeurMapper.toDTO(chauffeur));
    }

    public ChauffeurDTO create(ChauffeurDTO chauffeurDTO) {
        Chauffeur chauffeur = chauffeurMapper.toEntity(chauffeurDTO);
        Chauffeur savedChauffeur = chauffeurRepository.save(chauffeur);
        linkVehicule(savedChauffeur.getId(), chauffeurDTO.getVehiculeId(), chauffeurDTO.getRefChauffeurVehiculeRoleId());
        return enrichWithVehicule(chauffeurMapper.toDTO(savedChauffeur));
    }

    public ChauffeurDTO update(Long id, ChauffeurDTO chauffeurDTO) {
        Chauffeur existingChauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur", "id", id));

        existingChauffeur.setNom(chauffeurDTO.getNom());
        existingChauffeur.setPrenom(chauffeurDTO.getPrenom());
        existingChauffeur.setTelephone(chauffeurDTO.getTelephone());
        existingChauffeur.setNumeroPermis(chauffeurDTO.getNumeroPermis());
        existingChauffeur.setDateNaissance(chauffeurDTO.getDateNaissance());
        existingChauffeur.setDateEmbauche(chauffeurDTO.getDateEmbauche());

        Chauffeur updatedChauffeur = chauffeurRepository.save(existingChauffeur);
        linkVehicule(updatedChauffeur.getId(), chauffeurDTO.getVehiculeId(), chauffeurDTO.getRefChauffeurVehiculeRoleId());
        return enrichWithVehicule(chauffeurMapper.toDTO(updatedChauffeur));
    }

    public void delete(Long id) {
        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chauffeur", "id", id));
        chauffeurRepository.delete(chauffeur);
    }

    private ChauffeurDTO enrichWithVehicule(ChauffeurDTO dto) {
        if (dto == null || dto.getId() == null) {
            return dto;
        }
        chauffeurVehiculeRepository.findFirstByChauffeurIdAndDateFinIsNullOrderByDateDebutDesc(dto.getId())
            .ifPresent(link -> {
                dto.setVehiculeId(link.getVehiculeId());
                if (link.getVehicule() != null) {
                    dto.setVehiculeImmatriculation(link.getVehicule().getImmatriculation());
                }
                if (link.getRefChauffeurVehiculeRole() != null) {
                    dto.setRefChauffeurVehiculeRoleId(link.getRefChauffeurVehiculeRole().getId());
                    dto.setRefChauffeurVehiculeRoleCode(link.getRefChauffeurVehiculeRole().getCode());
                    dto.setRefChauffeurVehiculeRoleLibelle(link.getRefChauffeurVehiculeRole().getLibelle());
                }
            });
        return dto;
    }

    private void linkVehicule(Long chauffeurId, Long vehiculeId, Long roleId) {
        if (chauffeurId == null || vehiculeId == null) {
            return;
        }

        chauffeurVehiculeRepository.findByChauffeurIdAndDateFinIsNull(chauffeurId)
            .forEach(existing -> {
                existing.setDateFin(LocalDateTime.now());
                chauffeurVehiculeRepository.save(existing);
            });

        ChauffeurVehicule link = new ChauffeurVehicule();
        link.setChauffeurId(chauffeurId);
        link.setVehiculeId(vehiculeId);
        link.setDateDebut(LocalDateTime.now());

        RefChauffeurVehiculeRole role = null;
        if (roleId != null) {
            role = refChauffeurVehiculeRoleRepository.findById(roleId).orElse(null);
        }
        if (role == null) {
            role = refChauffeurVehiculeRoleRepository.findByCode("PRINCIPAL").orElse(null);
        }

        if (role != null) {
            link.setRefChauffeurVehiculeRole(role);
            chauffeurVehiculeRepository.save(link);
        }
    }
}
