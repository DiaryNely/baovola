package com.taxi_brousse.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.reference.RefDepartStatut;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.RefDepartStatutRepository;
import com.taxi_brousse.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartAutomationService {

    private final DepartRepository departRepository;
    private final RefDepartStatutRepository refDepartStatutRepository;
    private final ReservationRepository reservationRepository;
    private final SiegeConfigurationService siegeConfigurationService;

    /**
     * Exécuté toutes les 5 minutes pour mettre à jour automatiquement les statuts des départs
     */
    @Scheduled(fixedDelay = 300000) // 5 minutes en millisecondes
    @Transactional
    public void updateDepartStatuses() {
        log.info("Début de la mise à jour automatique des statuts de départs");
        
        int departsTermines = updateDepartsPassesToTermine();
        int departsComplets = updateDepartsToComplet();
        int departsEnCours = updateDepartsToEnCours();
        
        log.info("Mise à jour terminée: {} départs terminés, {} départs complets, {} départs en cours", 
                departsTermines, departsComplets, departsEnCours);
    }

    /**
     * Passe les départs dont la date est dépassée au statut TERMINE
     * @return nombre de départs mis à jour
     */
    private int updateDepartsPassesToTermine() {
        RefDepartStatut statutTermine = refDepartStatutRepository.findByCode("TERMINE")
                .orElseThrow(() -> new RuntimeException("Statut TERMINE non trouvé"));
        
        // Trouver tous les départs dont la date est passée et qui ne sont pas déjà terminés ou annulés
        List<Depart> departsAPasser = departRepository.findAll().stream()
                .filter(d -> {
                    String code = d.getRefDepartStatut().getCode();
                    return !code.equals("TERMINE") && !code.equals("ANNULE");
                })
                .filter(d -> {
                    LocalDateTime dateDepart = d.getDateHeureDepart();
                    LocalDateTime dateArrivee = d.getDateHeureArriveeEstimee();
                    LocalDateTime now = LocalDateTime.now();
                    
                    // Si date d'arrivée existe, comparer avec elle
                    // Sinon, comparer avec la date de départ
                    if (dateArrivee != null) {
                        return dateArrivee.isBefore(now);
                    } else {
                        return dateDepart.isBefore(now);
                    }
                })
                .toList();
        
        for (Depart depart : departsAPasser) {
            depart.setRefDepartStatut(statutTermine);
            departRepository.save(depart);
            log.debug("Départ {} passé au statut TERMINE", depart.getCode());
        }
        
        return departsAPasser.size();
    }

    /**
     * Passe les départs au statut COMPLET si toutes les places sont réservées
     * @return nombre de départs mis à jour
     */
    private int updateDepartsToComplet() {
        RefDepartStatut statutComplet = refDepartStatutRepository.findByCode("COMPLET")
                .orElse(null);
        
        if (statutComplet == null) {
            log.warn("Statut COMPLET non trouvé dans la base de données");
            return 0;
        }
        
        // Trouver tous les départs programmés ou en cours
        List<Depart> departsActifs = departRepository.findAll().stream()
                .filter(d -> {
                    String code = d.getRefDepartStatut().getCode();
                    return code.equals("PROGRAMME") || code.equals("EN_COURS");
                })
                .filter(d -> d.getDateHeureDepart().isAfter(LocalDateTime.now()))
                .toList();
        
        int count = 0;
        for (Depart depart : departsActifs) {
            // Calculer la capacité effective
                Integer capacite = siegeConfigurationService.getTotalPlacesForVehicule(
                    depart.getVehicule().getId(), depart.getCapaciteOverride());
                if (capacite == 0) {
                capacite = depart.getVehicule().getNombrePlaces();
                }
            
            // Compter les réservations non annulées
            Long nbReservations = reservationRepository.countPassagersByDepartId(depart.getId());
            
            // Si toutes les places sont prises, passer à COMPLET
            if (nbReservations >= capacite) {
                depart.setRefDepartStatut(statutComplet);
                departRepository.save(depart);
                log.debug("Départ {} passé au statut COMPLET ({}/{})", depart.getCode(), nbReservations, capacite);
                count++;
            }
        }
        
        return count;
    }

    /**
     * Passe les départs au statut EN_COURS si la date de départ est atteinte
     * @return nombre de départs mis à jour
     */
    private int updateDepartsToEnCours() {
        RefDepartStatut statutEnCours = refDepartStatutRepository.findByCode("EN_COURS")
                .orElse(null);
        
        if (statutEnCours == null) {
            log.warn("Statut EN_COURS non trouvé dans la base de données");
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // Trouver tous les départs programmés ou complets dont la date est atteinte
        List<Depart> departsADemarrer = departRepository.findAll().stream()
                .filter(d -> {
                    String code = d.getRefDepartStatut().getCode();
                    return code.equals("PROGRAMME") || code.equals("COMPLET");
                })
                .filter(d -> {
                    LocalDateTime dateDepart = d.getDateHeureDepart();
                    // Le départ est en cours si on est entre la date de départ et la date d'arrivée
                    if (d.getDateHeureArriveeEstimee() != null) {
                        return !dateDepart.isAfter(now) && d.getDateHeureArriveeEstimee().isAfter(now);
                    } else {
                        // Si pas de date d'arrivée, considérer en cours si date départ est passée dans les 24h
                        return !dateDepart.isAfter(now) && dateDepart.plusHours(24).isAfter(now);
                    }
                })
                .toList();
        
        for (Depart depart : departsADemarrer) {
            depart.setRefDepartStatut(statutEnCours);
            departRepository.save(depart);
            log.debug("Départ {} passé au statut EN_COURS", depart.getCode());
        }
        
        return departsADemarrer.size();
    }

    /**
     * Méthode publique pour forcer une mise à jour immédiate
     * Peut être appelée manuellement ou après certaines opérations
     */
    @Transactional
    public void forceUpdate() {
        log.info("Mise à jour forcée des statuts de départs");
        updateDepartStatuses();
    }
}
