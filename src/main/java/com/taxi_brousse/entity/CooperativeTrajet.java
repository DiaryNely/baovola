package com.taxi_brousse.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cooperative_trajet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CooperativeTrajetId.class)
public class CooperativeTrajet {

    @Id
    @Column(name = "cooperative_id")
    private Long cooperativeId;

    @Id
    @Column(name = "trajet_id")
    private Long trajetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperative_id", insertable = false, updatable = false)
    private Cooperative cooperative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajet_id", insertable = false, updatable = false)
    private Trajet trajet;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
