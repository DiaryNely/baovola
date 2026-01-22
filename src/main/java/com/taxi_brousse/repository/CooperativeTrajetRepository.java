package com.taxi_brousse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.CooperativeTrajet;
import com.taxi_brousse.entity.CooperativeTrajetId;

@Repository
public interface CooperativeTrajetRepository extends JpaRepository<CooperativeTrajet, CooperativeTrajetId> {
    Optional<CooperativeTrajet> findFirstByTrajetIdAndActifTrueOrderByCooperativeIdAsc(Long trajetId);
    List<CooperativeTrajet> findByTrajetId(Long trajetId);
}
