package com.taxi_brousse.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.VehiculeDTO;
import com.taxi_brousse.entity.ChauffeurVehicule;
import com.taxi_brousse.entity.CooperativeVehicule;
import com.taxi_brousse.entity.Vehicule;
import com.taxi_brousse.entity.reference.RefChauffeurVehiculeRole;
import com.taxi_brousse.entity.reference.RefVehiculeEtat;
import com.taxi_brousse.entity.reference.RefVehiculeType;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.VehiculeMapper;
import com.taxi_brousse.repository.ChauffeurVehiculeRepository;
import com.taxi_brousse.repository.CooperativeVehiculeRepository;
import com.taxi_brousse.repository.RefChauffeurVehiculeRoleRepository;
import com.taxi_brousse.repository.VehiculeRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final VehiculeMapper vehiculeMapper;
    private final VehiculeSiegeConfigService vehiculeSiegeConfigService;
    private final CooperativeVehiculeRepository cooperativeVehiculeRepository;
    private final ChauffeurVehiculeRepository chauffeurVehiculeRepository;
    private final RefChauffeurVehiculeRoleRepository refChauffeurVehiculeRoleRepository;

    public List<VehiculeDTO> findAll() {
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toDTO)
                .map(this::enrichWithConfigs)
                .map(this::enrichWithRelations)
                .collect(Collectors.toList());
    }

    public VehiculeDTO findById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", id));
        return enrichWithRelations(enrichWithConfigs(vehiculeMapper.toDTO(vehicule)));
    }

    public VehiculeDTO create(VehiculeDTO vehiculeDTO) {
        Vehicule vehicule = vehiculeMapper.toEntity(vehiculeDTO);
        
        if (vehiculeDTO.getRefVehiculeTypeId() != null) {
            RefVehiculeType type = new RefVehiculeType();
            type.setId(vehiculeDTO.getRefVehiculeTypeId());
            vehicule.setRefVehiculeType(type);
        }
        
        if (vehiculeDTO.getRefVehiculeEtatId() != null) {
            RefVehiculeEtat etat = new RefVehiculeEtat();
            etat.setId(vehiculeDTO.getRefVehiculeEtatId());
            vehicule.setRefVehiculeEtat(etat);
        }
        
        if (vehiculeDTO.getSiegeConfigs() != null && !vehiculeDTO.getSiegeConfigs().isEmpty()) {
                        int total = vehiculeDTO.getSiegeConfigs().stream()
                            .mapToInt(c -> Objects.requireNonNullElse(c.getNbPlaces(), 0))
                            .sum();
            vehicule.setNombrePlaces(total);
        }

        Vehicule savedVehicule = vehiculeRepository.save(vehicule);

        linkCooperative(savedVehicule.getId(), vehiculeDTO.getCooperativeId());
        linkChauffeur(savedVehicule.getId(), vehiculeDTO.getChauffeurId(), vehiculeDTO.getRefChauffeurVehiculeRoleId());

        if (vehiculeDTO.getSiegeConfigs() != null && !vehiculeDTO.getSiegeConfigs().isEmpty()) {
            vehiculeSiegeConfigService.saveConfigs(savedVehicule.getId(), vehiculeDTO.getSiegeConfigs());
        }

        return enrichWithRelations(enrichWithConfigs(vehiculeMapper.toDTO(savedVehicule)));
    }

    public VehiculeDTO update(Long id, VehiculeDTO vehiculeDTO) {
        Vehicule existingVehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", id));

        existingVehicule.setImmatriculation(vehiculeDTO.getImmatriculation());
        existingVehicule.setMarque(vehiculeDTO.getMarque());
        existingVehicule.setModele(vehiculeDTO.getModele());
        existingVehicule.setAnnee(vehiculeDTO.getAnnee());
        if (vehiculeDTO.getSiegeConfigs() != null && !vehiculeDTO.getSiegeConfigs().isEmpty()) {
                        int total = vehiculeDTO.getSiegeConfigs().stream()
                            .mapToInt(c -> Objects.requireNonNullElse(c.getNbPlaces(), 0))
                            .sum();
            existingVehicule.setNombrePlaces(total);
        } else {
            existingVehicule.setNombrePlaces(vehiculeDTO.getNombrePlaces());
        }
        existingVehicule.setDateMiseEnService(vehiculeDTO.getDateMiseEnService());
        
        if (vehiculeDTO.getRefVehiculeTypeId() != null) {
            RefVehiculeType type = new RefVehiculeType();
            type.setId(vehiculeDTO.getRefVehiculeTypeId());
            existingVehicule.setRefVehiculeType(type);
        }
        
        if (vehiculeDTO.getRefVehiculeEtatId() != null) {
            RefVehiculeEtat etat = new RefVehiculeEtat();
            etat.setId(vehiculeDTO.getRefVehiculeEtatId());
            existingVehicule.setRefVehiculeEtat(etat);
        }

        Vehicule updatedVehicule = vehiculeRepository.save(existingVehicule);

        linkCooperative(updatedVehicule.getId(), vehiculeDTO.getCooperativeId());
        linkChauffeur(updatedVehicule.getId(), vehiculeDTO.getChauffeurId(), vehiculeDTO.getRefChauffeurVehiculeRoleId());

        if (vehiculeDTO.getSiegeConfigs() != null && !vehiculeDTO.getSiegeConfigs().isEmpty()) {
            vehiculeSiegeConfigService.saveConfigs(updatedVehicule.getId(), vehiculeDTO.getSiegeConfigs());
        }

        return enrichWithRelations(enrichWithConfigs(vehiculeMapper.toDTO(updatedVehicule)));
    }

    public void delete(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", id));
        vehiculeRepository.delete(vehicule);
    }

    private VehiculeDTO enrichWithConfigs(VehiculeDTO dto) {
        if (dto == null || dto.getId() == null) {
            return dto;
        }
        dto.setSiegeConfigs(vehiculeSiegeConfigService.getConfigsByVehicule(dto.getId()));
        return dto;
    }

    private VehiculeDTO enrichWithRelations(VehiculeDTO dto) {
        if (dto == null || dto.getId() == null) {
            return dto;
        }

        cooperativeVehiculeRepository.findFirstByVehiculeIdAndDateFinIsNullOrderByDateDebutDesc(dto.getId())
            .ifPresent(link -> {
                dto.setCooperativeId(link.getCooperativeId());
                if (link.getCooperative() != null) {
                    dto.setCooperativeNom(link.getCooperative().getNom());
                }
            });

        chauffeurVehiculeRepository.findFirstByVehiculeIdAndDateFinIsNullOrderByDateDebutDesc(dto.getId())
            .ifPresent(link -> {
                dto.setChauffeurId(link.getChauffeurId());
                if (link.getChauffeur() != null) {
                    dto.setChauffeurNom(link.getChauffeur().getNom());
                    dto.setChauffeurPrenom(link.getChauffeur().getPrenom());
                }
                if (link.getRefChauffeurVehiculeRole() != null) {
                    dto.setRefChauffeurVehiculeRoleId(link.getRefChauffeurVehiculeRole().getId());
                    dto.setRefChauffeurVehiculeRoleCode(link.getRefChauffeurVehiculeRole().getCode());
                    dto.setRefChauffeurVehiculeRoleLibelle(link.getRefChauffeurVehiculeRole().getLibelle());
                }
            });

        return dto;
    }

    private void linkCooperative(Long vehiculeId, Long cooperativeId) {
        if (vehiculeId == null || cooperativeId == null) {
            return;
        }

        cooperativeVehiculeRepository.findByVehiculeIdAndDateFinIsNull(vehiculeId)
            .forEach(existing -> {
                existing.setDateFin(LocalDate.now());
                cooperativeVehiculeRepository.save(existing);
            });

        CooperativeVehicule link = new CooperativeVehicule();
        link.setVehiculeId(vehiculeId);
        link.setCooperativeId(cooperativeId);
        link.setDateDebut(LocalDate.now());
        cooperativeVehiculeRepository.save(link);
    }

    private void linkChauffeur(Long vehiculeId, Long chauffeurId, Long roleId) {
        if (vehiculeId == null || chauffeurId == null) {
            return;
        }

        chauffeurVehiculeRepository.findByVehiculeIdAndDateFinIsNull(vehiculeId)
            .forEach(existing -> {
                existing.setDateFin(LocalDateTime.now());
                chauffeurVehiculeRepository.save(existing);
            });

        ChauffeurVehicule link = new ChauffeurVehicule();
        link.setVehiculeId(vehiculeId);
        link.setChauffeurId(chauffeurId);
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
