package com.taxi_brousse.service;

import com.taxi_brousse.dto.StockDepartDTO;
import com.taxi_brousse.entity.StockDepart;
import com.taxi_brousse.mapper.StockDepartMapper;
import com.taxi_brousse.repository.StockDepartRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockDepartService {

    @Autowired
    private StockDepartRepository stockDepartRepository;

    @Autowired
    private StockDepartMapper stockDepartMapper;

    @Autowired
    private RefDeviseRepository refDeviseRepository;

    public List<StockDepartDTO> findByDepartId(Long departId) {
        return stockDepartRepository.findByDepartId(departId)
                .stream()
                .map(stockDepartMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<StockDepartDTO> findAvailableByDepartId(Long departId) {
        return stockDepartRepository.findAvailableByDepartId(departId)
                .stream()
                .map(stockDepartMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<StockDepartDTO> findById(Long id) {
        return stockDepartRepository.findByIdWithDetails(id)
                .map(stockDepartMapper::toDTO);
    }

    public StockDepartDTO save(StockDepartDTO dto) {
        StockDepart entity;
        
        // Vérifier si un stock existe déjà pour ce départ et ce produit (uniquement pour création)
        if (dto.getId() == null) {
            Optional<StockDepart> existing = stockDepartRepository
                    .findByDepartIdAndProduitId(dto.getDepartId(), dto.getProduitId());
            if (existing.isPresent()) {
                throw new RuntimeException("Un stock existe déjà pour ce produit sur ce départ");
            }
            
            // Initialiser quantite_disponible = quantite_initiale si nouveau
            dto.setQuantiteDisponible(dto.getQuantiteInitiale());
            dto.setQuantiteVendue(0);
            
            entity = stockDepartMapper.toEntity(dto);
        } else {
            // Modification : récupérer l'entité existante
            entity = stockDepartRepository.findByIdWithDetails(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Stock non trouvé"));
            
            if (dto.getQuantiteInitiale() < entity.getQuantiteVendue()) {
                throw new RuntimeException(
                    String.format("La quantité initiale (%d) ne peut pas être inférieure à la quantité déjà vendue (%d)",
                        dto.getQuantiteInitiale(), entity.getQuantiteVendue())
                );
            }
            
            // Mettre à jour uniquement les champs modifiables (pas le produit ni le départ)
            entity.setQuantiteInitiale(dto.getQuantiteInitiale());
            entity.setQuantiteDisponible(dto.getQuantiteInitiale() - entity.getQuantiteVendue());
            entity.setPrixUnitaire(dto.getPrixUnitaire());
            
            if (dto.getRefDeviseId() != null) {
                entity.setRefDevise(refDeviseRepository.findById(dto.getRefDeviseId())
                        .orElseThrow(() -> new RuntimeException("Devise non trouvée: " + dto.getRefDeviseId())));
            }
            
            entity.setNotes(dto.getNotes());
        }

        StockDepart saved = stockDepartRepository.save(entity);
        // Recharger avec les relations pour éviter les erreurs lazy loading
        return stockDepartRepository.findByIdWithDetails(saved.getId())
                .map(stockDepartMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Erreur lors de la récupération du stock sauvegardé"));
    }

    public void deleteById(Long id) {
        StockDepart stock = stockDepartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock non trouvé"));
        
        // Vérifier qu'il n'y a pas eu de ventes
        if (stock.getQuantiteVendue() > 0) {
            throw new RuntimeException("Impossible de supprimer un stock avec des ventes enregistrées");
        }
        
        stockDepartRepository.deleteById(id);
    }

    public BigDecimal calculateStockValue(Long departId) {
        return stockDepartRepository.calculateStockValue(departId);
    }

    public boolean hasStock(Long departId) {
        return !stockDepartRepository.findByDepartId(departId).isEmpty();
    }

    public Long countByDepartId(Long departId) {
        return (long) stockDepartRepository.findByDepartId(departId).size();
    }
}
