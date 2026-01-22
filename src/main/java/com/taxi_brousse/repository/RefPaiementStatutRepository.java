package com.taxi_brousse.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefPaiementStatut;

@Repository
public interface RefPaiementStatutRepository extends JpaRepository<RefPaiementStatut, Long> {
    java.util.List<RefPaiementStatut> findByActifTrue();
    Optional<RefPaiementStatut> findByCode(String code);
    Optional<RefPaiementStatut> findTop1ByCodeOrderByIdAsc(String code);
}
