package com.taxi_brousse.repository;

import com.taxi_brousse.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Optional<Produit> findByCode(String code);

    @Query("SELECT p FROM Produit p " +
           "LEFT JOIN FETCH p.refCategorieProduit " +
           "LEFT JOIN FETCH p.refDevise " +
           "WHERE p.id = :id")
    Optional<Produit> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT p FROM Produit p " +
           "LEFT JOIN FETCH p.refCategorieProduit " +
           "LEFT JOIN FETCH p.refDevise " +
           "WHERE p.actif = true " +
           "ORDER BY p.nom")
    List<Produit> findAllActive();

    @Query(value = "SELECT DISTINCT p.* FROM produit p " +
           "LEFT JOIN ref_categorie_produit cat ON cat.id = p.ref_categorie_produit_id " +
           "LEFT JOIN ref_devise d ON d.id = p.ref_devise_id " +
           "WHERE (:categorie IS NULL OR cat.id = :categorie) " +
           "AND (:actif IS NULL OR p.actif = :actif) " +
           "AND (:search IS NULL OR " +
           "p.nom ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') OR " +
           "p.code ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%')) " +
           "ORDER BY p.nom",
           countQuery = "SELECT COUNT(DISTINCT p.id) FROM produit p " +
           "LEFT JOIN ref_categorie_produit cat ON cat.id = p.ref_categorie_produit_id " +
           "WHERE (:categorie IS NULL OR cat.id = :categorie) " +
           "AND (:actif IS NULL OR p.actif = :actif) " +
           "AND (:search IS NULL OR " +
           "p.nom ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%') OR " +
           "p.code ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%'))",
           nativeQuery = true)
    Page<Produit> searchProduits(
            @Param("categorie") Long categorieId,
            @Param("actif") Boolean actif,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Produit p WHERE p.actif = true")
    Long countActive();

    @Query("SELECT p FROM Produit p " +
           "LEFT JOIN FETCH p.refCategorieProduit " +
           "LEFT JOIN FETCH p.refDevise " +
           "WHERE p.refCategorieProduit.id = :categorieId " +
           "AND p.actif = true " +
           "ORDER BY p.nom")
    List<Produit> findByCategorie(@Param("categorieId") Long categorieId);
}
