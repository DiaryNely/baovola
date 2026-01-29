package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.StockDepartDTO;
import com.taxi_brousse.entity.StockDepart;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.ProduitRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StockDepartMapper {

    @Autowired
    private DepartRepository departRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private RefDeviseRepository refDeviseRepository;

    public StockDepartDTO toDTO(StockDepart entity) {
        if (entity == null) {
            return null;
        }

        StockDepartDTO dto = new StockDepartDTO();
        dto.setId(entity.getId());
        dto.setDepartId(entity.getDepart().getId());
        dto.setDepartCode(entity.getDepart().getCode());
        dto.setProduitId(entity.getProduit().getId());
        dto.setProduitCode(entity.getProduit().getCode());
        dto.setProduitNom(entity.getProduit().getNom());
        dto.setProduitCategorie(entity.getProduit().getRefCategorieProduit().getLibelle());
        dto.setQuantiteInitiale(entity.getQuantiteInitiale());
        dto.setQuantiteVendue(entity.getQuantiteVendue());
        dto.setQuantiteDisponible(entity.getQuantiteDisponible());
        dto.setPrixUnitaire(entity.getPrixUnitaire());
        dto.setRefDeviseId(entity.getRefDevise().getId());
        dto.setRefDeviseCode(entity.getRefDevise().getCode());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());

        // Calculer la valeur du stock disponible
        BigDecimal valeur = entity.getPrixUnitaire()
                .multiply(new BigDecimal(entity.getQuantiteDisponible()));
        dto.setValeurStockDisponible(valeur);

        return dto;
    }

    public StockDepart toEntity(StockDepartDTO dto) {
        if (dto == null) {
            return null;
        }

        StockDepart entity = new StockDepart();
        entity.setId(dto.getId());

        if (dto.getDepartId() != null) {
            entity.setDepart(departRepository.findById(dto.getDepartId())
                    .orElseThrow(() -> new RuntimeException("Départ non trouvé: " + dto.getDepartId())));
        }

        if (dto.getProduitId() != null) {
            entity.setProduit(produitRepository.findById(dto.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé: " + dto.getProduitId())));
        }

        entity.setQuantiteInitiale(dto.getQuantiteInitiale());
        entity.setQuantiteVendue(dto.getQuantiteVendue() != null ? dto.getQuantiteVendue() : 0);
        entity.setQuantiteDisponible(dto.getQuantiteDisponible() != null ? 
                dto.getQuantiteDisponible() : dto.getQuantiteInitiale());
        entity.setPrixUnitaire(dto.getPrixUnitaire());

        if (dto.getRefDeviseId() != null) {
            entity.setRefDevise(refDeviseRepository.findById(dto.getRefDeviseId())
                    .orElseThrow(() -> new RuntimeException("Devise non trouvée: " + dto.getRefDeviseId())));
        }

        entity.setNotes(dto.getNotes());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }
}
