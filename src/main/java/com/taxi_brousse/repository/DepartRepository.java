package com.taxi_brousse.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taxi_brousse.entity.Depart;

@Repository
public interface DepartRepository extends JpaRepository<Depart, Long> {

    List<Depart> findByVehiculeId(Long vehiculeId);
    
    @Query("SELECT d FROM Depart d WHERE " +
           "(COALESCE(:lieuDepartId, 0L) = 0 OR d.lieuDepart.id = :lieuDepartId) AND " +
           "(COALESCE(:lieuArriveeId, 0L) = 0 OR d.lieuArrivee.id = :lieuArriveeId) AND " +
           "(COALESCE(:cooperativeId, 0L) = 0 OR d.cooperative.id = :cooperativeId) AND " +
           "(COALESCE(:trajetId, 0L) = 0 OR d.trajet.id = :trajetId) AND " +
           "(COALESCE(:statutId, 0L) = 0 OR d.refDepartStatut.id = :statutId) AND " +
           "(CAST(:dateDebut AS timestamp) IS NULL OR d.dateHeureDepart >= :dateDebut) AND " +
           "(CAST(:dateFin AS timestamp) IS NULL OR d.dateHeureDepart <= :dateFin)")
    List<Depart> searchDeparts(
            @Param("lieuDepartId") Long lieuDepartId,
            @Param("lieuArriveeId") Long lieuArriveeId,
            @Param("cooperativeId") Long cooperativeId,
            @Param("trajetId") Long trajetId,
            @Param("statutId") Long statutId,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
}
