package com.taxi_brousse.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.dto.PubliciteCaStatRowDTO;
import com.taxi_brousse.entity.DepartPublicite;
import com.taxi_brousse.entity.reference.RefDevise;

@Repository
public interface DepartPubliciteRepository extends JpaRepository<DepartPublicite, Long> {
    
    /**
     * Récupère toutes les diffusions pour un départ donné
     */
    @Query("SELECT dp FROM DepartPublicite dp " +
           "LEFT JOIN FETCH dp.publicite p " +
           "LEFT JOIN FETCH p.societePublicitaire " +
           "LEFT JOIN FETCH dp.tarifPublicite " +
           "LEFT JOIN FETCH dp.refDevise " +
           "WHERE dp.depart.id = :departId " +
           "ORDER BY dp.dateDiffusion DESC")
    List<DepartPublicite> findByDepartId(@Param("departId") Long departId);
    
    /**
     * Récupère toutes les diffusions d'une publicité
     */
    @Query("SELECT dp FROM DepartPublicite dp " +
           "LEFT JOIN FETCH dp.depart d " +
           "LEFT JOIN FETCH d.trajet t " +
           "LEFT JOIN FETCH t.lieuDepart " +
           "LEFT JOIN FETCH t.lieuArrivee " +
           "WHERE dp.publicite.id = :publiciteId " +
           "ORDER BY dp.dateDiffusion DESC")
    List<DepartPublicite> findByPubliciteId(@Param("publiciteId") Long publiciteId);
    
    /**
     * Compte le nombre de diffusions pour un départ
     */
    @Query("SELECT COUNT(dp) FROM DepartPublicite dp WHERE dp.depart.id = :departId")
    Long countByDepartId(@Param("departId") Long departId);
    
    /**
     * Compte le nombre de diffusions pour une publicité
     */
    @Query("SELECT COUNT(dp) FROM DepartPublicite dp WHERE dp.publicite.id = :publiciteId")
    Long countByPubliciteId(@Param("publiciteId") Long publiciteId);
    
    /**
     * Récupère les diffusions par statut
     */
    @Query("SELECT dp FROM DepartPublicite dp " +
           "LEFT JOIN FETCH dp.depart " +
           "LEFT JOIN FETCH dp.publicite " +
           "WHERE dp.statutDiffusion = :statut " +
           "ORDER BY dp.dateDiffusion DESC")
    List<DepartPublicite> findByStatut(@Param("statut") String statut);
    
    /**
     * Récupère les diffusions sur une période
     */
    @Query("SELECT dp FROM DepartPublicite dp " +
           "LEFT JOIN FETCH dp.depart " +
           "LEFT JOIN FETCH dp.publicite " +
           "WHERE dp.dateDiffusion BETWEEN :dateDebut AND :dateFin " +
           "ORDER BY dp.dateDiffusion DESC")
    List<DepartPublicite> findByPeriode(
        @Param("dateDebut") LocalDateTime dateDebut,
        @Param("dateFin") LocalDateTime dateFin
    );
    
    /**
     * Vérifie si une publicité est déjà associée à un départ
     */
    @Query("SELECT CASE WHEN COUNT(dp) > 0 THEN TRUE ELSE FALSE END " +
           "FROM DepartPublicite dp " +
           "WHERE dp.depart.id = :departId AND dp.publicite.id = :publiciteId")
    boolean existsByDepartIdAndPubliciteId(
        @Param("departId") Long departId, 
        @Param("publiciteId") Long publiciteId
    );

       /**
        * Statistiques CA par publicité sur une période
        */
       @Query("SELECT new com.taxi_brousse.dto.PubliciteCaStatRowDTO(" +
                 "p.id, p.titre, sp.nom, " +
                 "SUM(COALESCE(dp.nombreRepetitions, 1)), " +
                 "CASE WHEN SUM(COALESCE(dp.nombreRepetitions, 1)) = 0 THEN 0 " +
                 "ELSE SUM(dp.montantFacture) / SUM(COALESCE(dp.nombreRepetitions, 1)) END, " +
                 "SUM(dp.montantFacture), " +
                 "devise.code, devise.symbole) " +
                 "FROM DepartPublicite dp " +
                 "JOIN dp.publicite p " +
                 "JOIN p.societePublicitaire sp " +
                 "JOIN dp.refDevise devise " +
                 "WHERE dp.dateDiffusion BETWEEN :dateDebut AND :dateFin " +
                 "GROUP BY p.id, p.titre, sp.nom, devise.code, devise.symbole " +
                 "ORDER BY SUM(dp.montantFacture) DESC")
       List<PubliciteCaStatRowDTO> findCaParPublicite(
              @Param("dateDebut") LocalDateTime dateDebut,
              @Param("dateFin") LocalDateTime dateFin
       );

       @Query("SELECT new com.taxi_brousse.dto.PubliciteCaStatRowDTO(" +
                 "p.id, p.titre, sp.nom, " +
                 "SUM(COALESCE(dp.nombreRepetitions, 1)), " +
                 "CASE WHEN SUM(COALESCE(dp.nombreRepetitions, 1)) = 0 THEN 0 " +
                 "ELSE SUM(dp.montantFacture) / SUM(COALESCE(dp.nombreRepetitions, 1)) END, " +
                 "SUM(dp.montantFacture), " +
                 "devise.code, devise.symbole) " +
                 "FROM DepartPublicite dp " +
                 "JOIN dp.publicite p " +
                 "JOIN p.societePublicitaire sp " +
                 "JOIN dp.refDevise devise " +
                 "WHERE dp.dateDiffusion BETWEEN :dateDebut AND :dateFin " +
                 "AND sp.id = :societeId " +
                 "GROUP BY p.id, p.titre, sp.nom, devise.code, devise.symbole " +
                 "ORDER BY SUM(dp.montantFacture) DESC")
       List<PubliciteCaStatRowDTO> findCaParPubliciteBySociete(
              @Param("dateDebut") LocalDateTime dateDebut,
              @Param("dateFin") LocalDateTime dateFin,
              @Param("societeId") Long societeId
       );

          /**
           * Montant total facturé pour un départ (diffusions non annulées)
           */
          @Query("SELECT COALESCE(SUM(dp.montantFacture), 0) " +
                 "FROM DepartPublicite dp " +
                 "WHERE dp.depart.id = :departId " +
                 "AND dp.statutDiffusion <> 'ANNULE'")
          java.math.BigDecimal sumMontantFactureByDepartId(@Param("departId") Long departId);

          /**
           * Dernière devise utilisée pour un départ (via diffusions)
           */
          @Query("SELECT dp.refDevise FROM DepartPublicite dp " +
                 "WHERE dp.depart.id = :departId " +
                 "ORDER BY dp.dateDiffusion DESC")
          List<RefDevise> findDeviseByDepartId(@Param("departId") Long departId, Pageable pageable);

           /**
            * Montant total facturé pour une société publicitaire
            */
           @Query("SELECT COALESCE(SUM(dp.montantFacture), 0) " +
                  "FROM DepartPublicite dp " +
                  "WHERE dp.publicite.societePublicitaire.id = :societeId " +
                  "AND dp.statutDiffusion <> 'ANNULE'")
           java.math.BigDecimal sumMontantFactureBySocieteId(@Param("societeId") Long societeId);

           /**
            * Dernière devise utilisée pour une société publicitaire (via diffusions)
            */
           @Query("SELECT dp.refDevise FROM DepartPublicite dp " +
                  "WHERE dp.publicite.societePublicitaire.id = :societeId " +
                  "ORDER BY dp.dateDiffusion DESC")
           List<RefDevise> findDeviseBySocieteId(@Param("societeId") Long societeId, Pageable pageable);

          @Query("SELECT dp FROM DepartPublicite dp " +
                "LEFT JOIN FETCH dp.publicite p " +
                "LEFT JOIN FETCH p.societePublicitaire sp " +
                "LEFT JOIN FETCH dp.depart d " +
                "LEFT JOIN FETCH d.trajet t " +
                "LEFT JOIN FETCH t.lieuDepart " +
                "LEFT JOIN FETCH t.lieuArrivee " +
                "LEFT JOIN FETCH dp.refDevise " +
                "WHERE sp.id = :societeId " +
                "AND dp.dateDiffusion BETWEEN :dateDebut AND :dateFin " +
                "ORDER BY dp.dateDiffusion DESC")
          List<DepartPublicite> findBySocieteIdAndDateDiffusionBetween(
                 @Param("societeId") Long societeId,
                 @Param("dateDebut") LocalDateTime dateDebut,
                 @Param("dateFin") LocalDateTime dateFin);

          @Query("SELECT COALESCE(SUM(dp.montantFacture), 0) " +
                "FROM DepartPublicite dp " +
                "WHERE dp.publicite.societePublicitaire.id = :societeId " +
                "AND dp.dateDiffusion BETWEEN :dateDebut AND :dateFin " +
                "AND dp.statutDiffusion <> 'ANNULE'")
          java.math.BigDecimal sumMontantFactureBySocieteIdAndPeriode(
                 @Param("societeId") Long societeId,
                 @Param("dateDebut") LocalDateTime dateDebut,
                 @Param("dateFin") LocalDateTime dateFin);

           /**
            * Recalcule le montant_facture pour toutes les diffusions liées à un tarif
            * montant_facture = tarifUnitaire × nombreRepetitions
            */
           @Modifying
           @Query("UPDATE DepartPublicite dp " +
                  "SET dp.montantFacture = :montantUnitaire * COALESCE(dp.nombreRepetitions, 1) " +
                  "WHERE dp.tarifPublicite.id = :tarifId")
           int recalculerMontantsParTarif(
                   @Param("tarifId") Long tarifId,
                   @Param("montantUnitaire") java.math.BigDecimal montantUnitaire
           );
}
