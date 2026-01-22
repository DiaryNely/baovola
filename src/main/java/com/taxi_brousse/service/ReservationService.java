package com.taxi_brousse.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.CreerReservationRequest;
import com.taxi_brousse.dto.PaiementRequest;
import com.taxi_brousse.dto.ReservationDTO;
import com.taxi_brousse.dto.ReservationPassagerDTO;
import com.taxi_brousse.dto.SeatInfoDTO;
import com.taxi_brousse.entity.Billet;
import com.taxi_brousse.entity.Client;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.DepartTarifRemise;
import com.taxi_brousse.entity.DepartTarifSiege;
import com.taxi_brousse.entity.Paiement;
import com.taxi_brousse.entity.Reservation;
import com.taxi_brousse.entity.ReservationPassager;
import com.taxi_brousse.entity.reference.RefBilletStatut;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefPaiementMethode;
import com.taxi_brousse.entity.reference.RefPaiementStatut;
import com.taxi_brousse.entity.reference.RefPassagerCategorie;
import com.taxi_brousse.entity.reference.RefReservationStatut;
import com.taxi_brousse.entity.reference.RefSiegeCategorie;
import com.taxi_brousse.exception.BadRequestException;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.ReservationMapper;
import com.taxi_brousse.mapper.ReservationPassagerMapper;
import com.taxi_brousse.repository.BilletRepository;
import com.taxi_brousse.repository.ClientRepository;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.DepartTarifRemiseRepository;
import com.taxi_brousse.repository.DepartTarifSiegeRepository;
import com.taxi_brousse.repository.PaiementRepository;
import com.taxi_brousse.repository.RefBilletStatutRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import com.taxi_brousse.repository.RefPaiementMethodeRepository;
import com.taxi_brousse.repository.RefPaiementStatutRepository;
import com.taxi_brousse.repository.RefPassagerCategorieRepository;
import com.taxi_brousse.repository.RefReservationStatutRepository;
import com.taxi_brousse.repository.ReservationPassagerRepository;
import com.taxi_brousse.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationPassagerRepository reservationPassagerRepository;
    private final ClientRepository clientRepository;
    private final DepartRepository departRepository;
    private final RefReservationStatutRepository refReservationStatutRepository;
    private final PaiementRepository paiementRepository;
    private final BilletRepository billetRepository;
    private final RefBilletStatutRepository refBilletStatutRepository;
    private final RefDeviseRepository refDeviseRepository;
    private final RefPaiementMethodeRepository refPaiementMethodeRepository;
    private final RefPaiementStatutRepository refPaiementStatutRepository;
    private final DepartTarifSiegeRepository departTarifSiegeRepository;
    private final DepartTarifRemiseRepository departTarifRemiseRepository;
    private final SiegeConfigurationService siegeConfigurationService;
    private final RefPassagerCategorieRepository refPassagerCategorieRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationPassagerMapper reservationPassagerMapper;

    public List<ReservationDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDTO)
                .map(this::enrichReservationDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        return enrichReservationDTO(reservationMapper.toDTO(reservation));
    }

    public List<ReservationDTO> findByClientId(Long clientId) {
        return reservationRepository.findByClientId(clientId).stream()
                .map(reservationMapper::toDTO)
                .map(this::enrichReservationDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> findByDepartId(Long departId) {
        return reservationRepository.findByDepartId(departId).stream()
                .map(reservationMapper::toDTO)
                .map(this::enrichReservationDTO)
                .collect(Collectors.toList());
    }

    public void recalculateConfirmedReservationsForDepart(Long departId) {
        if (departId == null) {
            return;
        }

        List<Reservation> reservations = reservationRepository.findByDepartId(departId).stream()
                .filter(r -> r.getRefReservationStatut() != null)
                .filter(r -> !"ANNULE".equalsIgnoreCase(r.getRefReservationStatut().getCode()))
                .toList();
        if (reservations.isEmpty()) {
            return;
        }

        Depart depart = departRepository.findById(departId)
            .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", departId));

        Map<Integer, RefSiegeCategorie> seatCategories = siegeConfigurationService.buildSeatCategoryMap(depart);
        Map<Long, Map<Long, DepartTarifRemise>> remisesByCategorie = loadRemisesByDepart(departId);

        Map<Long, DepartTarifSiege> tarifsByCategorie = departTarifSiegeRepository.findByDepartId(departId).stream()
                .filter(t -> t.getRefSiegeCategorie() != null)
                .collect(Collectors.toMap(
                        t -> t.getRefSiegeCategorie().getId(),
                        t -> t,
                        (a, b) -> a,
                        HashMap::new));

        for (Reservation reservation : reservations) {
            List<ReservationPassager> passagers = reservationPassagerRepository.findByReservationId(reservation.getId());
            BigDecimal total = BigDecimal.ZERO;

            for (ReservationPassager passager : passagers) {
                RefSiegeCategorie categorie = null;
                if (passager.getNumeroSiege() != null) {
                    categorie = seatCategories.get(passager.getNumeroSiege());
                }
                if (categorie == null) {
                    categorie = passager.getRefSiegeCategorie();
                }
                if (categorie == null) {
                    continue;
                }

                DepartTarifSiege tarifSiege = tarifsByCategorie.get(categorie.getId());
                if (tarifSiege == null || tarifSiege.getMontant() == null) {
                    continue;
                }

                RefPassagerCategorie passagerCategorie = passager.getRefPassagerCategorie();
                if (passagerCategorie == null) {
                    continue;
                }

                BigDecimal montantFinal = applyRemise(tarifSiege.getMontant(), categorie.getId(), passagerCategorie.getId(), remisesByCategorie);
                BigDecimal montantRemise = tarifSiege.getMontant().subtract(montantFinal);

                passager.setRefSiegeCategorie(categorie);
                passager.setMontantTarif(montantFinal);
                passager.setMontantRemise(montantRemise.compareTo(BigDecimal.ZERO) > 0 ? montantRemise : BigDecimal.ZERO);
                passager.setRefDevise(tarifSiege.getRefDevise());

                total = total.add(montantFinal);
            }

            reservationPassagerRepository.saveAll(passagers);

            reservation.setMontantTotal(total);
            List<Paiement> paiements = paiementRepository.findByReservationId(reservation.getId());
            List<Paiement> paiementsValides = paiements.stream()
                    .filter(p -> p.getRefPaiementStatut() != null)
                    .filter(p -> "VALIDE".equalsIgnoreCase(p.getRefPaiementStatut().getCode()))
                    .toList();

            BigDecimal montantPaye = paiementsValides.stream()
                    .map(p -> p.getMontant() != null ? p.getMontant() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (!paiementsValides.isEmpty() && montantPaye.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = total.divide(montantPaye, 6, RoundingMode.HALF_UP);
                BigDecimal cumul = BigDecimal.ZERO;
                for (int i = 0; i < paiementsValides.size(); i++) {
                    Paiement paiement = paiementsValides.get(i);
                    BigDecimal original = paiement.getMontant() != null ? paiement.getMontant() : BigDecimal.ZERO;
                    BigDecimal nouveau;
                    if (i < paiementsValides.size() - 1) {
                        nouveau = original.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                        cumul = cumul.add(nouveau);
                    } else {
                        nouveau = total.subtract(cumul).setScale(2, RoundingMode.HALF_UP);
                        if (nouveau.compareTo(BigDecimal.ZERO) < 0) {
                            nouveau = BigDecimal.ZERO;
                        }
                    }
                    paiement.setMontant(nouveau);
                }
                paiementRepository.saveAll(paiementsValides);
                montantPaye = paiementsValides.stream()
                        .map(p -> p.getMontant() != null ? p.getMontant() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            reservation.setMontantPaye(montantPaye);
            BigDecimal reste = total.subtract(montantPaye);
            reservation.setResteAPayer(reste.compareTo(BigDecimal.ZERO) > 0 ? reste : BigDecimal.ZERO);

            reservationRepository.save(reservation);
        }
    }

    public List<Integer> getPlacesDisponibles(Long departId) {
        Depart depart = departRepository.findById(departId)
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", departId));
        
        Integer capacite = getCapaciteEffective(depart);
        
        List<Integer> placesOccupees = reservationRepository.findOccupiedSeatsByDepartId(departId);
        
        List<Integer> placesDisponibles = new ArrayList<>();
        for (int i = 1; i <= capacite; i++) {
            if (!placesOccupees.contains(i)) {
                placesDisponibles.add(i);
            }
        }
        
        return placesDisponibles;
    }

    public List<SeatInfoDTO> getSeatMap(Long departId) {
        Depart depart = departRepository.findById(departId)
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", departId));

        Map<Integer, RefSiegeCategorie> seatCategories = siegeConfigurationService.buildSeatCategoryMap(depart);
        if (seatCategories.isEmpty()) {
            throw new BadRequestException("Aucune configuration de sièges trouvée pour ce véhicule");
        }

        Map<Long, DepartTarifSiege> tarifsByCategorie = loadTarifsSiegeByDepart(departId);
        List<Integer> placesOccupees = reservationRepository.findOccupiedSeatsByDepartId(departId);
        int capacite = getCapaciteEffective(depart);

        List<SeatInfoDTO> seats = new ArrayList<>();
        for (int i = 1; i <= capacite; i++) {
            RefSiegeCategorie categorie = seatCategories.get(i);
            DepartTarifSiege tarif = categorie != null ? tarifsByCategorie.get(categorie.getId()) : null;
            if (categorie == null || tarif == null) {
                continue;
            }
            SeatInfoDTO seat = new SeatInfoDTO();
            seat.setNumeroSiege(i);
            seat.setDisponible(!placesOccupees.contains(i));
            seat.setRefSiegeCategorieId(categorie.getId());
            seat.setRefSiegeCategorieCode(categorie.getCode());
            seat.setRefSiegeCategorieLibelle(categorie.getLibelle());
            seat.setMontant(tarif.getMontant());
            if (tarif.getRefDevise() != null) {
                seat.setDeviseCode(tarif.getRefDevise().getCode());
                seat.setDeviseSymbole(tarif.getRefDevise().getSymbole());
            }
            seats.add(seat);
        }

        return seats;
    }

    public ReservationDTO creerReservation(CreerReservationRequest request) {
        // Validation du client
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));
        
        // Validation du départ
        Depart depart = departRepository.findById(request.getDepartId())
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", request.getDepartId()));
        
        // Vérifier que le départ est dans le futur
        if (depart.getDateHeureDepart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Impossible de réserver pour un départ passé");
        }
        
        // Vérifier le statut du départ
        if (!"PROGRAMME".equals(depart.getRefDepartStatut().getCode())) {
            throw new BadRequestException("Le départ n'est pas disponible pour réservation (statut: " + 
                depart.getRefDepartStatut().getLibelle() + ")");
        }
        
        // Obtenir la capacité du véhicule
        Integer capacite = getCapaciteEffective(depart);
        
        // Vérifier les places demandées
        List<Integer> numerosSeges = request.getPassagers().stream()
                .map(CreerReservationRequest.PassagerRequest::getNumeroSiege)
                .collect(Collectors.toList());
        
        // Vérifier les doublons dans la requête
        if (numerosSeges.size() != numerosSeges.stream().distinct().count()) {
            throw new BadRequestException("Des numéros de siège sont en double dans la demande");
        }
        
        // Vérifier que les sièges sont valides
        for (Integer numeroSiege : numerosSeges) {
            if (numeroSiege < 1 || numeroSiege > capacite) {
                throw new BadRequestException("Le siège n°" + numeroSiege + " n'existe pas (capacité: " + capacite + ")");
            }
        }
        
        // Vérifier la disponibilité des sièges
        List<Integer> placesOccupees = reservationRepository.findOccupiedSeatsByDepartId(request.getDepartId());
        for (Integer numeroSiege : numerosSeges) {
            if (placesOccupees.contains(numeroSiege)) {
                throw new BadRequestException("Le siège n°" + numeroSiege + " est déjà réservé");
            }
        }
        
        // Vérifier qu'il reste suffisamment de places
        Long placesDejaOccupees = reservationRepository.countPassagersByDepartId(request.getDepartId());
        if (placesDejaOccupees + request.getPassagers().size() > capacite) {
            throw new BadRequestException("Pas assez de places disponibles (disponibles: " + 
                (capacite - placesDejaOccupees) + ", demandées: " + request.getPassagers().size() + ")");
        }
        
        // Calculer le montant total basé sur les tarifs par catégorie
        
        Map<Long, DepartTarifSiege> tarifsByCategorie = loadTarifsSiegeByDepart(depart.getId());
        Map<Integer, RefSiegeCategorie> seatCategories = siegeConfigurationService.buildSeatCategoryMap(depart);
        if (seatCategories.isEmpty()) {
            throw new BadRequestException("Aucune configuration de sièges trouvée pour ce véhicule");
        }

        Map<Long, RefPassagerCategorie> passagerCategories = refPassagerCategorieRepository.findAll().stream()
                .collect(Collectors.toMap(RefPassagerCategorie::getId, c -> c));
        Map<Long, Map<Long, DepartTarifRemise>> remisesByCategorie = loadRemisesByDepart(depart.getId());

        BigDecimal montantTotal = BigDecimal.ZERO;
        for (CreerReservationRequest.PassagerRequest passagerRequest : request.getPassagers()) {
            Integer numeroSiege = passagerRequest.getNumeroSiege();
            RefSiegeCategorie categorie = seatCategories.get(numeroSiege);
            if (categorie == null) {
                throw new BadRequestException("Le siège n°" + numeroSiege + " n'est pas configuré par catégorie");
            }
            DepartTarifSiege tarifSiege = tarifsByCategorie.get(categorie.getId());
            if (tarifSiege == null) {
                throw new BadRequestException("Tarif manquant pour la catégorie " + categorie.getLibelle());
            }

            RefPassagerCategorie passagerCategorie = passagerCategories.get(passagerRequest.getRefPassagerCategorieId());
            if (passagerCategorie == null) {
                throw new BadRequestException("Catégorie de passager invalide pour le siège " + numeroSiege);
            }

            BigDecimal montantFinal = applyRemise(tarifSiege.getMontant(), categorie.getId(), passagerCategorie.getId(), remisesByCategorie);
            montantTotal = montantTotal.add(montantFinal);
        }
        
        // Déterminer le statut selon le mode de paiement
        RefReservationStatut statut;
        BigDecimal montantPaye = BigDecimal.ZERO;
        
        String modePaiement = request.getModePaiement();
        if (null == modePaiement) {
            throw new BadRequestException("Mode de paiement invalide: " + modePaiement);
        } else {
            switch (modePaiement) {
                case "PAIEMENT_IMMEDIAT" -> {
                    // Paiement immédiat - valider les infos de paiement
                    if (request.getPaiementInfo() == null) {
                        throw new BadRequestException("Les informations de paiement sont obligatoires pour un paiement immédiat");
                    }
                    // Vérifier que le montant payé correspond au total
                    if (request.getPaiementInfo().getMontant().compareTo(montantTotal) < 0) {
                        throw new BadRequestException("Le montant payé (" + request.getPaiementInfo().getMontant() +
                                ") est insuffisant. Montant requis: " + montantTotal);
                    }
                    montantPaye = request.getPaiementInfo().getMontant();
                    statut = refReservationStatutRepository.findTop1ByCodeOrderByIdAsc("CONFIRMEE")
                            .orElseThrow(() -> new ResourceNotFoundException("RefReservationStatut", "code", "CONFIRMEE"));
                }
                case "COMPTOIR", "EMBARQUEMENT" -> // Paiement différé
                    statut = refReservationStatutRepository.findTop1ByCodeOrderByIdAsc("EN_ATTENTE")
                            .orElseThrow(() -> new ResourceNotFoundException("RefReservationStatut", "code", "EN_ATTENTE"));
                default -> throw new BadRequestException("Mode de paiement invalide: " + modePaiement);
            }
        }
        
        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setCode(genererCodeReservation(client, depart));
        reservation.setClient(client);
        reservation.setDepart(depart);
        reservation.setRefReservationStatut(statut);
        reservation.setNotes(request.getNotes());
        reservation.setMontantTotal(montantTotal);
        reservation.setMontantPaye(montantPaye);
        reservation.setResteAPayer(montantTotal.subtract(montantPaye));
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // Créer les passagers
        List<ReservationPassager> passagers = new ArrayList<>();
        for (CreerReservationRequest.PassagerRequest passagerRequest : request.getPassagers()) {
            RefSiegeCategorie categorie = seatCategories.get(passagerRequest.getNumeroSiege());
            DepartTarifSiege tarifSiege = categorie != null ? tarifsByCategorie.get(categorie.getId()) : null;
            if (categorie == null || tarifSiege == null) {
                throw new BadRequestException("Tarif/catégorie manquant pour le siège " + passagerRequest.getNumeroSiege());
            }

            RefPassagerCategorie passagerCategorie = passagerCategories.get(passagerRequest.getRefPassagerCategorieId());
            if (passagerCategorie == null) {
                throw new BadRequestException("Catégorie de passager invalide pour le siège " + passagerRequest.getNumeroSiege());
            }

            BigDecimal montantFinal = applyRemise(tarifSiege.getMontant(), categorie.getId(), passagerCategorie.getId(), remisesByCategorie);
            BigDecimal montantRemise = tarifSiege.getMontant().subtract(montantFinal);
            ReservationPassager passager = new ReservationPassager();
            passager.setReservation(savedReservation);
            passager.setDepart(depart);
            passager.setNom(passagerRequest.getNom());
            passager.setPrenom(passagerRequest.getPrenom());
            passager.setNumeroSiege(passagerRequest.getNumeroSiege());
            passager.setRefSiegeCategorie(categorie);
            passager.setRefPassagerCategorie(passagerCategorie);
            passager.setMontantTarif(montantFinal);
            passager.setMontantRemise(montantRemise.compareTo(BigDecimal.ZERO) > 0 ? montantRemise : BigDecimal.ZERO);
            passager.setRefDevise(tarifSiege.getRefDevise());
            passagers.add(passager);
        }
        
        reservationPassagerRepository.saveAll(passagers);
        
        // Si paiement immédiat, créer le paiement et émettre les billets
        if ("PAIEMENT_IMMEDIAT".equals(modePaiement) && request.getPaiementInfo() != null) {
            creerPaiement(savedReservation, request.getPaiementInfo());
            emettreBillets(savedReservation, passagers);
        }
        
        return enrichReservationDTO(reservationMapper.toDTO(savedReservation));
    }
    
    public ReservationDTO enregistrerPaiement(Long reservationId, PaiementRequest paiementRequest) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", reservationId));
        
        // Vérifier que la réservation n'est pas déjà payée
        if ("CONFIRME".equals(reservation.getRefReservationStatut().getCode()) && 
            reservation.getResteAPayer().compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException("Cette réservation est déjà entièrement payée");
        }
        
        // Vérifier que la réservation n'est pas annulée
        if ("ANNULE".equals(reservation.getRefReservationStatut().getCode())) {
            throw new BadRequestException("Impossible d'enregistrer un paiement pour une réservation annulée");
        }
        
        // Créer le paiement
        creerPaiement(reservation, paiementRequest);
        
        // Mettre à jour les montants
        BigDecimal nouveauMontantPaye = reservation.getMontantPaye().add(paiementRequest.getMontant());
        reservation.setMontantPaye(nouveauMontantPaye);
        reservation.setResteAPayer(reservation.getMontantTotal().subtract(nouveauMontantPaye));
        
        // Si entièrement payé, passer en statut CONFIRME et émettre les billets
        if (reservation.getResteAPayer().compareTo(BigDecimal.ZERO) <= 0) {
            RefReservationStatut statutConfirme = refReservationStatutRepository.findTop1ByCodeOrderByIdAsc("CONFIRMEE")
                    .orElseThrow(() -> new ResourceNotFoundException("RefReservationStatut", "code", "CONFIRMEE"));
            reservation.setRefReservationStatut(statutConfirme);
            
            // Émettre les billets
            List<ReservationPassager> passagers = reservationPassagerRepository.findByReservationId(reservation.getId());
            emettreBillets(reservation, passagers);
        }
        
        reservationRepository.save(reservation);
        
        return enrichReservationDTO(reservationMapper.toDTO(reservation));
    }
    
    private void creerPaiement(Reservation reservation, PaiementRequest paiementRequest) {
        Paiement paiement = new Paiement();
        paiement.setReservation(reservation);
        paiement.setMontant(paiementRequest.getMontant());
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setReferenceTransaction(paiementRequest.getReferenceTransaction());
        paiement.setNotes(paiementRequest.getNotes());

        // Devise: prendre la devise des tarifs de siège si disponible, sinon MGA
        RefDevise devise = null;
        List<ReservationPassager> passagers = reservationPassagerRepository.findByReservationId(reservation.getId());
        if (!passagers.isEmpty() && passagers.get(0).getRefDevise() != null) {
            devise = passagers.get(0).getRefDevise();
        }
        if (devise == null) {
            devise = refDeviseRepository.findTop1ByCodeOrderByIdAsc("MGA")
                .orElseThrow(() -> new ResourceNotFoundException("RefDevise", "code", "MGA"));
        }
        paiement.setRefDevise(devise);

        // Méthode de paiement: mappe le mode front vers la ref
        String methodeCode = mapModeToMethodeCode(paiementRequest.getModePaiement());
        RefPaiementMethode methode = refPaiementMethodeRepository.findTop1ByCodeOrderByIdAsc(methodeCode)
            .orElseThrow(() -> new ResourceNotFoundException("RefPaiementMethode", "code", methodeCode));
        paiement.setRefPaiementMethode(methode);

        // Statut: valide par défaut quand l'enregistrement passe
        RefPaiementStatut statut = refPaiementStatutRepository.findTop1ByCodeOrderByIdAsc("VALIDE")
            .orElseThrow(() -> new ResourceNotFoundException("RefPaiementStatut", "code", "VALIDE"));
        paiement.setRefPaiementStatut(statut);

        paiementRepository.save(paiement);
    }

    private Map<Long, Map<Long, DepartTarifRemise>> loadRemisesByDepart(Long departId) {
        return departTarifRemiseRepository.findByDepartId(departId).stream()
                .filter(r -> r.getRefSiegeCategorie() != null && r.getRefPassagerCategorie() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getRefSiegeCategorie().getId(),
                        Collectors.toMap(r -> r.getRefPassagerCategorie().getId(), r -> r, (a, b) -> a)
                ));
    }

    private BigDecimal applyRemise(BigDecimal montantBase, Long siegeCategorieId, Long passagerCategorieId,
            Map<Long, Map<Long, DepartTarifRemise>> remisesByCategorie) {
        if (montantBase == null) {
            return BigDecimal.ZERO;
        }
        DepartTarifRemise remise = null;
        Map<Long, DepartTarifRemise> remisesParPassager = remisesByCategorie.get(siegeCategorieId);
        if (remisesParPassager != null) {
            remise = remisesParPassager.get(passagerCategorieId);
        }

        if (remise == null) {
            return montantBase;
        }

        BigDecimal montantFinal;
        if ("POURCENT".equalsIgnoreCase(remise.getTypeRemise())) {
            BigDecimal montantRemise = montantBase.multiply(remise.getMontant()).divide(BigDecimal.valueOf(100));
            montantFinal = montantBase.subtract(montantRemise);
        } else {
            montantFinal = remise.getMontant();
        }

        if (montantFinal.compareTo(BigDecimal.ZERO) < 0) {
            montantFinal = BigDecimal.ZERO;
        }
        return montantFinal;
    }

    private String mapModeToMethodeCode(String modePaiement) {
        if (modePaiement == null) return "ESPECES";
        return switch (modePaiement) {
            case "ESPECES" -> "ESPECES";
            case "MOBILE_MONEY", "MVOLA", "ORANGE_MONEY", "AIRTEL_MONEY" -> "MVOLA";
            case "CARTE_BANCAIRE", "CARTE" -> "CARTE";
            case "VIREMENT" -> "VIREMENT";
            case "CHEQUE" -> "VIREMENT"; // fallback proche
            default -> "ESPECES";
        };
    }
    
    private void emettreBillets(Reservation reservation, List<ReservationPassager> passagers) {
        RefBilletStatut statutEmis = refBilletStatutRepository.findTop1ByCodeOrderByIdAsc("EMIS")
                .orElseThrow(() -> new ResourceNotFoundException("RefBilletStatut", "code", "EMIS"));
        
        for (ReservationPassager passager : passagers) {
            // Vérifier si un billet n'existe pas déjà
            if (!billetRepository.existsByReservationIdAndNumeroSiege(reservation.getId(), passager.getNumeroSiege())) {
                String numeroBillet = genererNumeroBillet(reservation, passager);
                
                Billet billet = new Billet();
                billet.setNumero(numeroBillet);
                billet.setCode(numeroBillet);
                billet.setReservation(reservation);
                billet.setPassagerNom(passager.getNom());
                billet.setPassagerPrenom(passager.getPrenom());
                billet.setNumeroSiege(passager.getNumeroSiege());
                billet.setDateEmission(LocalDateTime.now());
                billet.setRefBilletStatut(statutEmis);
                
                billetRepository.save(billet);
            }
        }
    }
    
    private String genererNumeroBillet(Reservation reservation, ReservationPassager passager) {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "BIL-" + reservation.getId() + "-" + passager.getNumeroSiege() + "-" + datePart;
    }

    public ReservationDTO updateStatut(Long id, Long nouveauStatutId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        
        RefReservationStatut nouveauStatut = refReservationStatutRepository.findById(nouveauStatutId)
                .orElseThrow(() -> new ResourceNotFoundException("RefReservationStatut", "id", nouveauStatutId));
        
        reservation.setRefReservationStatut(nouveauStatut);
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        return enrichReservationDTO(reservationMapper.toDTO(updatedReservation));
    }

    public ReservationDTO updateReservation(Long id, CreerReservationRequest request) {
        // Récupérer la réservation existante
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        
        // Vérifier que le départ n'est pas encore passé
        if (reservation.getDepart().getDateHeureDepart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Impossible de modifier une réservation pour un départ passé");
        }
        
        // Vérifier que la réservation n'est pas annulée
        if ("ANNULEE".equals(reservation.getRefReservationStatut().getCode())) {
            throw new BadRequestException("Impossible de modifier une réservation annulée");
        }
        
        // Validation du client
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));
        
        // Validation du départ
        Depart depart = departRepository.findById(request.getDepartId())
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", request.getDepartId()));
        
        // Obtenir la capacité du véhicule
        Integer capacite = getCapaciteEffective(depart);
        
        // Vérifier les places demandées
        List<Integer> numerosSeges = request.getPassagers().stream()
                .map(CreerReservationRequest.PassagerRequest::getNumeroSiege)
                .collect(Collectors.toList());
        
        // Vérifier les doublons dans la requête
        if (numerosSeges.size() != numerosSeges.stream().distinct().count()) {
            throw new BadRequestException("Des numéros de siège sont en double dans la demande");
        }
        
        // Vérifier que les sièges sont valides
        for (Integer numeroSiege : numerosSeges) {
            if (numeroSiege < 1 || numeroSiege > capacite) {
                throw new BadRequestException("Numéro de siège invalide: " + numeroSiege + 
                        ". Le véhicule a " + capacite + " places.");
            }
        }
        
        // Supprimer les anciens passagers AVANT de vérifier les places disponibles
        List<ReservationPassager> anciensPassagers = reservationPassagerRepository.findByReservationId(id);
        reservationPassagerRepository.deleteAll(anciensPassagers);
        reservationPassagerRepository.flush(); // Forcer la synchronisation avec la BD
        
        // Vérifier la disponibilité des places (maintenant que les anciens sont supprimés)
        List<Integer> placesOccupees = reservationRepository.findOccupiedSeatsByDepartId(request.getDepartId());
        
        for (Integer numeroSiege : numerosSeges) {
            if (placesOccupees.contains(numeroSiege)) {
                throw new BadRequestException("Le siège " + numeroSiege + " est déjà occupé");
            }
        }
        
        // Mettre à jour la réservation
        reservation.setClient(client);
        reservation.setDepart(depart);
        reservation.setNotes(request.getNotes());
        
        // Calculer le tarif
        Map<Long, DepartTarifSiege> tarifsByCategorie = loadTarifsSiegeByDepart(depart.getId());
        Map<Integer, RefSiegeCategorie> seatCategories = siegeConfigurationService.buildSeatCategoryMap(depart);
        if (seatCategories.isEmpty()) {
            throw new BadRequestException("Aucune configuration de sièges trouvée pour ce véhicule");
        }

        BigDecimal montantTotal = BigDecimal.ZERO;
        for (Integer numeroSiege : numerosSeges) {
            RefSiegeCategorie categorie = seatCategories.get(numeroSiege);
            if (categorie == null) {
                throw new BadRequestException("Le siège n°" + numeroSiege + " n'est pas configuré par catégorie");
            }
            DepartTarifSiege tarifSiege = tarifsByCategorie.get(categorie.getId());
            if (tarifSiege == null) {
                throw new BadRequestException("Tarif manquant pour la catégorie " + categorie.getLibelle());
            }
            montantTotal = montantTotal.add(tarifSiege.getMontant());
        }
        reservation.setMontantTotal(montantTotal);
        
        // Recalculer le reste à payer
        if (reservation.getMontantPaye() == null) {
            reservation.setMontantPaye(BigDecimal.ZERO);
        }
        reservation.setResteAPayer(montantTotal.subtract(reservation.getMontantPaye()));
        
        Reservation updatedReservation = reservationRepository.save(reservation);
        
        // Créer les nouveaux passagers
        for (CreerReservationRequest.PassagerRequest passagerReq : request.getPassagers()) {
            RefSiegeCategorie categorie = seatCategories.get(passagerReq.getNumeroSiege());
            DepartTarifSiege tarifSiege = categorie != null ? tarifsByCategorie.get(categorie.getId()) : null;
            if (categorie == null || tarifSiege == null) {
                throw new BadRequestException("Tarif/catégorie manquant pour le siège " + passagerReq.getNumeroSiege());
            }
            ReservationPassager passager = new ReservationPassager();
            passager.setReservation(updatedReservation);
            passager.setDepart(depart);
            passager.setNom(passagerReq.getNom());
            passager.setPrenom(passagerReq.getPrenom());
            passager.setNumeroSiege(passagerReq.getNumeroSiege());
            passager.setRefSiegeCategorie(categorie);
            passager.setMontantTarif(tarifSiege.getMontant());
            passager.setRefDevise(tarifSiege.getRefDevise());
            reservationPassagerRepository.save(passager);
        }
        
        return enrichReservationDTO(reservationMapper.toDTO(updatedReservation));
    }

    public void annulerReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        
        // Vérifier que le départ n'est pas encore passé
        if (reservation.getDepart().getDateHeureDepart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Impossible d'annuler une réservation pour un départ passé");
        }
        
        RefReservationStatut statutAnnule = refReservationStatutRepository.findTop1ByCodeOrderByIdAsc("ANNULEE")
                .orElseThrow(() -> new ResourceNotFoundException("RefReservationStatut", "code", "ANNULEE"));

        reservation.setRefReservationStatut(statutAnnule);

        List<Paiement> paiements = paiementRepository.findByReservationId(reservation.getId());
        if (!paiements.isEmpty()) {
            RefPaiementStatut statutRembourse = refPaiementStatutRepository.findTop1ByCodeOrderByIdAsc("REMBOURSE")
                    .orElse(null);
            if (statutRembourse != null) {
                paiements.forEach(p -> p.setRefPaiementStatut(statutRembourse));
                paiementRepository.saveAll(paiements);
            }
        }

        List<Billet> billets = billetRepository.findByReservationId(reservation.getId());
        if (!billets.isEmpty()) {
            RefBilletStatut billetAnnule = refBilletStatutRepository.findTop1ByCodeOrderByIdAsc("ANNULE")
                    .orElse(null);
            if (billetAnnule != null) {
                billets.forEach(b -> b.setRefBilletStatut(billetAnnule));
                billetRepository.saveAll(billets);
            }
        }

        reservation.setMontantTotal(BigDecimal.ZERO);
        reservation.setMontantPaye(BigDecimal.ZERO);
        reservation.setResteAPayer(BigDecimal.ZERO);

        reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        
        // Supprimer d'abord les passagers
        List<ReservationPassager> passagers = reservationPassagerRepository.findByReservationId(id);
        reservationPassagerRepository.deleteAll(passagers);
        
        // Supprimer la réservation
        reservationRepository.delete(reservation);
    }

    private String genererCodeReservation(Client client, Depart depart) {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String clientPart = client.getNom().substring(0, Math.min(3, client.getNom().length())).toUpperCase();
        String departPart = depart.getCode().substring(0, Math.min(3, depart.getCode().length())).toUpperCase();
        
        return "RES-" + clientPart + "-" + departPart + "-" + datePart;
    }

    private ReservationDTO enrichReservationDTO(ReservationDTO dto) {
        if (dto == null || dto.getId() == null) {
            return dto;
        }
        
        // Charger les passagers
        List<ReservationPassager> passagers = reservationPassagerRepository.findByReservationId(dto.getId());
        List<ReservationPassagerDTO> passagersDTO = passagers.stream()
                .map(reservationPassagerMapper::toDTO)
                .collect(Collectors.toList());
        dto.setPassagers(passagersDTO);
        
        return dto;
    }

    private Integer getCapaciteEffective(Depart depart) {
        if (depart == null || depart.getVehicule() == null) {
            return 0;
        }
        int totalByConfig = siegeConfigurationService.getTotalPlacesForVehicule(
                depart.getVehicule().getId(), depart.getCapaciteOverride());
        if (totalByConfig > 0) {
            return totalByConfig;
        }
        Integer base = depart.getVehicule().getNombrePlaces();
        if (depart.getCapaciteOverride() != null && depart.getCapaciteOverride() > 0) {
            return Math.min(base != null ? base : 0, depart.getCapaciteOverride());
        }
        return base != null ? base : 0;
    }

    private Map<Long, DepartTarifSiege> loadTarifsSiegeByDepart(Long departId) {
        List<DepartTarifSiege> tarifs = departTarifSiegeRepository.findByDepartId(departId);
        if (tarifs.isEmpty()) {
            throw new BadRequestException("Aucun tarif de siège configuré pour ce départ");
        }
        Long deviseId = tarifs.get(0).getRefDevise() != null ? tarifs.get(0).getRefDevise().getId() : null;
        boolean deviseUnique = tarifs.stream().allMatch(t -> {
            Long id = t.getRefDevise() != null ? t.getRefDevise().getId() : null;
            return id != null && id.equals(deviseId);
        });
        if (!deviseUnique) {
            throw new BadRequestException("Les tarifs de sièges doivent utiliser la même devise pour un départ");
        }
        return tarifs.stream().collect(Collectors.toMap(t -> t.getRefSiegeCategorie().getId(), t -> t));
    }
}
