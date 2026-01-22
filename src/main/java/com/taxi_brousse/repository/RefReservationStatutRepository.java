package com.taxi_brousse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefReservationStatut;

@Repository
public interface RefReservationStatutRepository extends JpaRepository<RefReservationStatut, Long> {
    
    Optional<RefReservationStatut> findByCode(String code);
    
    Optional<RefReservationStatut> findTop1ByCodeOrderByIdAsc(String code);
    
    List<RefReservationStatut> findByActifTrue();
}
