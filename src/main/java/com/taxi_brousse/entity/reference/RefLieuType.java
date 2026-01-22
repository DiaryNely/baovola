package com.taxi_brousse.entity.reference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ref_lieu_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefLieuType {

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
