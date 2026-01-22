package com.taxi_brousse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.DepartTarifRemise;

@Repository
public interface DepartTarifRemiseRepository extends JpaRepository<DepartTarifRemise, Long> {
    List<DepartTarifRemise> findByDepartId(Long departId);

    boolean existsByDepartIdAndRefSiegeCategorieIdAndRefPassagerCategorieId(Long departId, Long refSiegeCategorieId, Long refPassagerCategorieId);

    boolean existsByDepartIdAndRefSiegeCategorieIdAndRefPassagerCategorieIdAndIdNot(Long departId, Long refSiegeCategorieId, Long refPassagerCategorieId, Long id);
}
