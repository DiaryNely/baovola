package com.taxi_brousse.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LieuDTO {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long refLieuTypeId;
    private String refLieuTypeLibelle;
}
