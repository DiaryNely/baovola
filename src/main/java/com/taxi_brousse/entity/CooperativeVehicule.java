package com.taxi_brousse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cooperative_vehicule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CooperativeVehiculeId.class)
public class CooperativeVehicule {

    @Id
    @Column(name = "cooperative_id")
    private Long cooperativeId;

    @Id
    @Column(name = "vehicule_id")
    private Long vehiculeId;

    @Id
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id", insertable = false, updatable = false)
    private Cooperative cooperative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", insertable = false, updatable = false)
    private Vehicule vehicule;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dateDebut == null) {
            dateDebut = LocalDate.now();
        }
    }
}
