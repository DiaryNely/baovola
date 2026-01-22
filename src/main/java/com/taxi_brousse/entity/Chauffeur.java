package com.taxi_brousse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "chauffeur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chauffeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", length = 100)
    private String prenom;

    @Column(name = "telephone", length = 30)
    private String telephone;

    @Column(name = "numero_permis", nullable = false, unique = true, length = 60)
    private String numeroPermis;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
