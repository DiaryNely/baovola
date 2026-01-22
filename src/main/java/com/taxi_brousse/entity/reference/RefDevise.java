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
@Table(name = "ref_devise")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefDevise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;
    
    @Column(name = "symbole", length = 10)
    private String symbole;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getSymbole() {
        return symbole;
    }
}
