package com.taxi_brousse.entity.reference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "ref_billet_statut")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefBilletStatut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "libelle", nullable = false, length = 80)
    private String libelle;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;
}
