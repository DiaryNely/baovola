package com.taxi_brousse.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.VehiculeSiegeConfig;
import com.taxi_brousse.entity.reference.RefSiegeCategorie;
import com.taxi_brousse.repository.VehiculeSiegeConfigRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiegeConfigurationService {

    private final VehiculeSiegeConfigRepository vehiculeSiegeConfigRepository;

    @Transactional(readOnly = true)
    public List<VehiculeSiegeConfig> getConfigsForVehicule(Long vehiculeId) {
        return vehiculeSiegeConfigRepository.findByVehiculeIdOrderByRefSiegeCategorieOrdreAsc(vehiculeId);
    }

    @Transactional(readOnly = true)
    public int getTotalPlacesForVehicule(Long vehiculeId, Integer capaciteOverride) {
        int total = getConfigsForVehicule(vehiculeId).stream()
            .mapToInt(c -> {
                Integer value = c.getNbPlaces();
                return value == null ? 0 : value;
            })
            .sum();

        if (capaciteOverride != null && capaciteOverride > 0) {
            return Math.min(total, capaciteOverride);
        }
        return total;
    }

    @Transactional(readOnly = true)
    public Map<Integer, RefSiegeCategorie> buildSeatCategoryMap(Depart depart) {
        if (depart == null || depart.getVehicule() == null) {
            return Map.of();
        }

        List<VehiculeSiegeConfig> configs = new java.util.ArrayList<>(
                getConfigsForVehicule(depart.getVehicule().getId()));

        int total = getTotalPlacesForVehicule(depart.getVehicule().getId(), depart.getCapaciteOverride());
        Map<Integer, RefSiegeCategorie> map = new java.util.LinkedHashMap<>();

        int seatNumber = 1;
        for (VehiculeSiegeConfig config : configs) {
            if (config.getNbPlaces() == null || config.getNbPlaces() <= 0) {
                continue;
            }
            for (int i = 0; i < config.getNbPlaces() && seatNumber <= total; i++) {
                map.put(seatNumber, config.getRefSiegeCategorie());
                seatNumber++;
            }
            if (seatNumber > total) {
                break;
            }
        }

        return map;
    }
}
