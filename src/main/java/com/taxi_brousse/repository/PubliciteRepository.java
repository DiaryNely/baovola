package com.taxi_brousse.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.Publicite;

@Repository
public interface PubliciteRepository extends JpaRepository<Publicite, Long> {
    
    /**
     * Récupère toutes les publicités actives
     */
    List<Publicite> findByActifTrue();
    
    /**
     * Récupère les publicités valides à une date donnée
     */
    @Query("SELECT p FROM Publicite p " +
           "WHERE p.actif = TRUE " +
           "AND p.dateDebutValidite <= :date " +
           "AND (p.dateFinValidite IS NULL OR p.dateFinValidite >= :date)")
    List<Publicite> findValidesADate(@Param("date") LocalDate date);
    
    /**
     * Récupère les publicités d'une société
     */
    @Query("SELECT p FROM Publicite p WHERE p.societePublicitaire.id = :societeId ORDER BY p.dateDebutValidite DESC")
    List<Publicite> findBySocietePublicitaireId(@Param("societeId") Long societeId);

    Optional<Publicite> findByCode(String code);
}
