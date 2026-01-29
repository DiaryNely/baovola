package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.ProduitDTO;
import com.taxi_brousse.entity.Produit;
import com.taxi_brousse.entity.reference.RefCategorieProduit;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.repository.RefCategorieProduitRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProduitMapper {

    @Autowired
    private RefCategorieProduitRepository refCategorieProduitRepository;

    @Autowired
    private RefDeviseRepository refDeviseRepository;

    public ProduitDTO toDTO(Produit entity) {
        if (entity == null) {
            return null;
        }

        ProduitDTO dto = new ProduitDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setNom(entity.getNom());
        dto.setDescription(entity.getDescription());
        dto.setPrixVente(entity.getPrixVente());
        dto.setActif(entity.getActif());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getRefCategorieProduit() != null) {
            dto.setRefCategorieProduitId(entity.getRefCategorieProduit().getId());
            dto.setRefCategorieProduitLibelle(entity.getRefCategorieProduit().getLibelle());
        }

        if (entity.getRefDevise() != null) {
            dto.setRefDeviseId(entity.getRefDevise().getId());
            dto.setRefDeviseCode(entity.getRefDevise().getCode());
        }

        return dto;
    }

    public Produit toEntity(ProduitDTO dto) {
        if (dto == null) {
            return null;
        }

        Produit entity = new Produit();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setNom(dto.getNom());
        entity.setDescription(dto.getDescription());
        entity.setPrixVente(dto.getPrixVente());
        entity.setActif(dto.getActif());
        entity.setCreatedAt(dto.getCreatedAt());

        if (dto.getRefCategorieProduitId() != null) {
            RefCategorieProduit categorie = refCategorieProduitRepository.findById(dto.getRefCategorieProduitId())
                    .orElseThrow(() -> new RuntimeException("Catégorie produit introuvable: " + dto.getRefCategorieProduitId()));
            entity.setRefCategorieProduit(categorie);
        }

        if (dto.getRefDeviseId() != null) {
            RefDevise devise = refDeviseRepository.findById(dto.getRefDeviseId())
                    .orElseThrow(() -> new RuntimeException("Devise introuvable: " + dto.getRefDeviseId()));
            entity.setRefDevise(devise);
        }

        return entity;
    }
}
