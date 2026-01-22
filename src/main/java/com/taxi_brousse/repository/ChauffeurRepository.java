package com.taxi_brousse.repository;

import com.taxi_brousse.entity.Chauffeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChauffeurRepository extends JpaRepository<Chauffeur, Long> {
}
