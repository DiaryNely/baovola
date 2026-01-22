package com.taxi_brousse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trajet_escale")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TrajetEscaleId.class)
public class TrajetEscale {

    @Id
    @Column(name = "trajet_id")
    private Long trajetId;

    @Id
    @Column(name = "ordre")
    private Integer ordre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", insertable = false, updatable = false)
    private Trajet trajet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lieu_id", nullable = false)
    private Lieu lieu;

    @Column(name = "duree_arret_min")
    private Integer dureeArretMin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
