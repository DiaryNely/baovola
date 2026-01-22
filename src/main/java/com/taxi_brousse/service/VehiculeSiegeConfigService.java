package com.taxi_brousse.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.VehiculeSiegeConfigDTO;
import com.taxi_brousse.entity.Vehicule;
import com.taxi_brousse.entity.VehiculeSiegeConfig;
import com.taxi_brousse.entity.reference.RefSiegeCategorie;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.VehiculeSiegeConfigMapper;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.RefSiegeCategorieRepository;
import com.taxi_brousse.repository.VehiculeRepository;
import com.taxi_brousse.repository.VehiculeSiegeConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehiculeSiegeConfigService {

    private final VehiculeSiegeConfigRepository configRepository;
    private final VehiculeRepository vehiculeRepository;
    private final RefSiegeCategorieRepository refSiegeCategorieRepository;
    private final VehiculeSiegeConfigMapper configMapper;
    private final DepartRepository departRepository;
    private final ReservationService reservationService;

    @Transactional(readOnly = true)
    public List<VehiculeSiegeConfigDTO> getConfigsByVehicule(Long vehiculeId) {
        return configRepository.findByVehiculeIdOrderByRefSiegeCategorieOrdreAsc(vehiculeId).stream()
                .map(configMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<VehiculeSiegeConfigDTO> saveConfigs(Long vehiculeId, List<VehiculeSiegeConfigDTO> configs) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", vehiculeId));

        configRepository.deleteByVehiculeId(vehiculeId);

        List<VehiculeSiegeConfig> entities = configs.stream().map(dto -> {
            VehiculeSiegeConfig entity = new VehiculeSiegeConfig();
            entity.setVehicule(vehicule);

            RefSiegeCategorie categorie = refSiegeCategorieRepository.findById(dto.getRefSiegeCategorieId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefSiegeCategorie", "id", dto.getRefSiegeCategorieId()));
            entity.setRefSiegeCategorie(categorie);
            entity.setNbPlaces(dto.getNbPlaces());
            return entity;
        }).collect(Collectors.toList());

        List<VehiculeSiegeConfig> saved = configRepository.saveAll(entities);
        departRepository.findByVehiculeId(vehiculeId)
            .forEach(depart -> reservationService.recalculateConfirmedReservationsForDepart(depart.getId()));
        return saved.stream().map(configMapper::toDTO).collect(Collectors.toList());
    }
}
