package com.taxi_brousse.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefSiegeCategorie;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "depart_tarif_siege", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"depart_id", "ref_siege_categorie_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartTarifSiege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depart_id", nullable = false)
    private Depart depart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_siege_categorie_id", nullable = false)
    private RefSiegeCategorie refSiegeCategorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_devise_id", nullable = false)
    private RefDevise refDevise;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
