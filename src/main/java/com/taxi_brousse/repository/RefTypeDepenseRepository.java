package com.taxi_brousse.repository;

import java.util.Optional;

import java.util.List;


import com.taxi_brousse.entity.reference.RefTypeDepense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RefTypeDepenseRepository extends JpaRepository<RefTypeDepense, Long> {
    
    Optional<RefTypeDepense> findByCode(String code);
    
    List<RefTypeDepense> findByActifTrue();
}
