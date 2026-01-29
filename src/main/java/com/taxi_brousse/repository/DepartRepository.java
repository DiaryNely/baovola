package com.taxi_brousse.repository;

import java.math.BigDecimal;
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
    
    // ===== Requêtes pour Dashboard Financier =====
    
    /**
     * Récupère tous les départs dans une période donnée
     */
    @Query("SELECT d FROM Depart d WHERE " +
           "(CAST(:dateDebut AS timestamp) IS NULL OR d.dateHeureDepart >= :dateDebut) AND " +
           "(CAST(:dateFin AS timestamp) IS NULL OR d.dateHeureDepart <= :dateFin) " +
           "ORDER BY d.dateHeureDepart DESC")
    List<Depart> findByDateRange(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
    
    /**
     * Compte les départs par statut dans une période
     */
    @Query("SELECT COUNT(d) FROM Depart d WHERE " +
           "d.refDepartStatut.code = :statutCode AND " +
           "(CAST(:dateDebut AS timestamp) IS NULL OR d.dateHeureDepart >= :dateDebut) AND " +
           "(CAST(:dateFin AS timestamp) IS NULL OR d.dateHeureDepart <= :dateFin)")
    Long countByStatutAndDateRange(
            @Param("statutCode") String statutCode,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
    
    /**
     * Récupère les départs groupés par trajet avec statistiques
     */
    @Query("SELECT d.trajet.id as trajetId, " +
           "d.trajet.code as trajetCode, " +
           "d.lieuDepart.nom as lieuDepartNom, " +
           "d.lieuArrivee.nom as lieuArriveeNom, " +
           "COUNT(d) as nombreDeparts " +
           "FROM Depart d WHERE " +
           "(CAST(:dateDebut AS timestamp) IS NULL OR d.dateHeureDepart >= :dateDebut) AND " +
           "(CAST(:dateFin AS timestamp) IS NULL OR d.dateHeureDepart <= :dateFin) " +
           "GROUP BY d.trajet.id, d.trajet.code, d.lieuDepart.nom, d.lieuArrivee.nom " +
           "ORDER BY nombreDeparts DESC")
    List<Object[]> findDepartsGroupByTrajet(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
    
    /**
     * Récupère les départs avec statistiques mensuelles
     */
    @Query(value = "SELECT " +
           "EXTRACT(YEAR FROM d.date_heure_depart) as annee, " +
           "EXTRACT(MONTH FROM d.date_heure_depart) as mois, " +
           "COUNT(d.id) as nombre_departs " +
           "FROM depart d WHERE " +
           "(CAST(:dateDebut AS timestamp) IS NULL OR d.date_heure_depart >= CAST(:dateDebut AS timestamp)) AND " +
           "(CAST(:dateFin AS timestamp) IS NULL OR d.date_heure_depart <= CAST(:dateFin AS timestamp)) " +
           "GROUP BY annee, mois " +
           "ORDER BY annee DESC, mois DESC",
           nativeQuery = true)
    List<Object[]> findDepartsGroupByMonth(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin
    );
    
    /**
     * Récupère les départs disponibles (programmés et dans le futur)
     */
    @Query("SELECT d FROM Depart d WHERE " +
           "d.dateHeureDepart > :now AND " +
           "d.refDepartStatut.code = 'PROGRAMME' " +
           "ORDER BY d.dateHeureDepart ASC")
    List<Depart> findDepartsDisponibles(@Param("now") LocalDateTime now);
    
    /**
     * Récupère les départs pour un trajet après une date
     */
    @Query("SELECT d FROM Depart d WHERE " +
           "d.trajet.id = :trajetId AND " +
           "d.dateHeureDepart > :dateHeure " +
           "ORDER BY d.dateHeureDepart ASC")
    List<Depart> findByTrajetIdAndDateHeureAfter(
            @Param("trajetId") Long trajetId,
            @Param("dateHeure") LocalDateTime dateHeure
    );
    
    /**
     * Récupère les départs dans une plage de dates (non annulés, non terminés)
     */
    @Query("SELECT d FROM Depart d WHERE " +
           "d.dateHeureDepart >= :debut AND d.dateHeureDepart <= :fin AND " +
           "d.refDepartStatut.code NOT IN ('ANNULE', 'TERMINE') " +
           "ORDER BY d.dateHeureDepart ASC")
    List<Depart> findActiveByDateRange(
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );
}

