package com.taxi_brousse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefChauffeurVehiculeRole;

@Repository
public interface RefChauffeurVehiculeRoleRepository extends JpaRepository<RefChauffeurVehiculeRole, Long> {
    Optional<RefChauffeurVehiculeRole> findByCode(String code);
    List<RefChauffeurVehiculeRole> findByActifTrueOrderByLibelleAsc();
}
