package com.taxi_brousse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.CooperativeVehicule;
import com.taxi_brousse.entity.CooperativeVehiculeId;

@Repository
public interface CooperativeVehiculeRepository extends JpaRepository<CooperativeVehicule, CooperativeVehiculeId> {
    Optional<CooperativeVehicule> findFirstByVehiculeIdAndDateFinIsNullOrderByDateDebutDesc(Long vehiculeId);
    List<CooperativeVehicule> findByVehiculeIdAndDateFinIsNull(Long vehiculeId);
}
