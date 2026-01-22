package com.taxi_brousse.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.Tarif;

@Repository
public interface TarifRepository extends JpaRepository<Tarif, Long> {
    Optional<Tarif> findTop1ByTrajetIdOrderByIdAsc(Long trajetId);

    @Query("SELECT t FROM Tarif t " +
           "WHERE t.cooperative.id = :cooperativeId " +
           "AND t.trajet.id = :trajetId " +
           "AND t.refVehiculeType.id = :vehiculeTypeId " +
           "AND t.dateDebut <= :dateReference " +
           "AND (t.dateFin IS NULL OR t.dateFin >= :dateReference) " +
           "ORDER BY t.dateDebut DESC")
    Optional<Tarif> findApplicableTarif(
            @Param("cooperativeId") Long cooperativeId,
            @Param("trajetId") Long trajetId,
            @Param("vehiculeTypeId") Long vehiculeTypeId,
            @Param("dateReference") LocalDate dateReference);
}
