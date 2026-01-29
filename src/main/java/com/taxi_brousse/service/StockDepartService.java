package com.taxi_brousse.service;

import com.taxi_brousse.dto.StockDepartDTO;
import com.taxi_brousse.entity.StockDepart;
import com.taxi_brousse.mapper.StockDepartMapper;
import com.taxi_brousse.repository.StockDepartRepository;
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
        // Vérifier si un stock existe déjà pour ce départ et ce produit
        if (dto.getId() == null) {
            Optional<StockDepart> existing = stockDepartRepository
                    .findByDepartIdAndProduitId(dto.getDepartId(), dto.getProduitId());
            if (existing.isPresent()) {
                throw new RuntimeException("Un stock existe déjà pour ce produit sur ce départ");
            }
        }

        // Initialiser quantite_disponible = quantite_initiale si nouveau
        if (dto.getId() == null) {
            dto.setQuantiteDisponible(dto.getQuantiteInitiale());
            dto.setQuantiteVendue(0);
        }

        StockDepart entity = stockDepartMapper.toEntity(dto);
        StockDepart saved = stockDepartRepository.save(entity);
        return stockDepartMapper.toDTO(saved);
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
