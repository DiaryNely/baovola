package com.taxi_brousse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.VehiculeSiegeConfig;

@Repository
public interface VehiculeSiegeConfigRepository extends JpaRepository<VehiculeSiegeConfig, Long> {
    List<VehiculeSiegeConfig> findByVehiculeIdOrderByRefSiegeCategorieOrdreAsc(Long vehiculeId);
    void deleteByVehiculeId(Long vehiculeId);
}
