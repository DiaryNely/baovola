package com.taxi_brousse.entity;

import java.time.LocalDateTime;

import com.taxi_brousse.entity.reference.RefDepartStatut;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "depart")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Depart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 80)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private Cooperative cooperative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lieu_depart_id")
    private Lieu lieuDepart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lieu_arrivee_id")
    private Lieu lieuArrivee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_depart_statut_id", nullable = false)
    private RefDepartStatut refDepartStatut;

    @Column(name = "date_heure_depart", nullable = false)
    private LocalDateTime dateHeureDepart;

    @Column(name = "date_heure_arrivee_estimee")
    private LocalDateTime dateHeureArriveeEstimee;

    @Column(name = "capacite_override")
    private Integer capaciteOverride;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public LocalDateTime getDateHeureDepart() {
        return dateHeureDepart;
    }
}
