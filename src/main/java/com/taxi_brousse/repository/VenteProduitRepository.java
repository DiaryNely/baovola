package com.taxi_brousse.repository;

import com.taxi_brousse.entity.VenteProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VenteProduitRepository extends JpaRepository<VenteProduit, Long> {

    @Query("SELECT v FROM VenteProduit v " +
           "LEFT JOIN FETCH v.stockDepart s " +
           "LEFT JOIN FETCH s.produit p " +
           "LEFT JOIN FETCH v.client " +
           "WHERE s.depart.id = :departId " +
           "ORDER BY v.dateVente DESC")
    List<VenteProduit> findByDepartId(@Param("departId") Long departId);

    @Query("SELECT v FROM VenteProduit v " +
           "WHERE v.stockDepart.id = :stockDepartId " +
           "ORDER BY v.dateVente DESC")
    List<VenteProduit> findByStockDepartId(@Param("stockDepartId") Long stockDepartId);

    @Query("SELECT COALESCE(SUM(v.montantTotal), 0) " +
           "FROM VenteProduit v " +
           "WHERE v.stockDepart.depart.id = :departId")
    BigDecimal calculateTotalByDepartId(@Param("departId") Long departId);

    @Query("SELECT v FROM VenteProduit v " +
           "WHERE v.dateVente BETWEEN :startDate AND :endDate " +
           "ORDER BY v.dateVente DESC")
    List<VenteProduit> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(v) FROM VenteProduit v " +
           "WHERE v.stockDepart.depart.id = :departId")
    Long countByDepartId(@Param("departId") Long departId);
}
