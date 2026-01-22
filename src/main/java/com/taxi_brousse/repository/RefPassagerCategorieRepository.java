package com.taxi_brousse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefPassagerCategorie;

@Repository
public interface RefPassagerCategorieRepository extends JpaRepository<RefPassagerCategorie, Long> {
    Optional<RefPassagerCategorie> findByCode(String code);
    List<RefPassagerCategorie> findByActifTrueOrderByOrdreAsc();
}
