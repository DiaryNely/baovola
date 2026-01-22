package com.taxi_brousse.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taxi_brousse.dto.DepartDTO;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.repository.ChauffeurRepository;
import com.taxi_brousse.repository.ClientRepository;
import com.taxi_brousse.repository.CooperativeRepository;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.LieuRepository;
import com.taxi_brousse.repository.PaiementRepository;
import com.taxi_brousse.repository.RefDepartStatutRepository;
import com.taxi_brousse.repository.ReservationRepository;
import com.taxi_brousse.repository.TrajetRepository;
import com.taxi_brousse.repository.VehiculeRepository;
import com.taxi_brousse.service.DepartService;

@Controller
public class GestionController {

    @Autowired
    private LieuRepository lieuRepository;
    
    @Autowired
    private CooperativeRepository cooperativeRepository;
    
    @Autowired
    private TrajetRepository trajetRepository;
    
    @Autowired
    private VehiculeRepository vehiculeRepository;
    
    @Autowired
    private ChauffeurRepository chauffeurRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private DepartRepository departRepository;
    
    @Autowired
    private DepartService departService;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private PaiementRepository paiementRepository;
    
    
    
    
    @Autowired
    private RefDepartStatutRepository refDepartStatutRepository;

    // Liste des Lieux
    @GetMapping("/lieux")
    public String listLieux(Model model) {
        model.addAttribute("lieux", lieuRepository.findAll());
        return "taxi_brousse/lieux";
    }

    // Liste des Coopératives
    @GetMapping("/cooperatives")
    public String listCooperatives(Model model) {
        model.addAttribute("cooperatives", cooperativeRepository.findAll());
        return "taxi_brousse/cooperatives";
    }

    // Liste des Trajets
    @GetMapping("/trajets")
    public String listTrajets(Model model) {
        model.addAttribute("trajets", trajetRepository.findAll());
        return "taxi_brousse/trajets";
    }

    // Liste des Véhicules
    @GetMapping("/vehicules")
    public String listVehicules(Model model) {
        model.addAttribute("vehicules", vehiculeRepository.findAll());
        return "taxi_brousse/vehicules";
    }

    // Liste des Chauffeurs
    @GetMapping("/chauffeurs")
    public String listChauffeurs(Model model) {
        model.addAttribute("chauffeurs", chauffeurRepository.findAll());
        return "taxi_brousse/chauffeurs";
    }

    // Liste des Clients
    @GetMapping("/clients")
    public String listClients(Model model) {
        model.addAttribute("clients", clientRepository.findAll());
        return "taxi_brousse/clients";
    }

    // Liste et Recherche des Départs
    @GetMapping("/departs")
    public String listDeparts(
            @RequestParam(required = false) Long lieuDepartId,
            @RequestParam(required = false) Long lieuArriveeId,
            @RequestParam(required = false) Long cooperativeId,
            @RequestParam(required = false) Long trajetId,
            @RequestParam(required = false) Long statutId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            Model model) {
        
        List<DepartDTO> departs;
        
        // Si au moins un critère de recherche est fourni, effectuer la recherche
        if (lieuDepartId != null || lieuArriveeId != null || cooperativeId != null || 
            trajetId != null || statutId != null || dateDebut != null || dateFin != null) {
            
            // Conversion des null en 0L pour les IDs
            Long lieuDepartIdParam = (lieuDepartId != null && lieuDepartId > 0) ? lieuDepartId : 0L;
            Long lieuArriveeIdParam = (lieuArriveeId != null && lieuArriveeId > 0) ? lieuArriveeId : 0L;
            Long cooperativeIdParam = (cooperativeId != null && cooperativeId > 0) ? cooperativeId : 0L;
            Long trajetIdParam = (trajetId != null && trajetId > 0) ? trajetId : 0L;
            Long statutIdParam = (statutId != null && statutId > 0) ? statutId : 0L;
            
            // Conversion des dates en LocalDateTime
            LocalDateTime dateDebutTime = dateDebut != null ? dateDebut.atStartOfDay() : null;
            LocalDateTime dateFinTime = dateFin != null ? dateFin.atTime(LocalTime.MAX) : null;
            
            // Exécution de la recherche via repository puis conversion en DTO
            List<Depart> departEntities = departRepository.searchDeparts(
                    lieuDepartIdParam, 
                    lieuArriveeIdParam, 
                    cooperativeIdParam, 
                    trajetIdParam, 
                    statutIdParam, 
                    dateDebutTime, 
                    dateFinTime
            );
            // Convertir les entités en DTOs enrichis
            departs = departEntities.stream()
                    .map(d -> departService.findById(d.getId()))
                    .collect(java.util.stream.Collectors.toList());
        } else {
            // Aucun critère de recherche, utiliser le service qui retourne des DTOs enrichis
            departs = departService.findAll();
        }
        
        model.addAttribute("departs", departs);
        model.addAttribute("lieux", lieuRepository.findAll());
        model.addAttribute("cooperatives", cooperativeRepository.findAll());
        model.addAttribute("trajets", trajetRepository.findAll());
        model.addAttribute("statuts", refDepartStatutRepository.findAll());
        return "taxi_brousse/departs";
    }

    // Réservations avec critères (page principale)
    @GetMapping("/reservations")
    public String listReservations(Model model) {
        model.addAttribute("reservations", reservationRepository.findAll());
        model.addAttribute("lieux", lieuRepository.findAll());
        return "taxi_brousse/reservation-critere";
    }
    
    @GetMapping("/reservations-liste")
    public String listReservationsTable(Model model) {
        model.addAttribute("reservations", reservationRepository.findAll());
        return "taxi_brousse/reservations-liste";
    }

    @GetMapping("/reservations-critere")
    public String listReservationsCritere(Model model) {
        // Redirection vers /reservations
        return "redirect:/reservations";
    }

    // Liste des Paiements
    @GetMapping("/paiements")
    public String listPaiements(Model model) {
        model.addAttribute("paiements", paiementRepository.findAll());
        return "taxi_brousse/paiements";
    }

    // Note: /billets route is now handled by BilletController

    @GetMapping("/tarifs-sieges")
    public String listTarifsSieges(Model model) {
        return "taxi_brousse/tarifs-sieges";
    }

    @GetMapping("/tarifs-remises")
    public String listTarifsRemises(Model model) {
        return "taxi_brousse/tarifs-remises";
    }

    @GetMapping("/statistiques-publicite")
    public String statistiquesPublicite(Model model) {
        return "taxi_brousse/statistiques-publicite";
    }

    @GetMapping("/paiements-publicite")
    public String paiementsPublicite(Model model) {
        return "taxi_brousse/paiements-publicite";
    }

    @GetMapping("/paiement-publicite")
    public String paiementPublicite(Model model) {
        return "taxi_brousse/paiement-publicite";
    }

    @GetMapping("/siege-categories")
    public String listSiegeCategories(Model model) {
        return "taxi_brousse/siege-categories";
    }

    @GetMapping("/tarifs-publicite")
    public String listTarifsPublicite(Model model) {
        return "taxi_brousse/tarifs-publicite";
    }

    @GetMapping("/diffusions-publicite")
    public String listDiffusionsPublicite(Model model) {
        return "taxi_brousse/diffusions-publicite";
    }
}
