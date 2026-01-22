package com.taxi_brousse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeSiegeConfigDTO {
    private Long id;

    @NotNull(message = "Le véhicule est obligatoire")
    private Long vehiculeId;

    @NotNull(message = "La catégorie de siège est obligatoire")
    private Long refSiegeCategorieId;

    private String refSiegeCategorieCode;
    private String refSiegeCategorieLibelle;
    private Integer refSiegeCategorieOrdre;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 0, message = "Le nombre de places doit être positif")
    private Integer nbPlaces;
}
