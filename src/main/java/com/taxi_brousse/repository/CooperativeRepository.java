package com.taxi_brousse.repository;

import com.taxi_brousse.entity.Cooperative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CooperativeRepository extends JpaRepository<Cooperative, Long> {
}
