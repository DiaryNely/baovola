package com.taxi_brousse.repository;

import com.taxi_brousse.entity.ReservationPassager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationPassagerRepository extends JpaRepository<ReservationPassager, Long> {
    
    List<ReservationPassager> findByReservationId(Long reservationId);
    
    List<ReservationPassager> findByDepartId(Long departId);
    
    @Query("SELECT rp FROM ReservationPassager rp WHERE rp.depart.id = :departId AND rp.numeroSiege = :numeroSiege")
    List<ReservationPassager> findByDepartIdAndNumeroSiege(@Param("departId") Long departId, @Param("numeroSiege") Integer numeroSiege);
    
    boolean existsByDepartIdAndNumeroSiege(Long departId, Integer numeroSiege);
}
