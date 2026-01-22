package com.taxi_brousse.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefTypeDepense;

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
@Table(name = "depense_vehicule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepenseVehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private Cooperative cooperative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_type_depense_id", nullable = false)
    private RefTypeDepense refTypeDepense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_devise_id", nullable = false)
    private RefDevise refDevise;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_depense", nullable = false)
    private LocalDate dateDepense;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "numero_piece", length = 100)
    private String numeroPiece;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (dateDepense == null) {
            dateDepense = LocalDate.now();
        }
    }
}
