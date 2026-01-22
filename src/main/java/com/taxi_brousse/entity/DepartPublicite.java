package com.taxi_brousse.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.taxi_brousse.entity.reference.RefDevise;

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
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "depart_publicite")
@Getter
@Setter
public class DepartPublicite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depart_id", nullable = false)
    private Depart depart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicite_id", nullable = false)
    private Publicite publicite;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarif_publicite_id", nullable = false)
    private TarifPublicite tarifPublicite;
    
    @Column(name = "date_diffusion", nullable = false)
    private LocalDateTime dateDiffusion;
    
    @Column(name = "montant_facture", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantFacture;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_devise_id", nullable = false)
    private RefDevise refDevise;
    
    @Column(name = "statut_diffusion", nullable = false, length = 50)
    private String statutDiffusion;
    
    @Column(name = "nombre_repetitions", nullable = false)
    private Integer nombreRepetitions;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (dateDiffusion == null) {
            dateDiffusion = LocalDateTime.now();
        }
        if (statutDiffusion == null) {
            statutDiffusion = "PLANIFIE";
        }
        if (nombreRepetitions == null) {
            nombreRepetitions = 1;
        }
    }
}
