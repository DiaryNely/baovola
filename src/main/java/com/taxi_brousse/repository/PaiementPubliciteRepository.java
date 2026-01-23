package com.taxi_brousse.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.PaiementPublicite;
import com.taxi_brousse.entity.reference.RefDevise;

@Repository
public interface PaiementPubliciteRepository extends JpaRepository<PaiementPublicite, Long> {

    @Query("SELECT p FROM PaiementPublicite p " +
           "LEFT JOIN FETCH p.societePublicitaire sp " +
           "LEFT JOIN FETCH p.refDevise " +
           "WHERE sp.id = :societeId " +
           "ORDER BY p.datePaiement DESC")
    List<PaiementPublicite> findBySocieteId(@Param("societeId") Long societeId);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PaiementPublicite p WHERE p.societePublicitaire.id = :societeId")
    BigDecimal sumMontantBySocieteId(@Param("societeId") Long societeId);

    @Query("SELECT p.refDevise FROM PaiementPublicite p " +
           "WHERE p.societePublicitaire.id = :societeId " +
           "ORDER BY p.datePaiement DESC")
    List<RefDevise> findDeviseBySocieteId(@Param("societeId") Long societeId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PaiementPublicite p " +
           "WHERE p.societePublicitaire.id = :societeId " +
           "AND p.factureMois = :mois AND p.factureAnnee = :annee")
    BigDecimal sumMontantBySocieteIdAndFacturePeriode(
            @Param("societeId") Long societeId,
            @Param("mois") Integer mois,
            @Param("annee") Integer annee);

    @Query("SELECT p FROM PaiementPublicite p " +
           "LEFT JOIN FETCH p.societePublicitaire sp " +
           "LEFT JOIN FETCH p.refDevise " +
           "WHERE sp.id = :societeId " +
           "AND p.factureMois = :mois AND p.factureAnnee = :annee " +
           "ORDER BY p.datePaiement DESC")
    List<PaiementPublicite> findBySocieteIdAndFacturePeriode(
            @Param("societeId") Long societeId,
            @Param("mois") Integer mois,
            @Param("annee") Integer annee);
}
