package com.taxi_brousse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.SocietePublicitaire;

@Repository
public interface SocietePublicitaireRepository extends JpaRepository<SocietePublicitaire, Long> {
    List<SocietePublicitaire> findByActifTrueOrderByNomAsc();

    boolean existsByCode(String code);
}
