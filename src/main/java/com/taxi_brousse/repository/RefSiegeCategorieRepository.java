package com.taxi_brousse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefSiegeCategorie;

@Repository
public interface RefSiegeCategorieRepository extends JpaRepository<RefSiegeCategorie, Long> {
    Optional<RefSiegeCategorie> findByCode(String code);
    List<RefSiegeCategorie> findByActifTrueOrderByOrdreAsc();
    List<RefSiegeCategorie> findAllByOrderByOrdreAsc();
}
