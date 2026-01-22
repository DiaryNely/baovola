package com.taxi_brousse.repository;

import com.taxi_brousse.entity.Lieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LieuRepository extends JpaRepository<Lieu, Long> {
}
