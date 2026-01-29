package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.VenteProduitDTO;
import com.taxi_brousse.entity.VenteProduit;
import com.taxi_brousse.repository.ClientRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import com.taxi_brousse.repository.StockDepartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VenteProduitMapper {

    @Autowired
    private StockDepartRepository stockDepartRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RefDeviseRepository refDeviseRepository;

    public VenteProduitDTO toDTO(VenteProduit entity) {
        if (entity == null) {
            return null;
        }

        VenteProduitDTO dto = new VenteProduitDTO();
        dto.setId(entity.getId());
        dto.setStockDepartId(entity.getStockDepart().getId());
        dto.setDepartId(entity.getStockDepart().getDepart().getId());
        dto.setDepartCode(entity.getStockDepart().getDepart().getCode());
        dto.setProduitId(entity.getStockDepart().getProduit().getId());
        dto.setProduitNom(entity.getStockDepart().getProduit().getNom());

        if (entity.getClient() != null) {
            dto.setClientId(entity.getClient().getId());
            dto.setClientNom(entity.getClient().getNom());
            dto.setClientPrenom(entity.getClient().getPrenom());
        }

        dto.setQuantite(entity.getQuantite());
        dto.setPrixUnitaire(entity.getPrixUnitaire());
        dto.setMontantTotal(entity.getMontantTotal());
        dto.setRefDeviseId(entity.getRefDevise().getId());
        dto.setRefDeviseCode(entity.getRefDevise().getCode());
        dto.setDateVente(entity.getDateVente());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public VenteProduit toEntity(VenteProduitDTO dto) {
        if (dto == null) {
            return null;
        }

        VenteProduit entity = new VenteProduit();
        entity.setId(dto.getId());

        if (dto.getStockDepartId() != null) {
            entity.setStockDepart(stockDepartRepository.findById(dto.getStockDepartId())
                    .orElseThrow(() -> new RuntimeException("Stock non trouvé: " + dto.getStockDepartId())));
        }

        if (dto.getClientId() != null) {
            entity.setClient(clientRepository.findById(dto.getClientId())
                    .orElse(null)); // Client optionnel
        }

        entity.setQuantite(dto.getQuantite());
        entity.setPrixUnitaire(dto.getPrixUnitaire());
        entity.setMontantTotal(dto.getMontantTotal());

        if (dto.getRefDeviseId() != null) {
            entity.setRefDevise(refDeviseRepository.findById(dto.getRefDeviseId())
                    .orElseThrow(() -> new RuntimeException("Devise non trouvée: " + dto.getRefDeviseId())));
        }

        entity.setDateVente(dto.getDateVente());
        entity.setNotes(dto.getNotes());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }
}
