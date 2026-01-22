package com.taxi_brousse.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefPaiementMethode;

@Repository
public interface RefPaiementMethodeRepository extends JpaRepository<RefPaiementMethode, Long> {
    java.util.List<RefPaiementMethode> findByActifTrue();
    Optional<RefPaiementMethode> findByCode(String code);
    Optional<RefPaiementMethode> findTop1ByCodeOrderByIdAsc(String code);
}
