package com.taxi_brousse.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    Optional<Reservation> findByCode(String code);
    
    List<Reservation> findByClientId(Long clientId);
    
    List<Reservation> findByDepartId(Long departId);
    
    @Query("SELECT r FROM Reservation r WHERE r.depart.id = :departId AND r.refReservationStatut.code = :statutCode")
    List<Reservation> findByDepartIdAndStatut(@Param("departId") Long departId, @Param("statutCode") String statutCode);
    
    @Query("SELECT COUNT(rp) FROM ReservationPassager rp WHERE rp.depart.id = :departId AND rp.reservation.refReservationStatut.code NOT IN ('ANNULE','ANNULEE')")
    Long countPassagersByDepartId(@Param("departId") Long departId);
    
    @Query("SELECT rp.numeroSiege FROM ReservationPassager rp WHERE rp.depart.id = :departId AND rp.reservation.refReservationStatut.code NOT IN ('ANNULE','ANNULEE') ORDER BY rp.numeroSiege")
    List<Integer> findOccupiedSeatsByDepartId(@Param("departId") Long departId);
    
    @Query("SELECT rp.numeroSiege FROM ReservationPassager rp WHERE rp.depart.id = :departId AND rp.reservation.id != :reservationId AND rp.reservation.refReservationStatut.code NOT IN ('ANNULE','ANNULEE') ORDER BY rp.numeroSiege")
    List<Integer> findOccupiedSeatsByDepartIdExcludingReservation(@Param("departId") Long departId, @Param("reservationId") Long reservationId);
    
    // ===== Requêtes pour Dashboard Financier =====
    
    /**
     * Somme des montants payés pour les réservations dans une période
     */
    @Query("SELECT COALESCE(SUM(r.montantPaye), 0) FROM Reservation r WHERE " +
           "r.refReservationStatut.code NOT IN ('ANNULE', 'ANNULEE') AND " +
           "(CAST(:dateDebut AS timestamp) IS NULL OR r.depart.dateHeureDepart >= :dateDebut) AND " +
           "(CAST(:dateFin AS timestamp) IS NULL OR r.depart.dateHeureDepart <= :dateFin)")
    BigDecimal sumMontantPayeByDateRange(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
    
    /**
     * Compte les réservations confirmées dans une période
     */
    @Query("SELECT COUNT(r) FROM Reservation r WHERE " +
           "r.refReservationStatut.code NOT IN ('ANNULE', 'ANNULEE') AND " +
           "(CAST(:dateDebut AS timestamp) IS NULL OR r.depart.dateHeureDepart >= :dateDebut) AND " +
           "(CAST(:dateFin AS timestamp) IS NULL OR r.depart.dateHeureDepart <= :dateFin)")
    Long countConfirmedReservationsByDateRange(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
    
    /**
     * Somme des montants payés par départ
     */
    @Query("SELECT r.depart.id, COALESCE(SUM(r.montantPaye), 0) FROM Reservation r WHERE " +
           "r.refReservationStatut.code NOT IN ('ANNULE', 'ANNULEE') AND " +
           "r.depart.id IN :departIds " +
           "GROUP BY r.depart.id")
    List<Object[]> sumMontantPayeGroupByDepart(@Param("departIds") List<Long> departIds);
    
    /**
     * Compte les réservations par départ
     */
    @Query("SELECT r.depart.id, COUNT(r) FROM Reservation r WHERE " +
           "r.refReservationStatut.code NOT IN ('ANNULE', 'ANNULEE') AND " +
           "r.depart.id IN :departIds " +
           "GROUP BY r.depart.id")
    List<Object[]> countReservationsGroupByDepart(@Param("departIds") List<Long> departIds);
}

