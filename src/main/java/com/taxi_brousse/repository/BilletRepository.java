package com.taxi_brousse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.Billet;

@Repository
public interface BilletRepository extends JpaRepository<Billet, Long> {
    
    boolean existsByReservationIdAndNumeroSiege(Long reservationId, Integer numeroSiege);
    
    List<Billet> findByReservationId(Long reservationId);
    
    Optional<Billet> findByNumero(String numero);
}
