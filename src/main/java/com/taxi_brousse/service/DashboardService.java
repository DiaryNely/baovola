package com.taxi_brousse.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.DashboardDTO;
import com.taxi_brousse.dto.DashboardDTO.AlerteDTO;
import com.taxi_brousse.dto.DepartDTO;
import com.taxi_brousse.dto.RentabiliteTrajetDTO;
import com.taxi_brousse.dto.RevenuMensuelDTO;
import com.taxi_brousse.dto.StatistiquesFinancieresDTO;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.Reservation;
import com.taxi_brousse.entity.Vehicule;
import com.taxi_brousse.entity.VehiculeSiegeConfig;
import com.taxi_brousse.mapper.DepartMapper;
import com.taxi_brousse.repository.ChauffeurRepository;
import com.taxi_brousse.repository.ClientRepository;
import com.taxi_brousse.repository.CooperativeRepository;
import com.taxi_brousse.repository.DepartPubliciteRepository;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.PaiementPubliciteRepository;
import com.taxi_brousse.repository.PaiementRepository;
import com.taxi_brousse.repository.ReservationPassagerRepository;
import com.taxi_brousse.repository.ReservationRepository;
import com.taxi_brousse.repository.TrajetRepository;
import com.taxi_brousse.repository.VehiculeRepository;
import com.taxi_brousse.repository.VehiculeSiegeConfigRepository;

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
    private final ReservationPassagerRepository reservationPassagerRepository;
    private final PaiementPubliciteRepository paiementPubliciteRepository;
    private final DepartPubliciteRepository departPubliciteRepository;
    private final VehiculeSiegeConfigRepository vehiculeSiegeConfigRepository;

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
        
        // ============ STATISTIQUES FINANCIÈRES ============
        // Période : 6 derniers mois par défaut
        LocalDateTime dateDebutFinancier = now.minusMonths(6).withDayOfMonth(1).toLocalDate().atStartOfDay();
        StatistiquesFinancieresDTO statsFinancieres = getStatistiquesFinancieres(dateDebutFinancier, now);
        dashboard.setStatsFinancieres(statsFinancieres);
        
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
        // Utiliser la même logique que getStatistiquesFinancieres : basé sur dateHeureDepart
        // CA = montants payés des réservations dont le départ est dans la période
        BigDecimal revenusReservations = reservationRepository.sumMontantPayeByDateRange(start, end);
        
        // Ajouter les revenus publicités pour la période
        BigDecimal revenusPublicites = paiementPubliciteRepository.sumMontantByDateRange(start, end);
        
        return revenusReservations.add(revenusPublicites);
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
                
                // Compter les PASSAGERS (pas les réservations) pour ce départ
                Long nbPassagers = reservationRepository.countPassagersByDepartId(depart.getId());
                
                double taux = (double) nbPassagers / capacite * 100.0;
                totalTaux += taux;
                count++;
            }
        }
        
        return count > 0 ? BigDecimal.valueOf(totalTaux / count)
                .setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0;
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
                
                // Compter les PASSAGERS (pas les réservations) pour ce départ
                Long nbPassagers = reservationRepository.countPassagersByDepartId(depart.getId());
                
                if (nbPassagers >= capacite) {
                    AlerteDTO alerte = new AlerteDTO();
                    alerte.setType("info");
                    alerte.setTitre("Départ complet");
                    alerte.setMessage("Départ " + depart.getCode() + " est complet (" + nbPassagers + "/" + capacite + ")");
                    alerte.setLien("/departs");
                    alertes.add(alerte);
                }
            }
        }
        
        // Alerte départs proches sans réservations
        LocalDateTime dans2h = LocalDateTime.now().plusHours(2);
        for (Depart depart : prochainsDeparts) {
            if (depart.getDateHeureDepart() != null && depart.getDateHeureDepart().isBefore(dans2h)) {
                Long nbPassagers = reservationRepository.countPassagersByDepartId(depart.getId());
                
                if (nbPassagers == 0) {
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

    // ===== Méthodes pour Dashboard Financier =====

    /**
     * Génère les statistiques financières complètes pour une période donnée
     */
    public StatistiquesFinancieresDTO getStatistiquesFinancieres(LocalDateTime dateDebut, LocalDateTime dateFin) {
        // Récupérer les départs de la période
        List<Depart> departs = departRepository.findByDateRange(dateDebut, dateFin);
        List<Long> departIds = departs.stream().map(Depart::getId).collect(Collectors.toList());

        // Revenus réservations
        BigDecimal revenusReservations = reservationRepository.sumMontantPayeByDateRange(dateDebut, dateFin);
        Long nombreReservations = reservationRepository.countConfirmedReservationsByDateRange(dateDebut, dateFin);

        // Revenus publicités (basé sur date de paiement)
        BigDecimal revenusPublicites = paiementPubliciteRepository.sumMontantByDateRange(dateDebut, dateFin);

        // Revenus total
        BigDecimal revenusTotal = revenusReservations.add(revenusPublicites);

        // Taux de remplissage moyen
        Double tauxRemplissageMoyen = calculateTauxRemplissageMoyen(departs);

        // Statistiques par statut
        Long departsEnCours = departRepository.countByStatutAndDateRange("EN_COURS", dateDebut, dateFin);
        Long departsTermines = departRepository.countByStatutAndDateRange("TERMINE", dateDebut, dateFin);
        Long departsAnnules = departRepository.countByStatutAndDateRange("ANNULE", dateDebut, dateFin);
        Long deprogrammes = departRepository.countByStatutAndDateRange("PROGRAMME", dateDebut, dateFin);

        // Évolution mensuelle
        List<RevenuMensuelDTO> revenusMensuels = calculateRevenusMensuels(dateDebut, dateFin);

        // Rentabilité par trajet
        List<RentabiliteTrajetDTO> rentabiliteParTrajet = calculateRentabiliteParTrajet(dateDebut, dateFin, departs);

        // Top 5 trajets
        List<RentabiliteTrajetDTO> top5Trajets = rentabiliteParTrajet.stream()
                .sorted((a, b) -> b.getRevenusTotal().compareTo(a.getRevenusTotal()))
                .limit(5)
                .collect(Collectors.toList());

        return StatistiquesFinancieresDTO.builder()
                .revenusTotal(revenusTotal)
                .revenusReservations(revenusReservations)
                .revenusPublicites(revenusPublicites)
                .nombreDeparts(departs.size())
                .nombreReservations(nombreReservations.intValue())
                .tauxRemplissageMoyen(tauxRemplissageMoyen)
                .revenusMensuels(revenusMensuels)
                .rentabiliteParTrajet(rentabiliteParTrajet)
                .top5Trajets(top5Trajets)
                .departsEnCours(departsEnCours)
                .departsTermines(departsTermines)
                .departsAnnules(departsAnnules)
                .deprogrammes(deprogrammes)
                .build();
    }

    private Double calculateTauxRemplissageMoyen(List<Depart> departs) {
        if (departs.isEmpty()) return 0.0;

        double totalTaux = 0.0;
        int count = 0;

        for (Depart depart : departs) {
            Integer capacite = getCapaciteEffective(depart);
            if (capacite != null && capacite > 0) {
                Long passagers = reservationRepository.countPassagersByDepartId(depart.getId());
                double taux = (passagers.doubleValue() / capacite) * 100;
                totalTaux += taux;
                count++;
            }
        }

        return count > 0 ? BigDecimal.valueOf(totalTaux / count)
                .setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0;
    }

    private Integer getCapaciteEffective(Depart depart) {
        if (depart.getVehicule() != null) {
            // Utiliser capaciteOverride si défini
            if (depart.getCapaciteOverride() != null && depart.getCapaciteOverride() > 0) {
                return depart.getCapaciteOverride();
            }
            // Sinon utiliser la configuration des sièges
            List<VehiculeSiegeConfig> sieges = vehiculeSiegeConfigRepository
                    .findByVehiculeIdOrderByRefSiegeCategorieOrdreAsc(depart.getVehicule().getId());
            if (!sieges.isEmpty()) {
                return sieges.size();
            }
            // Fallback: utiliser nombrePlaces du véhicule
            return depart.getVehicule().getNombrePlaces();
        }
        return 0;
    }

    private List<RevenuMensuelDTO> calculateRevenusMensuels(LocalDateTime dateDebut, LocalDateTime dateFin) {
        List<RevenuMensuelDTO> mensuels = new ArrayList<>();
        List<Object[]> departsParMois = departRepository.findDepartsGroupByMonth(dateDebut, dateFin);
        
        for (Object[]row : departsParMois) {
            Integer annee = ((Number) row[0]).intValue();
            Integer mois = ((Number) row[1]).intValue();
            Long nombreDeparts = ((Number) row[2]).longValue();

            LocalDateTime debutMois = LocalDateTime.of(annee, mois, 1, 0, 0);
            LocalDateTime finMois = debutMois.plusMonths(1).minusSeconds(1);
            
            List<Depart> departsMois = departRepository.findByDateRange(debutMois, finMois);
            BigDecimal revenusReservations = reservationRepository.sumMontantPayeByDateRange(debutMois, finMois);
            BigDecimal revenusPublicites = paiementPubliciteRepository.sumMontantByDateRange(debutMois, finMois);
            Double tauxRemplissage = calculateTauxRemplissageMoyen(departsMois);
            Long nbReservations = reservationRepository.countConfirmedReservationsByDateRange(debutMois, finMois);

            String moisLabel = java.time.Month.of(mois).getDisplayName(TextStyle.FULL, new Locale("fr", "FR"))
                    + " " + annee;

            mensuels.add(RevenuMensuelDTO.builder()
                    .annee(annee)
                    .mois(mois)
                    .moisLabel(moisLabel)
                    .revenusTotal(revenusReservations.add(revenusPublicites))
                    .revenusReservations(revenusReservations)
                    .revenusPublicites(revenusPublicites)
                    .nombreDeparts(nombreDeparts.intValue())
                    .nombreReservations(nbReservations.intValue())
                    .tauxRemplissage(tauxRemplissage)
                    .build());
        }
        
        return mensuels;
    }

    private List<RentabiliteTrajetDTO> calculateRentabiliteParTrajet(
            LocalDateTime dateDebut, LocalDateTime dateFin, List<Depart> departs) {
        
        Map<Long, List<Depart>> departsByTrajet = departs.stream()
                .filter(d -> d.getTrajet() != null)
                .collect(Collectors.groupingBy(d -> d.getTrajet().getId()));

        List<RentabiliteTrajetDTO> rentabilites = new ArrayList<>();

        for (Map.Entry<Long, List<Depart>> entry : departsByTrajet.entrySet()) {
            List<Depart> departsTrajet = entry.getValue();
            Depart premierDepart = departsTrajet.get(0);
            List<Long> departIds = departsTrajet.stream().map(Depart::getId).collect(Collectors.toList());

            Map<Long, BigDecimal> revenusParDepart = new HashMap<>();
            if (!departIds.isEmpty()) {
                List<Object[]> results = reservationRepository.sumMontantPayeGroupByDepart(departIds);
                for (Object[] row : results) {
                    revenusParDepart.put(((Number) row[0]).longValue(), (BigDecimal) row[1]);
                }
            }
            BigDecimal revenusReservations = revenusParDepart.values().stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal revenusPublicites = BigDecimal.ZERO;
            for (Long departId : departIds) {
                BigDecimal montant = departPubliciteRepository.sumMontantPayeByDepartId(departId);
                if (montant != null) {
                    revenusPublicites = revenusPublicites.add(montant);
                }
            }

            Map<Long, Long> reservationsParDepart = new HashMap<>();
            if (!departIds.isEmpty()) {
                List<Object[]> results = reservationRepository.countReservationsGroupByDepart(departIds);
                for (Object[] row : results) {
                    reservationsParDepart.put(((Number) row[0]).longValue(), ((Number) row[1]).longValue());
                }
            }
            Integer nombreReservations = reservationsParDepart.values().stream()
                    .mapToInt(Long::intValue).sum();

            Double tauxRemplissage = calculateTauxRemplissageMoyen(departsTrajet);
            BigDecimal revenusTotal = revenusReservations.add(revenusPublicites);
            BigDecimal revenuMoyen = departsTrajet.size() > 0 
                    ? revenusTotal.divide(BigDecimal.valueOf(departsTrajet.size()), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            String itineraire = premierDepart.getLieuDepart().getNom() + " → " + 
                    premierDepart.getLieuArrivee().getNom();

            rentabilites.add(RentabiliteTrajetDTO.builder()
                    .trajetId(premierDepart.getTrajet().getId())
                    .trajetCode(premierDepart.getTrajet().getCode())
                    .lieuDepartNom(premierDepart.getLieuDepart().getNom())
                    .lieuArriveeNom(premierDepart.getLieuArrivee().getNom())
                    .itineraire(itineraire)
                    .nombreDeparts(departsTrajet.size())
                    .nombreReservations(nombreReservations)
                    .revenusTotal(revenusTotal)
                    .revenusReservations(revenusReservations)
                    .revenusPublicites(revenusPublicites)
                    .tauxRemplissageMoyen(tauxRemplissage)
                    .revenuMoyenParDepart(revenuMoyen)
                    .build());
        }

        return rentabilites.stream()
                .sorted((a, b) -> b.getRevenusTotal().compareTo(a.getRevenusTotal()))
                .collect(Collectors.toList());
    }
}

