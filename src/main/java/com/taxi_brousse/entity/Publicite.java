package com.taxi_brousse.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "publicite")
@Getter
@Setter
public class Publicite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 80)
    private String code;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_publicitaire_id", nullable = false)
    private SocietePublicitaire societePublicitaire;
    
    @Column(nullable = false, length = 200)
    private String titre;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "url_video", length = 500)
    private String urlVideo;
    
    @Column(name = "duree_secondes")
    private Integer dureeSecondes;
    
    @Column(name = "date_debut_validite", nullable = false)
    private LocalDate dateDebutValidite;
    
    @Column(name = "date_fin_validite", nullable = false)
    private LocalDate dateFinValidite;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
