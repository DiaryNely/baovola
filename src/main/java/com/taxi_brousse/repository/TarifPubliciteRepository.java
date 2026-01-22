package com.taxi_brousse.repository;

import com.taxi_brousse.entity.TarifPublicite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarifPubliciteRepository extends JpaRepository<TarifPublicite, Long> {
    
    /**
     * Trouve tous les tarifs actifs
     */
    List<TarifPublicite> findByActifTrue();
    
    /**
     * Trouve le tarif actuel (actif et date_fin = NULL)
     */
    @Query("SELECT t FROM TarifPublicite t WHERE t.actif = true AND t.dateFin IS NULL")
    Optional<TarifPublicite> findTarifActuel();
    
    /**
     * Trouve le tarif en vigueur à une date donnée
     */
    @Query("SELECT t FROM TarifPublicite t WHERE t.actif = true " +
           "AND t.dateDebut <= :date " +
           "AND (t.dateFin IS NULL OR t.dateFin >= :date) " +
           "ORDER BY t.dateDebut DESC")
    Optional<TarifPublicite> findTarifEnVigueur(@Param("date") LocalDate date);
    
    /**
     * Trouve tous les tarifs dans une période
     */
    @Query("SELECT t FROM TarifPublicite t WHERE " +
           "t.dateDebut <= :dateFin AND (t.dateFin IS NULL OR t.dateFin >= :dateDebut)")
    List<TarifPublicite> findTarifsDansPeriode(
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );
    
    /**
     * Vérifie s'il existe déjà un tarif actif (sans date de fin)
     */
    @Query("SELECT COUNT(t) > 0 FROM TarifPublicite t WHERE t.actif = true AND t.dateFin IS NULL AND t.id != :excludeId")
    boolean existsTarifActifAutre(@Param("excludeId") Long excludeId);
    
    /**
     * Trouve tous les tarifs triés par date de début décroissant
     */
    List<TarifPublicite> findAllByOrderByDateDebutDesc();
}
