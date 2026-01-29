package com.taxi_brousse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefCategorieProduit;

@Repository
public interface RefCategorieProduitRepository extends JpaRepository<RefCategorieProduit, Long> {

    Optional<RefCategorieProduit> findByCode(String code);

    @Query("SELECT r FROM RefCategorieProduit r WHERE r.actif = true ORDER BY r.libelle")
    List<RefCategorieProduit> findAllActive();

    List<RefCategorieProduit> findAllByOrderByLibelle();
}
