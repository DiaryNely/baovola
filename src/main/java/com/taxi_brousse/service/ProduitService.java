package com.taxi_brousse.service;

import com.taxi_brousse.dto.ProduitDTO;
import com.taxi_brousse.entity.Produit;
import com.taxi_brousse.mapper.ProduitMapper;
import com.taxi_brousse.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ProduitMapper produitMapper;

    public List<ProduitDTO> findAll() {
        return produitRepository.findAll().stream()
                .map(produitMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProduitDTO> findAllActive() {
        return produitRepository.findAllActive().stream()
                .map(produitMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<ProduitDTO> searchProduits(Long categorieId, Boolean actif, String search, Pageable pageable) {
        return produitRepository.searchProduits(categorieId, actif, search, pageable)
                .map(produitMapper::toDTO);
    }

    public ProduitDTO findById(Long id) {
        Produit produit = produitRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'ID: " + id));
        return produitMapper.toDTO(produit);
    }

    public ProduitDTO findByCode(String code) {
        Produit produit = produitRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec le code: " + code));
        return produitMapper.toDTO(produit);
    }

    public List<ProduitDTO> findByCategorie(Long categorieId) {
        return produitRepository.findByCategorie(categorieId).stream()
                .map(produitMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProduitDTO save(ProduitDTO produitDTO) {
        // Vérifier l'unicité du code
        if (produitDTO.getId() == null) {
            produitRepository.findByCode(produitDTO.getCode()).ifPresent(existing -> {
                throw new RuntimeException("Un produit avec ce code existe déjà: " + produitDTO.getCode());
            });
        } else {
            produitRepository.findByCode(produitDTO.getCode()).ifPresent(existing -> {
                if (!existing.getId().equals(produitDTO.getId())) {
                    throw new RuntimeException("Un produit avec ce code existe déjà: " + produitDTO.getCode());
                }
            });
        }

        Produit produit = produitMapper.toEntity(produitDTO);
        Produit saved = produitRepository.save(produit);
        return produitMapper.toDTO(saved);
    }

    public void deleteById(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new RuntimeException("Produit introuvable avec l'ID: " + id);
        }
        produitRepository.deleteById(id);
    }

    public void toggleActif(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'ID: " + id));
        produit.setActif(!produit.getActif());
        produitRepository.save(produit);
    }

    public Long countActive() {
        return produitRepository.countActive();
    }

    public String generateNextCode() {
        List<Produit> produits = produitRepository.findAll();
        int maxNumber = 0;
        
        for (Produit p : produits) {
            String code = p.getCode();
            if (code != null && code.startsWith("PROD-")) {
                try {
                    int number = Integer.parseInt(code.substring(5));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignorer les codes qui ne suivent pas le format
                }
            }
        }
        
        return String.format("PROD-%03d", maxNumber + 1);
    }
}
