package com.taxi_brousse.service;

import com.taxi_brousse.dto.VenteProduitDTO;
import com.taxi_brousse.entity.StockDepart;
import com.taxi_brousse.entity.VenteProduit;
import com.taxi_brousse.mapper.VenteProduitMapper;
import com.taxi_brousse.repository.StockDepartRepository;
import com.taxi_brousse.repository.VenteProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VenteProduitService {

    @Autowired
    private VenteProduitRepository venteProduitRepository;

    @Autowired
    private StockDepartRepository stockDepartRepository;

    @Autowired
    private VenteProduitMapper venteProduitMapper;

    public List<VenteProduitDTO> findByDepartId(Long departId) {
        return venteProduitRepository.findByDepartId(departId)
                .stream()
                .map(venteProduitMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<VenteProduitDTO> findByStockDepartId(Long stockDepartId) {
        return venteProduitRepository.findByStockDepartId(stockDepartId)
                .stream()
                .map(venteProduitMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<VenteProduitDTO> findById(Long id) {
        return venteProduitRepository.findById(id)
                .map(venteProduitMapper::toDTO);
    }

    public VenteProduitDTO save(VenteProduitDTO dto) {
        // Vérifier que le stock existe et est suffisant
        StockDepart stock = stockDepartRepository.findById(dto.getStockDepartId())
                .orElseThrow(() -> new RuntimeException("Stock non trouvé"));

        if (stock.getQuantiteDisponible() < dto.getQuantite()) {
            throw new RuntimeException("Stock insuffisant. Disponible: " + 
                    stock.getQuantiteDisponible() + ", Demandé: " + dto.getQuantite());
        }

        // Calculer le montant total
        BigDecimal montantTotal = dto.getPrixUnitaire()
                .multiply(new BigDecimal(dto.getQuantite()));
        dto.setMontantTotal(montantTotal);

        // Définir la date de vente si non fournie
        if (dto.getDateVente() == null) {
            dto.setDateVente(LocalDateTime.now());
        }

        // Récupérer la devise du stock si non fournie
        if (dto.getRefDeviseId() == null) {
            dto.setRefDeviseId(stock.getRefDevise().getId());
        }

        VenteProduit entity = venteProduitMapper.toEntity(dto);
        VenteProduit saved = venteProduitRepository.save(entity);
        
        // Mise à jour du stock côté Java (en plus du trigger PostgreSQL pour assurer la cohérence)
        stock.setQuantiteVendue(stock.getQuantiteVendue() + dto.getQuantite());
        stock.setQuantiteDisponible(stock.getQuantiteInitiale() - stock.getQuantiteVendue());
        stockDepartRepository.save(stock);
        
        return venteProduitMapper.toDTO(saved);
    }

    public void deleteById(Long id) {
        // Récupérer la vente avant suppression pour restaurer le stock
        VenteProduit vente = venteProduitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vente non trouvée"));
        
        StockDepart stock = vente.getStockDepart();
        if (stock != null) {
            // Restaurer le stock côté Java
            stock.setQuantiteVendue(Math.max(0, stock.getQuantiteVendue() - vente.getQuantite()));
            stock.setQuantiteDisponible(stock.getQuantiteInitiale() - stock.getQuantiteVendue());
            stockDepartRepository.save(stock);
        }
        
        venteProduitRepository.deleteById(id);
    }

    public BigDecimal calculateTotalByDepartId(Long departId) {
        return venteProduitRepository.calculateTotalByDepartId(departId);
    }

    public Long countByDepartId(Long departId) {
        return venteProduitRepository.countByDepartId(departId);
    }

    public List<VenteProduitDTO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return venteProduitRepository.findByDateRange(startDate, endDate)
                .stream()
                .map(venteProduitMapper::toDTO)
                .collect(Collectors.toList());
    }
}
