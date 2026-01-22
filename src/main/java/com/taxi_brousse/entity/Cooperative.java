package com.taxi_brousse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cooperative")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cooperative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "telephone", length = 30)
    private String telephone;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "adresse", columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
