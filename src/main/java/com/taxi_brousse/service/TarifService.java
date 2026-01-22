package com.taxi_brousse.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.TarifDTO;
import com.taxi_brousse.entity.Cooperative;
import com.taxi_brousse.entity.Tarif;
import com.taxi_brousse.entity.Trajet;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefVehiculeType;
import com.taxi_brousse.mapper.TarifMapper;
import com.taxi_brousse.repository.CooperativeRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import com.taxi_brousse.repository.RefVehiculeTypeRepository;
import com.taxi_brousse.repository.TarifRepository;
import com.taxi_brousse.repository.TrajetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifService {

    private final TarifRepository tarifRepository;
    private final CooperativeRepository cooperativeRepository;
    private final TrajetRepository trajetRepository;
    private final RefVehiculeTypeRepository refVehiculeTypeRepository;
    private final RefDeviseRepository refDeviseRepository;
    private final TarifMapper tarifMapper;

    @Transactional(readOnly = true)
    public List<TarifDTO> getAllTarifs() {
        return tarifRepository.findAll().stream()
                .map(tarifMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TarifDTO getTarifById(Long id) {
        Tarif tarif = tarifRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarif non trouvé avec l'ID: " + id));
        return tarifMapper.toDTO(tarif);
    }

    @Transactional
    public TarifDTO createTarif(TarifDTO tarifDTO) {
        // Validation
        validateTarif(tarifDTO);
        
        // Vérifier les chevauchements
        checkDateOverlap(tarifDTO);
        
        // Créer l'entité
        Tarif tarif = new Tarif();
        
        // Charger les entités liées
        Cooperative cooperative = cooperativeRepository.findById(tarifDTO.getCooperativeId())
                .orElseThrow(() -> new RuntimeException("Coopérative non trouvée"));
        tarif.setCooperative(cooperative);
        
        Trajet trajet = trajetRepository.findById(tarifDTO.getTrajetId())
                .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));
        tarif.setTrajet(trajet);
        
        RefVehiculeType vehiculeType = refVehiculeTypeRepository.findById(tarifDTO.getRefVehiculeTypeId())
                .orElseThrow(() -> new RuntimeException("Type de véhicule non trouvé"));
        tarif.setRefVehiculeType(vehiculeType);
        
        RefDevise devise = refDeviseRepository.findById(tarifDTO.getRefDeviseId())
                .orElseThrow(() -> new RuntimeException("Devise non trouvée"));
        tarif.setRefDevise(devise);
        
        // Données du tarif
        tarif.setMontant(tarifDTO.getMontant());
        tarif.setDateDebut(tarifDTO.getDateDebut() != null ? tarifDTO.getDateDebut() : LocalDate.now());
        tarif.setDateFin(tarifDTO.getDateFin());
        
        Tarif saved = tarifRepository.save(tarif);
        return tarifMapper.toDTO(saved);
    }

    @Transactional
    public TarifDTO updateTarif(Long id, TarifDTO tarifDTO) {
        Tarif existingTarif = tarifRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarif non trouvé avec l'ID: " + id));
        
        // Validation
        validateTarif(tarifDTO);
        
        // Vérifier les chevauchements (en excluant le tarif actuel)
        tarifDTO.setId(id);
        checkDateOverlap(tarifDTO);
        
        // Mettre à jour les relations
        if (tarifDTO.getCooperativeId() != null) {
            Cooperative cooperative = cooperativeRepository.findById(tarifDTO.getCooperativeId())
                    .orElseThrow(() -> new RuntimeException("Coopérative non trouvée"));
            existingTarif.setCooperative(cooperative);
        }
        
        if (tarifDTO.getTrajetId() != null) {
            Trajet trajet = trajetRepository.findById(tarifDTO.getTrajetId())
                    .orElseThrow(() -> new RuntimeException("Trajet non trouvé"));
            existingTarif.setTrajet(trajet);
        }
        
        if (tarifDTO.getRefVehiculeTypeId() != null) {
            RefVehiculeType vehiculeType = refVehiculeTypeRepository.findById(tarifDTO.getRefVehiculeTypeId())
                    .orElseThrow(() -> new RuntimeException("Type de véhicule non trouvé"));
            existingTarif.setRefVehiculeType(vehiculeType);
        }
        
        if (tarifDTO.getRefDeviseId() != null) {
            RefDevise devise = refDeviseRepository.findById(tarifDTO.getRefDeviseId())
                    .orElseThrow(() -> new RuntimeException("Devise non trouvée"));
            existingTarif.setRefDevise(devise);
        }
        
        // Mettre à jour les données
        existingTarif.setMontant(tarifDTO.getMontant());
        existingTarif.setDateDebut(tarifDTO.getDateDebut());
        existingTarif.setDateFin(tarifDTO.getDateFin());
        
        Tarif updated = tarifRepository.save(existingTarif);
        return tarifMapper.toDTO(updated);
    }

    @Transactional
    public void deleteTarif(Long id) {
        if (!tarifRepository.existsById(id)) {
            throw new RuntimeException("Tarif non trouvé avec l'ID: " + id);
        }
        tarifRepository.deleteById(id);
    }

    private void validateTarif(TarifDTO tarifDTO) {
        if (tarifDTO.getCooperativeId() == null) {
            throw new RuntimeException("La coopérative est obligatoire");
        }
        if (tarifDTO.getTrajetId() == null) {
            throw new RuntimeException("Le trajet est obligatoire");
        }
        if (tarifDTO.getRefVehiculeTypeId() == null) {
            throw new RuntimeException("Le type de véhicule est obligatoire");
        }
        if (tarifDTO.getRefDeviseId() == null) {
            throw new RuntimeException("La devise est obligatoire");
        }
        if (tarifDTO.getMontant() == null || tarifDTO.getMontant().signum() <= 0) {
            throw new RuntimeException("Le montant doit être positif");
        }
        if (tarifDTO.getDateDebut() == null) {
            throw new RuntimeException("La date de début est obligatoire");
        }
        if (tarifDTO.getDateFin() != null && tarifDTO.getDateFin().isBefore(tarifDTO.getDateDebut())) {
            throw new RuntimeException("La date de fin doit être postérieure à la date de début");
        }
    }

    private void checkDateOverlap(TarifDTO tarifDTO) {
        // Récupérer tous les tarifs pour la même combinaison
        List<Tarif> existingTarifs = tarifRepository.findAll().stream()
                .filter(t -> t.getCooperative().getId().equals(tarifDTO.getCooperativeId())
                          && t.getTrajet().getId().equals(tarifDTO.getTrajetId())
                          && t.getRefVehiculeType().getId().equals(tarifDTO.getRefVehiculeTypeId())
                          && t.getRefDevise().getId().equals(tarifDTO.getRefDeviseId())
                          && (tarifDTO.getId() == null || !t.getId().equals(tarifDTO.getId())))
                .collect(Collectors.toList());
        
        for (Tarif existing : existingTarifs) {
            if (periodsOverlap(
                    tarifDTO.getDateDebut(), tarifDTO.getDateFin(),
                    existing.getDateDebut(), existing.getDateFin())) {
                throw new RuntimeException("Les périodes de tarifs se chevauchent avec un tarif existant");
            }
        }
    }

    private boolean periodsOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        // Si end1 est null, la période 1 est ouverte (va à l'infini)
        // Si end2 est null, la période 2 est ouverte (va à l'infini)
        
        if (end1 == null && end2 == null) {
            // Les deux périodes sont ouvertes, elles se chevauchent forcément
            return true;
        }
        
        if (end1 == null) {
            // Période 1 ouverte: chevauchement si start1 <= end2
            return !start1.isAfter(end2);
        }
        
        if (end2 == null) {
            // Période 2 ouverte: chevauchement si start2 <= end1
            return !start2.isAfter(end1);
        }
        
        // Les deux périodes sont fermées
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
}
