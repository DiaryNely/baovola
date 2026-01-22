package com.taxi_brousse.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefBilletStatut;

@Repository
public interface RefBilletStatutRepository extends JpaRepository<RefBilletStatut, Long> {
    
    Optional<RefBilletStatut> findTop1ByCodeOrderByIdAsc(String code);
}
