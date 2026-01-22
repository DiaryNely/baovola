package com.taxi_brousse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefVehiculeType;

@Repository
public interface RefVehiculeTypeRepository extends JpaRepository<RefVehiculeType, Long> {
    
    List<RefVehiculeType> findByActifTrue();
}
