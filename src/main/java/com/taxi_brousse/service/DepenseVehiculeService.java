package com.taxi_brousse.service;

import com.taxi_brousse.dto.DepenseVehiculeDTO;
import com.taxi_brousse.entity.Cooperative;
import com.taxi_brousse.entity.DepenseVehicule;
import com.taxi_brousse.entity.Vehicule;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefTypeDepense;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.DepenseVehiculeMapper;
import com.taxi_brousse.repository.CooperativeRepository;
import com.taxi_brousse.repository.DepenseVehiculeRepository;
import com.taxi_brousse.repository.RefTypeDepenseRepository;
import com.taxi_brousse.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DepenseVehiculeService {

    private final DepenseVehiculeRepository depenseVehiculeRepository;
    private final VehiculeRepository vehiculeRepository;
    private final CooperativeRepository cooperativeRepository;
    private final RefTypeDepenseRepository refTypeDepenseRepository;
    private final DepenseVehiculeMapper depenseVehiculeMapper;

    public List<DepenseVehiculeDTO> findAll() {
        return depenseVehiculeRepository.findAll().stream()
                .map(depenseVehiculeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public DepenseVehiculeDTO findById(Long id) {
        DepenseVehicule depense = depenseVehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepenseVehicule", "id", id));
        return depenseVehiculeMapper.toDTO(depense);
    }

    public List<DepenseVehiculeDTO> findByVehiculeId(Long vehiculeId) {
        return depenseVehiculeRepository.findByVehiculeId(vehiculeId).stream()
                .map(depenseVehiculeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DepenseVehiculeDTO> findByCooperativeId(Long cooperativeId) {
        return depenseVehiculeRepository.findByCooperativeId(cooperativeId).stream()
                .map(depenseVehiculeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DepenseVehiculeDTO> findByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        return depenseVehiculeRepository.findByDateDepenseBetween(dateDebut, dateFin).stream()
                .map(depenseVehiculeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DepenseVehiculeDTO> findByVehiculeIdAndDateRange(Long vehiculeId, LocalDate dateDebut, LocalDate dateFin) {
        return depenseVehiculeRepository.findByVehiculeIdAndDateDepenseBetween(vehiculeId, dateDebut, dateFin).stream()
                .map(depenseVehiculeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public DepenseVehiculeDTO create(DepenseVehiculeDTO depenseDTO) {
        DepenseVehicule depense = depenseVehiculeMapper.toEntity(depenseDTO);
        
        // Charger et associer le véhicule
        Vehicule vehicule = vehiculeRepository.findById(depenseDTO.getVehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", depenseDTO.getVehiculeId()));
        depense.setVehicule(vehicule);
        
        // Charger et associer la coopérative
        Cooperative cooperative = cooperativeRepository.findById(depenseDTO.getCooperativeId())
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative", "id", depenseDTO.getCooperativeId()));
        depense.setCooperative(cooperative);
        
        // Charger et associer le type de dépense
        RefTypeDepense typeDepense = refTypeDepenseRepository.findById(depenseDTO.getRefTypeDepenseId())
                .orElseThrow(() -> new ResourceNotFoundException("RefTypeDepense", "id", depenseDTO.getRefTypeDepenseId()));
        depense.setRefTypeDepense(typeDepense);
        
        // Associer la devise
        if (depenseDTO.getRefDeviseId() != null) {
            RefDevise devise = new RefDevise();
            devise.setId(depenseDTO.getRefDeviseId());
            depense.setRefDevise(devise);
        }
        
        DepenseVehicule savedDepense = depenseVehiculeRepository.save(depense);
        return depenseVehiculeMapper.toDTO(savedDepense);
    }

    public DepenseVehiculeDTO update(Long id, DepenseVehiculeDTO depenseDTO) {
        DepenseVehicule existingDepense = depenseVehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepenseVehicule", "id", id));

        // Mettre à jour les champs simples
        existingDepense.setMontant(depenseDTO.getMontant());
        existingDepense.setDateDepense(depenseDTO.getDateDepense());
        existingDepense.setDescription(depenseDTO.getDescription());
        existingDepense.setNumeroPiece(depenseDTO.getNumeroPiece());
        
        // Mettre à jour le véhicule si modifié
        if (depenseDTO.getVehiculeId() != null && !depenseDTO.getVehiculeId().equals(existingDepense.getVehicule().getId())) {
            Vehicule vehicule = vehiculeRepository.findById(depenseDTO.getVehiculeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", depenseDTO.getVehiculeId()));
            existingDepense.setVehicule(vehicule);
        }
        
        // Mettre à jour la coopérative si modifiée
        if (depenseDTO.getCooperativeId() != null && !depenseDTO.getCooperativeId().equals(existingDepense.getCooperative().getId())) {
            Cooperative cooperative = cooperativeRepository.findById(depenseDTO.getCooperativeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cooperative", "id", depenseDTO.getCooperativeId()));
            existingDepense.setCooperative(cooperative);
        }
        
        // Mettre à jour le type de dépense si modifié
        if (depenseDTO.getRefTypeDepenseId() != null && !depenseDTO.getRefTypeDepenseId().equals(existingDepense.getRefTypeDepense().getId())) {
            RefTypeDepense typeDepense = refTypeDepenseRepository.findById(depenseDTO.getRefTypeDepenseId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefTypeDepense", "id", depenseDTO.getRefTypeDepenseId()));
            existingDepense.setRefTypeDepense(typeDepense);
        }
        
        // Mettre à jour la devise si modifiée
        if (depenseDTO.getRefDeviseId() != null && !depenseDTO.getRefDeviseId().equals(existingDepense.getRefDevise().getId())) {
            RefDevise devise = new RefDevise();
            devise.setId(depenseDTO.getRefDeviseId());
            existingDepense.setRefDevise(devise);
        }

        DepenseVehicule updatedDepense = depenseVehiculeRepository.save(existingDepense);
        return depenseVehiculeMapper.toDTO(updatedDepense);
    }

    public void delete(Long id) {
        DepenseVehicule depense = depenseVehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepenseVehicule", "id", id));
        depenseVehiculeRepository.delete(depense);
    }
}
