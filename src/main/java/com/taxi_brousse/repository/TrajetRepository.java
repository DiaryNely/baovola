package com.taxi_brousse.repository;

import com.taxi_brousse.entity.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrajetRepository extends JpaRepository<Trajet, Long> {
}
