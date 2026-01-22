package com.taxi_brousse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.reference.RefLieuType;

@Repository
public interface RefLieuTypeRepository extends JpaRepository<RefLieuType, Long> {
    
    List<RefLieuType> findByActifTrue();
}
