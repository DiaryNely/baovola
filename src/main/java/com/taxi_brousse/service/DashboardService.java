package com.taxi_brousse.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.DashboardDTO;
import com.taxi_brousse.dto.DashboardDTO.AlerteDTO;
import com.taxi_brousse.dto.DepartDTO;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.Paiement;
import com.taxi_brousse.entity.Reservation;
import com.taxi_brousse.entity.Vehicule;
import com.taxi_brousse.mapper.DepartMapper;
import com.taxi_brousse.repository.ChauffeurRepository;
import com.taxi_brousse.repository.ClientRepository;
import com.taxi_brousse.repository.CooperativeRepository;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.PaiementRepository;
import com.taxi_brousse.repository.ReservationRepository;
import com.taxi_brousse.repository.TrajetRepository;
import com.taxi_brousse.repository.VehiculeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DepartRepository departRepository;
    private final ReservationRepository reservationRepository;
    private final VehiculeRepository vehiculeRepository;
    private final PaiementRepository paiementRepository;
    private final ClientRepository clientRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final CooperativeRepository cooperativeRepository;
    private final TrajetRepository trajetRepository;
    private final DepartMapper departMapper;
    private final DepartAutomationService departAutomationService;
    private final SiegeConfigurationService siegeConfigurationService;

    @Transactional(readOnly = true)
    public DashboardDTO getDashboardStats() {
        // Mettre à jour les statuts des départs avant de charger les stats
        departAutomationService.forceUpdate();
        
        DashboardDTO dashboard = new DashboardDTO();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        
        // Début de la semaine (lundi)
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime startOfWeekTime = startOfWeek.atStartOfDay();
        
        // Début du mois
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDateTime startOfMonthTime = startOfMonth.atStartOfDay();
        
        // ============ STATISTIQUES DES DÉPARTS ============
        dashboard.setDepartsAujourdhui(countDepartsInPeriod(startOfDay, endOfDay));
        dashboard.setDepartsCetteSemaine(countDepartsInPeriod(startOfWeekTime, now));
        dashboard.setDepartsCeMois(countDepartsInPeriod(startOfMonthTime, now));
        
        // ============ STATISTIQUES DES RÉSERVATIONS ============
        List<Reservation> allReservations = reservationRepository.findAll();
        
        // Compter les réservations par statut
        long enCours = allReservations.stream()
                .filter(r -> r.getRefReservationStatut() != null && 
                           "EN_ATTENTE".equals(r.getRefReservationStatut().getCode()))
                .count();
        
        long confirmees = allReservations.stream()
                .filter(r -> r.getRefReservationStatut() != null && 
                           "CONFIRMEE".equals(r.getRefReservationStatut().getCode()))
                .count();
        
        long aujourdhui = allReservations.stream()
                .filter(r -> r.getDateCreation() != null &&
                           r.getDateCreation().isAfter(startOfDay) &&
                           r.getDateCreation().isBefore(endOfDay))
                .count();
        
        dashboard.setReservationsEnCours(enCours);
        dashboard.setReservationsConfirmees(confirmees);
        dashboard.setReservationsAujourdhui(aujourdhui);
        
        // ============ STATISTIQUES DES VÉHICULES ============
        List<Vehicule> vehicules = vehiculeRepository.findAll();
        long totalVehicules = vehicules.size();
        
        long vehiculesEnPanne = vehicules.stream()
                .filter(v -> v.getRefVehiculeEtat() != null && 
                           "EN_PANNE".equals(v.getRefVehiculeEtat().getCode()))
                .count();
        
        long vehiculesActifs = totalVehicules - vehiculesEnPanne;
        
        dashboard.setTotalVehicules(totalVehicules);
        dashboard.setVehiculesActifs(vehiculesActifs);
        dashboard.setVehiculesEnPanne(vehiculesEnPanne);
        
        // Taux de remplissage moyen
        Double tauxRemplissage = calculerTauxRemplissageMoyen();
        dashboard.setTauxRemplissageMoyen(tauxRemplissage);
        
        // ============ CHIFFRE D'AFFAIRES ============
        dashboard.setChiffreAffairesAujourdhui(calculerChiffreAffaires(startOfDay, endOfDay));
        dashboard.setChiffreAffairesSemaine(calculerChiffreAffaires(startOfWeekTime, now));
        dashboard.setChiffreAffairesMois(calculerChiffreAffaires(startOfMonthTime, now));
        
        // ============ PROCHAINS DÉPARTS (24h) ============
        LocalDateTime dans24h = now.plusHours(24);
        List<Depart> prochainsDeparts = departRepository.findAll().stream()
                .filter(d -> d.getDateHeureDepart() != null &&
                           d.getDateHeureDepart().isAfter(now) &&
                           d.getDateHeureDepart().isBefore(dans24h))
                .sorted((d1, d2) -> d1.getDateHeureDepart().compareTo(d2.getDateHeureDepart()))
                .limit(5)
                .collect(Collectors.toList());
        
        List<DepartDTO> prochainsDepartsDTO = prochainsDeparts.stream()
                .map(departMapper::toDTO)
                .collect(Collectors.toList());
        dashboard.setProchainsDeparts(prochainsDepartsDTO);
        
        // ============ ALERTES ============
        List<AlerteDTO> alertes = genererAlertes(vehiculesEnPanne, prochainsDeparts);
        dashboard.setAlertes(alertes);
        
        // ============ STATISTIQUES GÉNÉRALES ============
        dashboard.setTotalClients(clientRepository.count());
        dashboard.setTotalChauffeurs(chauffeurRepository.count());
        dashboard.setTotalCooperatives(cooperativeRepository.count());
        dashboard.setTotalTrajets(trajetRepository.count());
        
        return dashboard;
    }
    
    private Long countDepartsInPeriod(LocalDateTime start, LocalDateTime end) {
        return departRepository.findAll().stream()
                .filter(d -> d.getDateHeureDepart() != null &&
                           d.getDateHeureDepart().isAfter(start) &&
                           d.getDateHeureDepart().isBefore(end))
                .count();
    }
    
    private BigDecimal calculerChiffreAffaires(LocalDateTime start, LocalDateTime end) {
        List<Paiement> paiements = paiementRepository.findAll().stream()
                .filter(p -> p.getDatePaiement() != null &&
                           p.getDatePaiement().isAfter(start) &&
                           p.getDatePaiement().isBefore(end) &&
                           p.getRefPaiementStatut() != null &&
                           "VALIDE".equals(p.getRefPaiementStatut().getCode()))
                .collect(Collectors.toList());
        
        return paiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private Double calculerTauxRemplissageMoyen() {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime debutJour = maintenant.toLocalDate().atStartOfDay();
        
        // Départs d'aujourd'hui
        List<Depart> departsAujourdhui = departRepository.findAll().stream()
                .filter(d -> d.getDateHeureDepart() != null &&
                           d.getDateHeureDepart().isAfter(debutJour) &&
                           d.getDateHeureDepart().isBefore(maintenant.plusDays(1)))
                .collect(Collectors.toList());
        
        if (departsAujourdhui.isEmpty()) {
            return 0.0;
        }
        
        double totalTaux = 0.0;
        int count = 0;
        
        for (Depart depart : departsAujourdhui) {
            if (depart.getVehicule() != null && depart.getVehicule().getNombrePlaces() != null) {
                int capacite = siegeConfigurationService.getTotalPlacesForVehicule(
                        depart.getVehicule().getId(), depart.getCapaciteOverride());
                if (capacite == 0) {
                    capacite = depart.getVehicule().getNombrePlaces();
                }
                
                // Compter les réservations pour ce départ
                long nbReservations = reservationRepository.findAll().stream()
                        .filter(r -> r.getDepart() != null && r.getDepart().getId().equals(depart.getId()))
                        .count();
                
                double taux = (double) nbReservations / capacite * 100.0;
                totalTaux += taux;
                count++;
            }
        }
        
        return count > 0 ? totalTaux / count : 0.0;
    }
    
    private List<AlerteDTO> genererAlertes(long vehiculesEnPanne, List<Depart> prochainsDeparts) {
        List<AlerteDTO> alertes = new ArrayList<>();
        
        // Alerte véhicules en panne
        if (vehiculesEnPanne > 0) {
            AlerteDTO alerte = new AlerteDTO();
            alerte.setType("danger");
            alerte.setTitre("Véhicules en panne");
            alerte.setMessage(vehiculesEnPanne + " véhicule(s) en panne nécessitent une attention");
            alerte.setLien("/vehicules");
            alertes.add(alerte);
        }
        
        // Alerte départs complets
        for (Depart depart : prochainsDeparts) {
            if (depart.getVehicule() != null && depart.getVehicule().getNombrePlaces() != null) {
                int capacite = siegeConfigurationService.getTotalPlacesForVehicule(
                        depart.getVehicule().getId(), depart.getCapaciteOverride());
                if (capacite == 0) {
                    capacite = depart.getVehicule().getNombrePlaces();
                }
                
                long nbReservations = reservationRepository.findAll().stream()
                        .filter(r -> r.getDepart() != null && r.getDepart().getId().equals(depart.getId()))
                        .count();
                
                if (nbReservations >= capacite) {
                    AlerteDTO alerte = new AlerteDTO();
                    alerte.setType("info");
                    alerte.setTitre("Départ complet");
                    alerte.setMessage("Départ " + depart.getCode() + " est complet (" + nbReservations + "/" + capacite + ")");
                    alerte.setLien("/departs");
                    alertes.add(alerte);
                }
            }
        }
        
        // Alerte départs proches sans réservations
        LocalDateTime dans2h = LocalDateTime.now().plusHours(2);
        for (Depart depart : prochainsDeparts) {
            if (depart.getDateHeureDepart() != null && depart.getDateHeureDepart().isBefore(dans2h)) {
                long nbReservations = reservationRepository.findAll().stream()
                        .filter(r -> r.getDepart() != null && r.getDepart().getId().equals(depart.getId()))
                        .count();
                
                if (nbReservations == 0) {
                    AlerteDTO alerte = new AlerteDTO();
                    alerte.setType("warning");
                    alerte.setTitre("Départ sans réservation");
                    alerte.setMessage("Départ " + depart.getCode() + " dans moins de 2h sans réservations");
                    alerte.setLien("/departs");
                    alertes.add(alerte);
                }
            }
        }
        
        return alertes;
    }
}
