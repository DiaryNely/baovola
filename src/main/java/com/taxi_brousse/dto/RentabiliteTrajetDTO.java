package com.taxi_brousse.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentabiliteTrajetDTO {
    
    private Long trajetId;
    private String trajetCode;
    private String lieuDepartNom;
    private String lieuArriveeNom;
    private String itineraire; // "Antananarivo → Toamasina"
    
    private Integer nombreDeparts;
    private Integer nombreReservations;
    
    private BigDecimal revenusTotal; // CA + publicités
    private BigDecimal revenusReservations; // CA uniquement
    private BigDecimal revenusPublicites; // Publicités uniquement
    
    private Double tauxRemplissageMoyen; // %
    private BigDecimal revenuMoyenParDepart;
}
