package com.taxi_brousse.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.PaiementDTO;
import com.taxi_brousse.entity.Paiement;
import com.taxi_brousse.entity.Reservation;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefPaiementMethode;
import com.taxi_brousse.entity.reference.RefPaiementStatut;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.repository.PaiementRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import com.taxi_brousse.repository.RefPaiementMethodeRepository;
import com.taxi_brousse.repository.RefPaiementStatutRepository;
import com.taxi_brousse.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;
    private final RefDeviseRepository refDeviseRepository;
    private final RefPaiementMethodeRepository refPaiementMethodeRepository;
    private final RefPaiementStatutRepository refPaiementStatutRepository;

    @Transactional(readOnly = true)
    public List<PaiementDTO> getAll() {
        return paiementRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaiementDTO getById(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", "id", id));
        return toDTO(paiement);
    }

    @Transactional
    public PaiementDTO create(PaiementDTO dto) {
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", dto.getReservationId()));

        RefDevise devise = refDeviseRepository.findById(dto.getRefDeviseId())
                .orElseThrow(() -> new ResourceNotFoundException("RefDevise", "id", dto.getRefDeviseId()));

        RefPaiementMethode methode = refPaiementMethodeRepository.findById(dto.getRefPaiementMethodeId())
                .orElseThrow(() -> new ResourceNotFoundException("RefPaiementMethode", "id", dto.getRefPaiementMethodeId()));

        RefPaiementStatut statut = refPaiementStatutRepository.findById(dto.getRefPaiementStatutId())
                .orElseThrow(() -> new ResourceNotFoundException("RefPaiementStatut", "id", dto.getRefPaiementStatutId()));

        Paiement paiement = new Paiement();
        paiement.setReservation(reservation);
        paiement.setMontant(dto.getMontant());
        paiement.setRefDevise(devise);
        paiement.setRefPaiementMethode(methode);
        paiement.setRefPaiementStatut(statut);
        paiement.setReferenceExterne(dto.getReferenceExterne());
        paiement.setNotes(dto.getNotes());
        paiement.setDatePaiement(dto.getDatePaiement() != null ? dto.getDatePaiement() : LocalDateTime.now());

        Paiement saved = paiementRepository.save(paiement);
        updateReservationPaymentTotals(reservation);
        return toDTO(saved);
    }

    @Transactional
    public PaiementDTO update(Long id, PaiementDTO dto) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", "id", id));

        Reservation reservation = paiement.getReservation();
        if (dto.getReservationId() != null && (reservation == null || !dto.getReservationId().equals(reservation.getId()))) {
            reservation = reservationRepository.findById(dto.getReservationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", dto.getReservationId()));
            paiement.setReservation(reservation);
        }

        if (dto.getRefDeviseId() != null) {
            RefDevise devise = refDeviseRepository.findById(dto.getRefDeviseId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefDevise", "id", dto.getRefDeviseId()));
            paiement.setRefDevise(devise);
        }

        if (dto.getRefPaiementMethodeId() != null) {
            RefPaiementMethode methode = refPaiementMethodeRepository.findById(dto.getRefPaiementMethodeId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefPaiementMethode", "id", dto.getRefPaiementMethodeId()));
            paiement.setRefPaiementMethode(methode);
        }

        if (dto.getRefPaiementStatutId() != null) {
            RefPaiementStatut statut = refPaiementStatutRepository.findById(dto.getRefPaiementStatutId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefPaiementStatut", "id", dto.getRefPaiementStatutId()));
            paiement.setRefPaiementStatut(statut);
        }

        if (dto.getMontant() != null) {
            paiement.setMontant(dto.getMontant());
        }

        if (dto.getReferenceExterne() != null) {
            paiement.setReferenceExterne(dto.getReferenceExterne());
        }

        if (dto.getNotes() != null) {
            paiement.setNotes(dto.getNotes());
        }

        if (dto.getDatePaiement() != null) {
            paiement.setDatePaiement(dto.getDatePaiement());
        }

        Paiement saved = paiementRepository.save(paiement);
        if (reservation != null) {
            updateReservationPaymentTotals(reservation);
        }
        return toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", "id", id));
        Reservation reservation = paiement.getReservation();
        paiementRepository.delete(paiement);
        if (reservation != null) {
            updateReservationPaymentTotals(reservation);
        }
    }

    private void updateReservationPaymentTotals(Reservation reservation) {
        if (reservation == null) return;
        List<Paiement> paiements = paiementRepository.findByReservationId(reservation.getId());
        java.math.BigDecimal montantPaye = paiements.stream()
                .filter(p -> p.getRefPaiementStatut() != null)
                .filter(p -> "VALIDE".equalsIgnoreCase(p.getRefPaiementStatut().getCode()))
                .map(p -> p.getMontant() != null ? p.getMontant() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        reservation.setMontantPaye(montantPaye);
        if (reservation.getMontantTotal() != null) {
            java.math.BigDecimal reste = reservation.getMontantTotal().subtract(montantPaye);
            reservation.setResteAPayer(reste.compareTo(java.math.BigDecimal.ZERO) > 0 ? reste : java.math.BigDecimal.ZERO);
        }
        reservationRepository.save(reservation);
    }

    private PaiementDTO toDTO(Paiement paiement) {
        PaiementDTO dto = new PaiementDTO();
        dto.setId(paiement.getId());
        if (paiement.getReservation() != null) {
            dto.setReservationId(paiement.getReservation().getId());
            dto.setReservationCode(paiement.getReservation().getCode());
            if (paiement.getReservation().getClient() != null) {
                dto.setClientNom(paiement.getReservation().getClient().getNom());
                dto.setClientPrenom(paiement.getReservation().getClient().getPrenom());
            }
            if (paiement.getReservation().getDepart() != null) {
                dto.setDepartCode(paiement.getReservation().getDepart().getCode());
            }
        }

        dto.setMontant(paiement.getMontant());
        if (paiement.getRefDevise() != null) {
            dto.setRefDeviseId(paiement.getRefDevise().getId());
            dto.setDeviseCode(paiement.getRefDevise().getCode());
            dto.setDeviseSymbole(paiement.getRefDevise().getSymbole());
        }

        if (paiement.getRefPaiementMethode() != null) {
            dto.setRefPaiementMethodeId(paiement.getRefPaiementMethode().getId());
            dto.setRefPaiementMethodeCode(paiement.getRefPaiementMethode().getCode());
            dto.setRefPaiementMethodeLibelle(paiement.getRefPaiementMethode().getLibelle());
        }

        if (paiement.getRefPaiementStatut() != null) {
            dto.setRefPaiementStatutId(paiement.getRefPaiementStatut().getId());
            dto.setRefPaiementStatutCode(paiement.getRefPaiementStatut().getCode());
            dto.setRefPaiementStatutLibelle(paiement.getRefPaiementStatut().getLibelle());
        }

        dto.setReferenceExterne(paiement.getReferenceExterne());
        dto.setNotes(paiement.getNotes());
        dto.setDatePaiement(paiement.getDatePaiement());
        dto.setCreatedAt(paiement.getCreatedAt());
        return dto;
    }
}
