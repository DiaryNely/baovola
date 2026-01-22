package com.taxi_brousse.entity;

import com.taxi_brousse.entity.reference.RefBilletStatut;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "billet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_billet_statut_id", nullable = false)
    private RefBilletStatut refBilletStatut;

    @Column(name = "numero", nullable = false, unique = true, length = 120)
    private String numero;
    
    @Column(name = "code", length = 120)
    private String code;
    
    @Column(name = "passager_nom", length = 120)
    private String passagerNom;
    
    @Column(name = "passager_prenom", length = 120)
    private String passagerPrenom;
    
    @Column(name = "numero_siege")
    private Integer numeroSiege;

    @Column(name = "date_emission", nullable = false)
    private LocalDateTime dateEmission;

    @Column(name = "contenu_qr", columnDefinition = "TEXT")
    private String contenuQr;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dateEmission == null) {
            dateEmission = LocalDateTime.now();
        }
    }
}
