package com.taxi_brousse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiegeCategorieDTO {
    private Long id;
    private String code;
    private String libelle;
    private Integer ordre;
    private Boolean actif;
}
