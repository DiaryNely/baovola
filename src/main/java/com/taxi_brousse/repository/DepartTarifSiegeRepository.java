package com.taxi_brousse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.DepartTarifSiege;

@Repository
public interface DepartTarifSiegeRepository extends JpaRepository<DepartTarifSiege, Long> {
    List<DepartTarifSiege> findByDepartId(Long departId);
    void deleteByDepartId(Long departId);
}
