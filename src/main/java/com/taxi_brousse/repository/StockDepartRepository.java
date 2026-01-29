package com.taxi_brousse.repository;

import com.taxi_brousse.entity.StockDepart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockDepartRepository extends JpaRepository<StockDepart, Long> {

    @Query("SELECT s FROM StockDepart s " +
           "LEFT JOIN FETCH s.depart " +
           "LEFT JOIN FETCH s.produit p " +
           "LEFT JOIN FETCH p.refCategorieProduit " +
           "LEFT JOIN FETCH s.refDevise " +
           "WHERE s.depart.id = :departId " +
           "ORDER BY p.nom")
    List<StockDepart> findByDepartId(@Param("departId") Long departId);

    @Query("SELECT s FROM StockDepart s " +
           "LEFT JOIN FETCH s.depart " +
           "LEFT JOIN FETCH s.produit " +
           "LEFT JOIN FETCH s.refDevise " +
           "WHERE s.id = :id")
    Optional<StockDepart> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT s FROM StockDepart s " +
           "WHERE s.depart.id = :departId " +
           "AND s.produit.id = :produitId")
    Optional<StockDepart> findByDepartIdAndProduitId(
            @Param("departId") Long departId,
            @Param("produitId") Long produitId
    );

    @Query("SELECT s FROM StockDepart s " +
           "LEFT JOIN FETCH s.produit p " +
           "WHERE s.depart.id = :departId " +
           "AND s.quantiteDisponible > 0 " +
           "ORDER BY p.nom")
    List<StockDepart> findAvailableByDepartId(@Param("departId") Long departId);

    @Query("SELECT COALESCE(SUM(s.quantiteDisponible * s.prixUnitaire), 0) " +
           "FROM StockDepart s " +
           "WHERE s.depart.id = :departId")
    BigDecimal calculateStockValue(@Param("departId") Long departId);
    
    // Pour les statistiques CA - valeur totale du stock dans une période (basé sur date départ)
    @Query("SELECT COALESCE(SUM(s.quantiteInitiale * s.prixUnitaire), 0) " +
           "FROM StockDepart s " +
           "WHERE s.depart.dateHeureDepart BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalStockValueByDateRange(@Param("startDate") java.time.LocalDateTime startDate, 
                                                    @Param("endDate") java.time.LocalDateTime endDate);
}
