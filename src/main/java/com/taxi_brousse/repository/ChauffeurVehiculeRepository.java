package com.taxi_brousse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.ChauffeurVehicule;
import com.taxi_brousse.entity.ChauffeurVehiculeId;

@Repository
public interface ChauffeurVehiculeRepository extends JpaRepository<ChauffeurVehicule, ChauffeurVehiculeId> {
    Optional<ChauffeurVehicule> findFirstByVehiculeIdAndDateFinIsNullOrderByDateDebutDesc(Long vehiculeId);
    List<ChauffeurVehicule> findByVehiculeIdAndDateFinIsNull(Long vehiculeId);
    Optional<ChauffeurVehicule> findFirstByChauffeurIdAndDateFinIsNullOrderByDateDebutDesc(Long chauffeurId);
    List<ChauffeurVehicule> findByChauffeurIdAndDateFinIsNull(Long chauffeurId);
}
