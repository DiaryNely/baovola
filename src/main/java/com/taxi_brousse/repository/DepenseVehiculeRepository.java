package com.taxi_brousse.repository;

import com.taxi_brousse.entity.DepenseVehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DepenseVehiculeRepository extends JpaRepository<DepenseVehicule, Long> {
    
    List<DepenseVehicule> findByVehiculeId(Long vehiculeId);
    
    List<DepenseVehicule> findByCooperativeId(Long cooperativeId);
    
    List<DepenseVehicule> findByRefTypeDepenseId(Long refTypeDepenseId);
    
    @Query("SELECT d FROM DepenseVehicule d WHERE d.dateDepense BETWEEN :dateDebut AND :dateFin")
    List<DepenseVehicule> findByDateDepenseBetween(@Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT d FROM DepenseVehicule d WHERE d.vehicule.id = :vehiculeId AND d.dateDepense BETWEEN :dateDebut AND :dateFin")
    List<DepenseVehicule> findByVehiculeIdAndDateDepenseBetween(@Param("vehiculeId") Long vehiculeId, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
    
    @Query("SELECT d FROM DepenseVehicule d WHERE d.cooperative.id = :cooperativeId AND d.dateDepense BETWEEN :dateDebut AND :dateFin")
    List<DepenseVehicule> findByCooperativeIdAndDateDepenseBetween(@Param("cooperativeId") Long cooperativeId, @Param("dateDebut") LocalDate dateDebut, @Param("dateFin") LocalDate dateFin);
}
