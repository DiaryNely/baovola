package com.taxi_brousse.entity;

import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefPaiementMethode;
import com.taxi_brousse.entity.reference.RefPaiementStatut;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column(name = "montant", nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_devise_id", nullable = false)
    private RefDevise refDevise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_paiement_methode_id", nullable = false)
    private RefPaiementMethode refPaiementMethode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_paiement_statut_id", nullable = false)
    private RefPaiementStatut refPaiementStatut;

    @Column(name = "reference_externe", length = 120)
    private String referenceExterne;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Alias pour compatibilité avec le service
    public void setReferenceTransaction(String reference) {
        this.referenceExterne = reference;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (datePaiement == null) {
            datePaiement = LocalDateTime.now();
        }
    }
}
