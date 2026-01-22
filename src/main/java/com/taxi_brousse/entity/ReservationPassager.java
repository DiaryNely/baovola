package com.taxi_brousse.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefPassagerCategorie;
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
@Table(name = "reservation_passager", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"depart_id", "numero_siege"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPassager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depart_id", nullable = false)
    private Depart depart;

    @Column(name = "nom", nullable = false, length = 120)
    private String nom;

    @Column(name = "prenom", length = 120)
    private String prenom;

    @Column(name = "numero_siege", nullable = false)
    private Integer numeroSiege;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_siege_categorie_id")
    private RefSiegeCategorie refSiegeCategorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_passager_categorie_id")
    private RefPassagerCategorie refPassagerCategorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_devise_id")
    private RefDevise refDevise;

    @Column(name = "montant_tarif", precision = 12, scale = 2)
    private BigDecimal montantTarif;

    @Column(name = "montant_remise", precision = 12, scale = 2)
    private BigDecimal montantRemise;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
