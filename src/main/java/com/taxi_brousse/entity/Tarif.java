package com.taxi_brousse.entity;

import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefVehiculeType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarif", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cooperative_id", "trajet_id", "ref_vehicule_type_id", "ref_devise_id", "date_debut"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private Cooperative cooperative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", nullable = false)
    private Trajet trajet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_vehicule_type_id", nullable = false)
    private RefVehiculeType refVehiculeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_devise_id", nullable = false)
    private RefDevise refDevise;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

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
