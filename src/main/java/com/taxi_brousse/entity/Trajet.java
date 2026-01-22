package com.taxi_brousse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trajet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 80)
    private String code;

    @Column(name = "libelle", nullable = false, length = 200)
    private String libelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lieu_depart_id")
    private Lieu lieuDepart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lieu_arrivee_id")
    private Lieu lieuArrivee;

    @Column(name = "distance_km", precision = 10, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "duree_estimee_min")
    private Integer dureeEstimeeMin;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
