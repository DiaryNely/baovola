package com.taxi_brousse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, length = 120)
    private String nom;

    @Column(name = "prenom", length = 120)
    private String prenom;

    @Column(name = "telephone", length = 30)
    private String telephone;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "numero_cin", length = 50)
    private String numeroCin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
