package com.taxi_brousse.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.Paiement;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    java.util.List<Paiement> findByReservationId(Long reservationId);
    
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p " +
           "WHERE p.reservation.depart.id = :departId " +
           "AND p.refPaiementStatut.code = 'VALIDE'")
    BigDecimal sumMontantByDepartId(@Param("departId") Long departId);
}
