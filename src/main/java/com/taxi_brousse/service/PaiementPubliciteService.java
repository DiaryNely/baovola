package com.taxi_brousse.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.PaiementPubliciteDTO;
import com.taxi_brousse.dto.PaiementPubliciteResumeDTO;
import com.taxi_brousse.entity.PaiementPublicite;
import com.taxi_brousse.entity.SocietePublicitaire;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.exception.BadRequestException;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.repository.DepartPubliciteRepository;
import com.taxi_brousse.repository.PaiementPubliciteRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import com.taxi_brousse.repository.SocietePublicitaireRepository;
import com.taxi_brousse.mapper.DepartPubliciteMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaiementPubliciteService {

    private final PaiementPubliciteRepository paiementPubliciteRepository;
    private final DepartPubliciteRepository departPubliciteRepository;
    private final SocietePublicitaireRepository societePublicitaireRepository;
    private final RefDeviseRepository refDeviseRepository;
    private final DepartPubliciteMapper departPubliciteMapper;

    @Transactional(readOnly = true)
    public PaiementPubliciteResumeDTO getResume(Long societeId) {
        return getResumeByPeriode(societeId, null, null);
    }

    @Transactional(readOnly = true)
    public PaiementPubliciteResumeDTO getResumeByPeriode(Long societeId, Integer mois, Integer annee) {
        SocietePublicitaire societe = societePublicitaireRepository.findById(societeId)
                .orElseThrow(() -> new ResourceNotFoundException("SocietePublicitaire", "id", societeId));

        BigDecimal totalFacture;
        BigDecimal totalPaye;

        if (mois != null && annee != null) {
            java.time.YearMonth yearMonth = java.time.YearMonth.of(annee, mois);
            java.time.LocalDateTime dateDebut = yearMonth.atDay(1).atStartOfDay();
            java.time.LocalDateTime dateFin = java.time.LocalDateTime.of(yearMonth.atEndOfMonth(), java.time.LocalTime.MAX);
            totalFacture = departPubliciteRepository.sumMontantFactureBySocieteIdAndPeriode(societeId, dateDebut, dateFin);
            totalPaye = paiementPubliciteRepository.sumMontantBySocieteIdAndFacturePeriode(societeId, mois, annee);
        } else {
            totalFacture = departPubliciteRepository.sumMontantFactureBySocieteId(societeId);
            totalPaye = paiementPubliciteRepository.sumMontantBySocieteId(societeId);
        }
        BigDecimal restant = totalFacture.subtract(totalPaye);
        if (restant.compareTo(BigDecimal.ZERO) < 0) {
            restant = BigDecimal.ZERO;
        }

        RefDevise devise = resolveDevise(societeId);

        PaiementPubliciteResumeDTO resume = new PaiementPubliciteResumeDTO();
        resume.setSocietePublicitaireId(societe.getId());
        resume.setSocietePublicitaireNom(societe.getNom());
        resume.setMois(mois);
        resume.setAnnee(annee);
        resume.setMontantTotalFacture(totalFacture);
        resume.setMontantTotalPaye(totalPaye);
        resume.setMontantRestant(restant);
        if (devise != null) {
            resume.setDeviseCode(devise.getCode());
            resume.setDeviseSymbole(devise.getSymbole());
        }
        return resume;
    }

    @Transactional(readOnly = true)
    public List<PaiementPubliciteDTO> listBySociete(Long societeId) {
        return paiementPubliciteRepository.findBySocieteId(societeId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaiementPubliciteDTO> listBySocieteAndPeriode(Long societeId, Integer mois, Integer annee) {
        return paiementPubliciteRepository.findBySocieteIdAndFacturePeriode(societeId, mois, annee).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<com.taxi_brousse.dto.DepartPubliciteDTO> listDiffusionsBySocieteAndPeriode(Long societeId, Integer mois, Integer annee) {
        java.time.YearMonth yearMonth = java.time.YearMonth.of(annee, mois);
        java.time.LocalDateTime dateDebut = yearMonth.atDay(1).atStartOfDay();
        java.time.LocalDateTime dateFin = java.time.LocalDateTime.of(yearMonth.atEndOfMonth(), java.time.LocalTime.MAX);
        return departPubliciteRepository.findBySocieteIdAndDateDiffusionBetween(societeId, dateDebut, dateFin).stream()
                .map(departPubliciteMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PaiementPubliciteDTO create(PaiementPubliciteDTO dto) {
        SocietePublicitaire societe = societePublicitaireRepository.findById(dto.getSocietePublicitaireId())
                .orElseThrow(() -> new ResourceNotFoundException("SocietePublicitaire", "id", dto.getSocietePublicitaireId()));

        if (dto.getMontant() == null || dto.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Le montant doit être positif");
        }

        Integer factureMois = dto.getFactureMois();
        Integer factureAnnee = dto.getFactureAnnee();
        if (factureMois == null || factureAnnee == null) {
            java.time.LocalDateTime baseDate = dto.getDatePaiement() != null ? dto.getDatePaiement() : java.time.LocalDateTime.now();
            factureMois = baseDate.getMonthValue();
            factureAnnee = baseDate.getYear();
        }

        PaiementPubliciteResumeDTO resume = getResumeByPeriode(societe.getId(), factureMois, factureAnnee);
        if (resume.getMontantRestant() != null && dto.getMontant().compareTo(resume.getMontantRestant()) > 0) {
            throw new BadRequestException("Le montant dépasse le reste à payer");
        }

        RefDevise devise = null;
        if (dto.getRefDeviseId() != null) {
            devise = refDeviseRepository.findById(dto.getRefDeviseId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefDevise", "id", dto.getRefDeviseId()));
        } else {
            devise = resolveDevise(societe.getId());
            if (devise == null) {
                devise = refDeviseRepository.findTop1ByCodeOrderByIdAsc("MGA")
                        .orElseThrow(() -> new ResourceNotFoundException("RefDevise", "code", "MGA"));
            }
        }

        PaiementPublicite paiement = new PaiementPublicite();
        paiement.setSocietePublicitaire(societe);
        paiement.setMontant(dto.getMontant());
        paiement.setRefDevise(devise);
        paiement.setDatePaiement(dto.getDatePaiement());
        paiement.setFactureMois(factureMois);
        paiement.setFactureAnnee(factureAnnee);
        paiement.setNote(dto.getNote());

        paiement = paiementPubliciteRepository.save(paiement);
        return toDTO(paiement);
    }

    private RefDevise resolveDevise(Long societeId) {
        List<RefDevise> devisesDiffusions = departPubliciteRepository.findDeviseBySocieteId(societeId, PageRequest.of(0, 1));
        if (!devisesDiffusions.isEmpty()) {
            return devisesDiffusions.get(0);
        }
        List<RefDevise> devisesPaiements = paiementPubliciteRepository.findDeviseBySocieteId(societeId, PageRequest.of(0, 1));
        if (!devisesPaiements.isEmpty()) {
            return devisesPaiements.get(0);
        }
        return null;
    }

    private PaiementPubliciteDTO toDTO(PaiementPublicite entity) {
        PaiementPubliciteDTO dto = new PaiementPubliciteDTO();
        dto.setId(entity.getId());
        dto.setSocietePublicitaireId(entity.getSocietePublicitaire().getId());
        dto.setSocietePublicitaireNom(entity.getSocietePublicitaire().getNom());
        dto.setMontant(entity.getMontant());
        dto.setRefDeviseId(entity.getRefDevise().getId());
        dto.setDeviseCode(entity.getRefDevise().getCode());
        dto.setDeviseSymbole(entity.getRefDevise().getSymbole());
        dto.setDatePaiement(entity.getDatePaiement());
        dto.setFactureMois(entity.getFactureMois());
        dto.setFactureAnnee(entity.getFactureAnnee());
        dto.setNote(entity.getNote());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
