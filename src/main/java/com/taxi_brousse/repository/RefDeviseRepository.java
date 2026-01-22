package com.taxi_brousse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefDevise;

@Repository
public interface RefDeviseRepository extends JpaRepository<RefDevise, Long> {
    Optional<RefDevise> findByCode(String code);
    Optional<RefDevise> findTop1ByCodeOrderByIdAsc(String code);
    List<RefDevise> findByActifTrue();
}
