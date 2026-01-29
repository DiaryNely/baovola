package com.taxi_brousse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefCategorieProduitDTO {
    private Long id;
    private String code;
    private String libelle;
    private String description;
    private Boolean actif;
    private LocalDateTime createdAt;
}
