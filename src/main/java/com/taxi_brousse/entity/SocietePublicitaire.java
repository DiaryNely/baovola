package com.taxi_brousse.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "societe_publicitaire")
@Getter
@Setter
public class SocietePublicitaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String nom;
    
    @Column(length = 30)
    private String telephone;
    
    @Column(length = 120)
    private String email;
    
    @Column(columnDefinition = "TEXT")
    private String adresse;
    
    @Column(name = "contact_nom", length = 150)
    private String contactNom;
    
    @Column(name = "contact_telephone", length = 30)
    private String contactTelephone;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "societePublicitaire", cascade = CascadeType.ALL)
    private List<Publicite> publicites = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
