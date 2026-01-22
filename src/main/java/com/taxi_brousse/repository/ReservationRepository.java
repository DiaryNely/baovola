package com.taxi_brousse.repository;

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
}
