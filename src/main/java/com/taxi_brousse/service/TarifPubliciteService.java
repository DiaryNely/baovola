package com.taxi_brousse.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.TarifPubliciteDTO;
import com.taxi_brousse.entity.TarifPublicite;
import com.taxi_brousse.exception.BadRequestException;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.TarifPubliciteMapper;
import com.taxi_brousse.repository.DepartPubliciteRepository;
import com.taxi_brousse.repository.TarifPubliciteRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TarifPubliciteService {
    
    private final TarifPubliciteRepository tarifPubliciteRepository;
    private final TarifPubliciteMapper tarifPubliciteMapper;
    private final DepartPubliciteRepository departPubliciteRepository;
    
    /**
     * Récupère tous les tarifs triés par date de début décroissant
     */
    public List<TarifPubliciteDTO> findAll() {
        return tarifPubliciteRepository.findAllByOrderByDateDebutDesc().stream()
                .map(tarifPubliciteMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un tarif par ID
     */
    public TarifPubliciteDTO findById(Long id) {
        TarifPublicite tarif = tarifPubliciteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TarifPublicite", "id", id));
        return tarifPubliciteMapper.toDTO(tarif);
    }
    
    /**
     * Récupère le tarif actuel (sans date de fin)
     */
    public TarifPubliciteDTO findTarifActuel() {
        TarifPublicite tarif = tarifPubliciteRepository.findTarifActuel()
                .orElseThrow(() -> new ResourceNotFoundException("Aucun tarif actuel trouvé"));
        return tarifPubliciteMapper.toDTO(tarif);
    }
    
    /**
     * Récupère le tarif en vigueur à une date donnée
     */
    public TarifPubliciteDTO findTarifEnVigueur(LocalDate date) {
        final LocalDate dateEffective = (date == null) ? LocalDate.now() : date;
        
        TarifPublicite tarif = tarifPubliciteRepository.findTarifEnVigueur(dateEffective)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Aucun tarif trouvé pour la date " + dateEffective));
        return tarifPubliciteMapper.toDTO(tarif);
    }
    
    /**
     * Crée un nouveau tarif publicitaire
     */
    public TarifPubliciteDTO create(TarifPubliciteDTO dto) {
        // Validations métier
        validerTarif(dto, null);
        
        TarifPublicite tarif = tarifPubliciteMapper.toEntity(dto);
        tarif = tarifPubliciteRepository.save(tarif);
        
        return tarifPubliciteMapper.toDTO(tarif);
    }
    
    /**
     * Met à jour un tarif existant
     */
    public TarifPubliciteDTO update(Long id, TarifPubliciteDTO dto) {
        TarifPublicite tarif = tarifPubliciteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TarifPublicite", "id", id));

        java.math.BigDecimal montantAvant = tarif.getMontant();
        
        // Validations métier
        validerTarif(dto, id);
        
        tarifPubliciteMapper.updateEntity(dto, tarif);
        tarif = tarifPubliciteRepository.save(tarif);

        // Recalculer uniquement si le tarif unitaire a été modifié via update
        if (dto.getMontant() != null && montantAvant != null && dto.getMontant().compareTo(montantAvant) != 0) {
            departPubliciteRepository.recalculerMontantsParTarif(tarif.getId(), tarif.getMontant());
        }
        
        return tarifPubliciteMapper.toDTO(tarif);
    }
    
    /**
     * Supprime un tarif
     */
    public void delete(Long id) {
        TarifPublicite tarif = tarifPubliciteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TarifPublicite", "id", id));
        
        // Vérifier si c'est le tarif actuel
        if (tarif.getDateFin() == null && tarif.getActif()) {
            throw new BadRequestException("Impossible de supprimer le tarif actuel. " +
                "Veuillez d'abord définir une date de fin.");
        }
        
        tarifPubliciteRepository.delete(tarif);
    }
    
    /**
     * Désactive un tarif (met actif = false)
     */
    public TarifPubliciteDTO desactiver(Long id) {
        TarifPublicite tarif = tarifPubliciteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TarifPublicite", "id", id));
        
        tarif.setActif(false);
        tarif = tarifPubliciteRepository.save(tarif);
        
        return tarifPubliciteMapper.toDTO(tarif);
    }
    
    /**
     * Clôture un tarif actuel en définissant une date de fin
     */
    public TarifPubliciteDTO cloturerTarifActuel(LocalDate dateFin) {
        TarifPublicite tarif = tarifPubliciteRepository.findTarifActuel()
                .orElseThrow(() -> new ResourceNotFoundException("Aucun tarif actuel trouvé"));
        
        if (dateFin == null) {
            dateFin = LocalDate.now().minusDays(1);
        }
        
        if (dateFin.isBefore(tarif.getDateDebut())) {
            throw new BadRequestException("La date de fin ne peut pas être antérieure à la date de début");
        }
        
        tarif.setDateFin(dateFin);
        tarif = tarifPubliciteRepository.save(tarif);
        
        return tarifPubliciteMapper.toDTO(tarif);
    }
    
    /**
     * Validations métier
     */
    private void validerTarif(TarifPubliciteDTO dto, Long excludeId) {
        // 1. Vérifier que la date de fin est après la date de début
        if (dto.getDateFin() != null && dto.getDateFin().isBefore(dto.getDateDebut())) {
            throw new BadRequestException("La date de fin doit être postérieure ou égale à la date de début");
        }
        
        // 2. Si c'est un nouveau tarif actuel (sans date de fin), vérifier qu'il n'en existe pas déjà un
        if (dto.getDateFin() == null && dto.getActif()) {
            Long idToExclude = (excludeId != null) ? excludeId : 0L;
            boolean existeAutreTarifActif = tarifPubliciteRepository.existsTarifActifAutre(idToExclude);
            
            if (existeAutreTarifActif) {
                throw new BadRequestException(
                    "Un tarif actuel existe déjà. Veuillez d'abord clôturer le tarif actuel " +
                    "avant d'en créer un nouveau.");
            }
        }
        
        // 3. Vérifier que le montant est valide
        if (dto.getMontant() == null || dto.getMontant().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le montant doit être supérieur à 0");
        }
        
        // 4. Vérifier que la date de début n'est pas trop ancienne (plus de 10 ans)
        LocalDate dateMinimum = LocalDate.now().minusYears(10);
        if (dto.getDateDebut().isBefore(dateMinimum)) {
            throw new BadRequestException("La date de début ne peut pas être antérieure à " + dateMinimum);
        }
    }
}
