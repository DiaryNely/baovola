package com.taxi_brousse.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "tarif_publicite")
@Getter
@Setter
public class TarifPublicite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_devise_id", nullable = false)
    private RefDevise refDevise;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;
    
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    
    @Column(name = "date_fin")
    private LocalDate dateFin;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
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
