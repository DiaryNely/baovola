package com.taxi_brousse.entity;

import com.taxi_brousse.entity.reference.RefReservationStatut;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 80)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depart_id", nullable = false)
    private Depart depart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_reservation_statut_id", nullable = false)
    private RefReservationStatut refReservationStatut;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "montant_total", precision = 12, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "montant_paye", precision = 12, scale = 2)
    private BigDecimal montantPaye;

    @Column(name = "reste_a_payer", precision = 12, scale = 2)
    private BigDecimal resteAPayer;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (montantPaye == null) {
            montantPaye = BigDecimal.ZERO;
        }
        if (montantTotal != null && montantPaye != null) {
            resteAPayer = montantTotal.subtract(montantPaye);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (montantTotal != null && montantPaye != null) {
            resteAPayer = montantTotal.subtract(montantPaye);
        }
    }
}
