package com.taxi_brousse.repository;

import com.taxi_brousse.entity.reference.RefDepartStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefDepartStatutRepository extends JpaRepository<RefDepartStatut, Long> {
    
    Optional<RefDepartStatut> findByCode(String code);
    
    List<RefDepartStatut> findByActifTrue();
}
