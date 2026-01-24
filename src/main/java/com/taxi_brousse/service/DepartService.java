package com.taxi_brousse.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.ChiffreAffairesCategorieDTO;
import com.taxi_brousse.dto.DepartDTO;
import com.taxi_brousse.dto.PaiementSocieteDTO;
import com.taxi_brousse.entity.Cooperative;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.Lieu;
import com.taxi_brousse.entity.ReservationPassager;
import com.taxi_brousse.entity.Trajet;
import com.taxi_brousse.entity.Vehicule;
import com.taxi_brousse.entity.reference.RefDepartStatut;
import com.taxi_brousse.exception.BadRequestException;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.DepartMapper;
import com.taxi_brousse.repository.CooperativeRepository;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.LieuRepository;
import com.taxi_brousse.repository.RefDepartStatutRepository;
import com.taxi_brousse.repository.ReservationPassagerRepository;
import com.taxi_brousse.repository.ReservationRepository;
import com.taxi_brousse.repository.TrajetRepository;
import com.taxi_brousse.repository.VehiculeRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartService {

    private final DepartRepository departRepository;
    private final CooperativeRepository cooperativeRepository;
    private final TrajetRepository trajetRepository;
    private final VehiculeRepository vehiculeRepository;
    private final LieuRepository lieuRepository;
    private final RefDepartStatutRepository refDepartStatutRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationPassagerRepository reservationPassagerRepository;
    private final DepartMapper departMapper;
    private final com.taxi_brousse.repository.PaiementRepository paiementRepository;
    private final com.taxi_brousse.repository.DepartPubliciteRepository departPubliciteRepository;
    private final com.taxi_brousse.repository.PaiementPubliciteRepository paiementPubliciteRepository;
    private final VehiculeSiegeConfigService vehiculeSiegeConfigService;
    private final DepartTarifSiegeService departTarifSiegeService;
    private final SiegeConfigurationService siegeConfigurationService;
    private final ReservationService reservationService;

    public List<DepartDTO> findAll() {
        return departRepository.findAll().stream()
                .map(departMapper::toDTO)
                .map(this::enrichDepartDTO)
                .collect(Collectors.toList());
    }

    public DepartDTO findById(Long id) {
        Depart depart = departRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", id));
        return enrichDepartDTO(departMapper.toDTO(depart));
    }

    public List<DepartDTO> findDepartsDisponibles() {
        LocalDateTime now = LocalDateTime.now();
        return departRepository.findAll().stream()
                .filter(d -> d.getDateHeureDepart().isAfter(now))
                .filter(d -> "PROGRAMME".equals(d.getRefDepartStatut().getCode()))
                .map(departMapper::toDTO)
                .map(this::enrichDepartDTO)
                .collect(Collectors.toList());
    }

    public List<DepartDTO> findByTrajetIdAndDateHeureAfter(Long trajetId, LocalDateTime dateHeure) {
        return departRepository.findAll().stream()
                .filter(d -> d.getTrajet().getId().equals(trajetId))
                .filter(d -> d.getDateHeureDepart().isAfter(dateHeure))
                .map(departMapper::toDTO)
                .map(this::enrichDepartDTO)
                .collect(Collectors.toList());
    }
    
    public List<DepartDTO> findByDateRange(LocalDateTime debut, LocalDateTime fin) {
        return departRepository.findAll().stream()
                .filter(d -> !d.getDateHeureDepart().isBefore(debut) && !d.getDateHeureDepart().isAfter(fin))
                .filter(d -> !"ANNULE".equals(d.getRefDepartStatut().getCode()) && 
                           !"TERMINE".equals(d.getRefDepartStatut().getCode()))
                .map(departMapper::toDTO)
                .map(this::enrichDepartDTO)
                .sorted((d1, d2) -> d1.getDateHeureDepart().compareTo(d2.getDateHeureDepart()))
                .collect(Collectors.toList());
    }

    public DepartDTO create(DepartDTO departDTO) {
        Depart depart = departMapper.toEntity(departDTO);
        
        // Charger et associer la coopérative
        Cooperative cooperative = cooperativeRepository.findById(departDTO.getCooperativeId())
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative", "id", departDTO.getCooperativeId()));
        depart.setCooperative(cooperative);
        
        // Charger et associer le trajet
        Trajet trajet = trajetRepository.findById(departDTO.getTrajetId())
                .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", departDTO.getTrajetId()));
        depart.setTrajet(trajet);
        
        // Charger et associer le véhicule
        Vehicule vehicule = vehiculeRepository.findById(departDTO.getVehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", departDTO.getVehiculeId()));
        depart.setVehicule(vehicule);
        
        // Générer le code automatiquement
        depart.setCode(genererCodeDepart(cooperative, trajet, departDTO.getDateHeureDepart()));
        
        // Lieux optionnels
        if (departDTO.getLieuDepartId() != null) {
            Lieu lieuDepart = lieuRepository.findById(departDTO.getLieuDepartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lieu", "id", departDTO.getLieuDepartId()));
            depart.setLieuDepart(lieuDepart);
        }
        
        if (departDTO.getLieuArriveeId() != null) {
            Lieu lieuArrivee = lieuRepository.findById(departDTO.getLieuArriveeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lieu", "id", departDTO.getLieuArriveeId()));
            depart.setLieuArrivee(lieuArrivee);
        }
        
        // Statut
        RefDepartStatut statut = refDepartStatutRepository.findById(departDTO.getRefDepartStatutId())
                .orElseThrow(() -> new ResourceNotFoundException("RefDepartStatut", "id", departDTO.getRefDepartStatutId()));
        depart.setRefDepartStatut(statut);
        
        // Vérifier que la date de départ est dans le futur
        if (depart.getDateHeureDepart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La date de départ doit être dans le futur");
        }
        
        Depart savedDepart = departRepository.save(depart);
        return enrichDepartDTO(departMapper.toDTO(savedDepart));
    }

    public DepartDTO update(Long id, DepartDTO departDTO) {
        Depart existingDepart = departRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", id));

        existingDepart.setCode(departDTO.getCode());
        existingDepart.setDateHeureDepart(departDTO.getDateHeureDepart());
        existingDepart.setDateHeureArriveeEstimee(departDTO.getDateHeureArriveeEstimee());
        existingDepart.setCapaciteOverride(departDTO.getCapaciteOverride());
        
        // Mettre à jour les relations si nécessaire
        if (departDTO.getCooperativeId() != null && !departDTO.getCooperativeId().equals(existingDepart.getCooperative().getId())) {
            Cooperative cooperative = cooperativeRepository.findById(departDTO.getCooperativeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cooperative", "id", departDTO.getCooperativeId()));
            existingDepart.setCooperative(cooperative);
        }
        
        if (departDTO.getTrajetId() != null && !departDTO.getTrajetId().equals(existingDepart.getTrajet().getId())) {
            Trajet trajet = trajetRepository.findById(departDTO.getTrajetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trajet", "id", departDTO.getTrajetId()));
            existingDepart.setTrajet(trajet);
        }
        
        if (departDTO.getVehiculeId() != null && !departDTO.getVehiculeId().equals(existingDepart.getVehicule().getId())) {
            Vehicule vehicule = vehiculeRepository.findById(departDTO.getVehiculeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", departDTO.getVehiculeId()));
            existingDepart.setVehicule(vehicule);
        }
        
        if (departDTO.getRefDepartStatutId() != null && !departDTO.getRefDepartStatutId().equals(existingDepart.getRefDepartStatut().getId())) {
            RefDepartStatut statut = refDepartStatutRepository.findById(departDTO.getRefDepartStatutId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefDepartStatut", "id", departDTO.getRefDepartStatutId()));
            existingDepart.setRefDepartStatut(statut);
        }

        Depart updatedDepart = departRepository.save(existingDepart);
        reservationService.recalculateConfirmedReservationsForDepart(updatedDepart.getId());
        return enrichDepartDTO(departMapper.toDTO(updatedDepart));
    }

    public void delete(Long id) {
        Depart depart = departRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", id));
        
        // Vérifier s'il y a des réservations
        Long reservationsCount = reservationRepository.findByDepartId(id).stream().count();
        if (reservationsCount > 0) {
            throw new BadRequestException("Impossible de supprimer un départ avec des réservations existantes");
        }
        
        departRepository.delete(depart);
    }

    private DepartDTO enrichDepartDTO(DepartDTO dto) {
        if (dto == null || dto.getId() == null) {
            return dto;
        }

        if (dto.getVehiculeId() != null) {
            dto.setSiegeConfigs(vehiculeSiegeConfigService.getConfigsByVehicule(dto.getVehiculeId()));
                        int totalByConfig = dto.getSiegeConfigs().stream()
                            .mapToInt(c -> Objects.requireNonNullElse(c.getNbPlaces(), 0))
                            .sum();
            if (totalByConfig > 0) {
                Integer total = dto.getCapaciteOverride() != null && dto.getCapaciteOverride() > 0
                        ? Math.min(totalByConfig, dto.getCapaciteOverride())
                        : totalByConfig;
                dto.setNombrePlaces(total);
            } else if (dto.getNombrePlaces() == null) {
                vehiculeRepository.findById(dto.getVehiculeId())
                        .ifPresent(v -> dto.setNombrePlaces(v.getNombrePlaces()));
            }
        }

        dto.setTarifsSieges(departTarifSiegeService.getTarifsByDepart(dto.getId()));
        
        // Calculer les places occupées
        Long placesOccupees = reservationRepository.countPassagersByDepartId(dto.getId());
        dto.setPlacesOccupees(placesOccupees.intValue());
        
        // Calculer les places disponibles
        Integer nombrePlaces = dto.getNombrePlaces();
        if (nombrePlaces != null) {
            dto.setPlacesDisponibles(nombrePlaces - placesOccupees.intValue());
        }
        
        // Calculer le chiffre d'affaires
        java.math.BigDecimal chiffreAffaires = paiementRepository.sumMontantByDepartId(dto.getId());
        dto.setChiffreAffaires(chiffreAffaires);

        // Calculer le montant total des diffusions publicitaires (non annulées)
        java.math.BigDecimal montantDiffusions = departPubliciteRepository.sumMontantFactureByDepartId(dto.getId());
        dto.setMontantDiffusionsPublicite(montantDiffusions);

        java.util.List<com.taxi_brousse.entity.reference.RefDevise> devisesDiffusions =
            departPubliciteRepository.findDeviseByDepartId(dto.getId(), org.springframework.data.domain.PageRequest.of(0, 1));
        if (!devisesDiffusions.isEmpty()) {
            dto.setMontantDiffusionsPubliciteDeviseCode(devisesDiffusions.get(0).getCode());
            dto.setMontantDiffusionsPubliciteDeviseSymbole(devisesDiffusions.get(0).getSymbole());
        }

        // Calculer le chiffre d'affaires par catégorie de siège
        computeChiffreAffairesParCategorie(dto);

        // Calculer le chiffre d'affaires maximal (somme des tarifs par place)
        computeChiffreAffairesMax(dto);

        // Calculer les paiements par société pour ce départ
        computePaiementsParSociete(dto);
        
        return dto;
    }

    private void computeChiffreAffairesMax(DepartDTO dto) {
        if (dto == null || dto.getId() == null) {
            return;
        }

        if (dto.getTarifsSieges() == null || dto.getTarifsSieges().isEmpty()) {
            dto.setChiffreAffairesMax(java.math.BigDecimal.ZERO);
            dto.setChiffreAffairesMaxDeviseCode(null);
            dto.setChiffreAffairesMaxDeviseSymbole(null);
            return;
        }

        // Map tarif by category
        java.util.Map<Long, com.taxi_brousse.dto.DepartTarifSiegeDTO> tarifByCategory = dto.getTarifsSieges().stream()
                .filter(t -> t.getRefSiegeCategorieId() != null)
                .collect(java.util.stream.Collectors.toMap(
                        com.taxi_brousse.dto.DepartTarifSiegeDTO::getRefSiegeCategorieId,
                        t -> t,
                        (a, b) -> a));

        int capacite = 0;
        if (dto.getVehiculeId() != null) {
            capacite = siegeConfigurationService.getTotalPlacesForVehicule(dto.getVehiculeId(), dto.getCapaciteOverride());
        }

        if (capacite <= 0 || dto.getSiegeConfigs() == null || dto.getSiegeConfigs().isEmpty()) {
            dto.setChiffreAffairesMax(java.math.BigDecimal.ZERO);
            dto.setChiffreAffairesMaxDeviseCode(null);
            dto.setChiffreAffairesMaxDeviseSymbole(null);
            return;
        }

        // Assurer l'ordre des catégories
        java.util.List<com.taxi_brousse.dto.VehiculeSiegeConfigDTO> configs = new java.util.ArrayList<>(dto.getSiegeConfigs());
        configs.sort((a, b) -> java.util.Objects.requireNonNullElse(a.getRefSiegeCategorieOrdre(), 0)
                .compareTo(java.util.Objects.requireNonNullElse(b.getRefSiegeCategorieOrdre(), 0)));

        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        String deviseCode = null;
        String deviseSymbole = null;

        int remaining = capacite;
        for (com.taxi_brousse.dto.VehiculeSiegeConfigDTO cfg : configs) {
            if (remaining <= 0) {
                break;
            }
            int nbPlaces = java.util.Objects.requireNonNullElse(cfg.getNbPlaces(), 0);
            if (nbPlaces <= 0) {
                continue;
            }
            int count = Math.min(nbPlaces, remaining);
            remaining -= count;

            com.taxi_brousse.dto.DepartTarifSiegeDTO tarif = tarifByCategory.get(cfg.getRefSiegeCategorieId());
            if (tarif != null && tarif.getMontant() != null) {
                total = total.add(tarif.getMontant().multiply(java.math.BigDecimal.valueOf(count)));

                if (deviseCode == null) {
                    deviseCode = tarif.getDeviseCode();
                    deviseSymbole = tarif.getDeviseSymbole();
                } else if (tarif.getDeviseCode() != null && !tarif.getDeviseCode().equals(deviseCode)) {
                    // Multi-devise: ne pas afficher de devise unique
                    deviseCode = null;
                    deviseSymbole = null;
                }
            }
        }

        dto.setChiffreAffairesMax(total);
        dto.setChiffreAffairesMaxDeviseCode(deviseCode);
        dto.setChiffreAffairesMaxDeviseSymbole(deviseSymbole);
    }

    private void computeChiffreAffairesParCategorie(DepartDTO dto) {
        if (dto == null || dto.getId() == null) {
            return;
        }

        List<ReservationPassager> passagers = reservationPassagerRepository.findByDepartId(dto.getId());
        java.util.Map<Long, java.util.List<ReservationPassager>> grouped = passagers.stream()
                .filter(rp -> rp.getRefSiegeCategorie() != null)
                .collect(java.util.stream.Collectors.groupingBy(rp -> rp.getRefSiegeCategorie().getId()));

        java.util.Map<Long, ChiffreAffairesCategorieDTO> statsMap = new java.util.HashMap<>();

        grouped.forEach((categorieId, list) -> {
            if (list == null || list.isEmpty()) {
                return;
            }

            ReservationPassager sample = list.get(0);
            java.math.BigDecimal total = list.stream()
                    .map(rp -> java.util.Objects.requireNonNullElse(rp.getMontantTarif(), java.math.BigDecimal.ZERO))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            String code = sample.getRefSiegeCategorie().getCode();
            String libelle = sample.getRefSiegeCategorie().getLibelle();
            String deviseCode = sample.getRefDevise() != null ? sample.getRefDevise().getCode() : null;
            String deviseSymbole = sample.getRefDevise() != null ? sample.getRefDevise().getSymbole() : null;

            statsMap.put(categorieId, new ChiffreAffairesCategorieDTO(
                    categorieId,
                    code,
                    libelle,
                    list.size(),
                    total,
                    deviseCode,
                    deviseSymbole
            ));
        });

        // Compléter les catégories sans passagers à partir des tarifs définis
        if (dto.getTarifsSieges() != null) {
            dto.getTarifsSieges().forEach(t -> {
                Long catId = t.getRefSiegeCategorieId();
                if (catId == null || statsMap.containsKey(catId)) {
                    return;
                }
                statsMap.put(catId, new ChiffreAffairesCategorieDTO(
                        catId,
                        t.getRefSiegeCategorieCode(),
                        t.getRefSiegeCategorieLibelle(),
                        0,
                        java.math.BigDecimal.ZERO,
                        t.getDeviseCode(),
                        t.getDeviseSymbole()
                ));
            });
        }

        java.util.List<ChiffreAffairesCategorieDTO> result = new java.util.ArrayList<>(statsMap.values());
        result.sort((a, b) -> java.util.Objects.requireNonNullElse(a.getRefSiegeCategorieLibelle(), "")
                .compareToIgnoreCase(java.util.Objects.requireNonNullElse(b.getRefSiegeCategorieLibelle(), "")));

        dto.setChiffreAffairesParCategorie(result);
    }

    private void computePaiementsParSociete(DepartDTO dto) {
        if (dto == null || dto.getId() == null) {
            return;
        }

        // Récupérer les sociétés ayant des diffusions pour ce départ
        java.util.List<com.taxi_brousse.entity.SocietePublicitaire> societes = 
            departPubliciteRepository.findSocietesByDepartId(dto.getId());

        java.util.List<PaiementSocieteDTO> paiementsParSociete = new java.util.ArrayList<>();
        java.math.BigDecimal totalPublicitesPaye = java.math.BigDecimal.ZERO;

        for (com.taxi_brousse.entity.SocietePublicitaire societe : societes) {
            // Montant facturé pour ce départ et cette société
            java.math.BigDecimal montantFacture = 
                departPubliciteRepository.sumMontantFactureByDepartIdAndSocieteId(dto.getId(), societe.getId());

            // Montant total payé par la société (tous départs confondus)
            java.math.BigDecimal montantTotalPaye = 
                paiementPubliciteRepository.sumMontantBySocieteId(societe.getId());

            // Montant total facturé pour la société (tous départs confondus)
            java.math.BigDecimal montantTotalFacture = 
                departPubliciteRepository.sumMontantFactureBySocieteId(societe.getId());

            // Calculer la proportion de paiement pour ce départ
            java.math.BigDecimal montantPaye = java.math.BigDecimal.ZERO;
            if (montantTotalFacture != null && montantTotalFacture.compareTo(java.math.BigDecimal.ZERO) > 0) {
                // Proportion: (montant de ce départ / montant total) * paiements totaux
                montantPaye = montantFacture
                    .multiply(montantTotalPaye != null ? montantTotalPaye : java.math.BigDecimal.ZERO)
                    .divide(montantTotalFacture, 2, java.math.RoundingMode.HALF_UP);
            }

            // Accumuler le total payé pour ce départ
            totalPublicitesPaye = totalPublicitesPaye.add(montantPaye);

            // Reste à payer pour ce départ
            java.math.BigDecimal montantRestant = montantFacture.subtract(montantPaye);

            // Devise (récupérée depuis les diffusions de la société)
            java.util.List<com.taxi_brousse.entity.reference.RefDevise> devises = 
                departPubliciteRepository.findDeviseByDepartId(dto.getId(), 
                    org.springframework.data.domain.PageRequest.of(0, 1));
            String deviseCode = "AR";
            String deviseSymbole = "Ar";
            if (!devises.isEmpty()) {
                deviseCode = devises.get(0).getCode();
                deviseSymbole = devises.get(0).getSymbole();
            }

            PaiementSocieteDTO paiementSociete = PaiementSocieteDTO.builder()
                .societeId(societe.getId())
                .societeCode(societe.getCode())
                .societeNom(societe.getNom())
                .montantFacture(montantFacture)
                .montantPaye(montantPaye)
                .montantRestant(montantRestant)
                .deviseCode(deviseCode)
                .deviseSymbole(deviseSymbole)
                .build();

            paiementsParSociete.add(paiementSociete);
        }

        dto.setPaiementsParSociete(paiementsParSociete);
        dto.setMontantPublicitesPaye(totalPublicitesPaye);
    }
    
    private String genererCodeDepart(Cooperative cooperative, Trajet trajet, LocalDateTime dateDepart) {
        String datePart = dateDepart.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String coopPart = cooperative.getNom().substring(0, Math.min(3, cooperative.getNom().length())).toUpperCase();
        String trajetPart = trajet.getLibelle().substring(0, Math.min(3, trajet.getLibelle().length())).toUpperCase();
        
        return "DEP-" + coopPart + "-" + trajetPart + "-" + datePart;
    }
}
