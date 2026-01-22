package com.taxi_brousse.entity;

import java.time.LocalDateTime;

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
@Table(name = "vehicule_siege_config", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"vehicule_id", "ref_siege_categorie_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeSiegeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_siege_categorie_id", nullable = false)
    private RefSiegeCategorie refSiegeCategorie;

    @Column(name = "nb_places", nullable = false)
    private Integer nbPlaces;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
