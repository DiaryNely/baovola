package com.taxi_brousse.entity;

import com.taxi_brousse.entity.reference.RefChauffeurVehiculeRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chauffeur_vehicule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ChauffeurVehiculeId.class)
public class ChauffeurVehicule {

    @Id
    @Column(name = "chauffeur_id")
    private Long chauffeurId;

    @Id
    @Column(name = "vehicule_id")
    private Long vehiculeId;

    @Id
    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chauffeur_id", insertable = false, updatable = false)
    private Chauffeur chauffeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", insertable = false, updatable = false)
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_chauffeur_vehicule_role_id", nullable = false)
    private RefChauffeurVehiculeRole refChauffeurVehiculeRole;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dateDebut == null) {
            dateDebut = LocalDateTime.now();
        }
    }
}
