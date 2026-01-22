package com.taxi_brousse.entity;

import com.taxi_brousse.entity.reference.RefVehiculeEtat;
import com.taxi_brousse.entity.reference.RefVehiculeType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "immatriculation", nullable = false, unique = true, length = 30)
    private String immatriculation;

    @Column(name = "marque", length = 80)
    private String marque;

    @Column(name = "modele", length = 80)
    private String modele;

    @Column(name = "annee")
    private Integer annee;

    @Column(name = "nombre_places", nullable = false)
    private Integer nombrePlaces;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_vehicule_type_id")
    private RefVehiculeType refVehiculeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_vehicule_etat_id")
    private RefVehiculeEtat refVehiculeEtat;

    @Column(name = "date_mise_en_service")
    private LocalDate dateMiseEnService;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
