package com.taxi_brousse.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.DepartTarifSiegeDTO;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.DepartTarifSiege;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefSiegeCategorie;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.DepartTarifSiegeMapper;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.DepartTarifSiegeRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import com.taxi_brousse.repository.RefSiegeCategorieRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartTarifSiegeService {

    private final DepartTarifSiegeRepository departTarifSiegeRepository;
    private final DepartRepository departRepository;
    private final RefSiegeCategorieRepository refSiegeCategorieRepository;
    private final RefDeviseRepository refDeviseRepository;
    private final DepartTarifSiegeMapper departTarifSiegeMapper;
    private final ReservationService reservationService;

    @Transactional(readOnly = true)
    public List<DepartTarifSiegeDTO> getTarifsByDepart(Long departId) {
        return departTarifSiegeRepository.findByDepartId(departId).stream()
                .map(departTarifSiegeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DepartTarifSiegeDTO> getAll() {
        return departTarifSiegeRepository.findAll().stream()
                .map(departTarifSiegeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DepartTarifSiegeDTO getById(Long id) {
        DepartTarifSiege tarif = departTarifSiegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartTarifSiege", "id", id));
        return departTarifSiegeMapper.toDTO(tarif);
    }

    @Transactional
    public List<DepartTarifSiegeDTO> saveTarifs(Long departId, List<DepartTarifSiegeDTO> tarifs) {
        Depart depart = departRepository.findById(departId)
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", departId));

        departTarifSiegeRepository.deleteByDepartId(departId);

        List<DepartTarifSiege> entities = tarifs.stream().map(dto -> {
            DepartTarifSiege entity = new DepartTarifSiege();
            entity.setDepart(depart);

            RefSiegeCategorie categorie = refSiegeCategorieRepository.findById(dto.getRefSiegeCategorieId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefSiegeCategorie", "id", dto.getRefSiegeCategorieId()));
            entity.setRefSiegeCategorie(categorie);

            RefDevise devise = refDeviseRepository.findById(dto.getRefDeviseId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefDevise", "id", dto.getRefDeviseId()));
            entity.setRefDevise(devise);

            entity.setMontant(dto.getMontant());
            return entity;
        }).collect(Collectors.toList());

        List<DepartTarifSiege> saved = departTarifSiegeRepository.saveAll(entities);
        reservationService.recalculateConfirmedReservationsForDepart(departId);
        return saved.stream().map(departTarifSiegeMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public DepartTarifSiegeDTO create(DepartTarifSiegeDTO dto) {
        if (dto.getDepartId() == null) {
            throw new ResourceNotFoundException("Depart", "id", null);
        }

        Depart depart = departRepository.findById(dto.getDepartId())
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", dto.getDepartId()));

        RefSiegeCategorie categorie = refSiegeCategorieRepository.findById(dto.getRefSiegeCategorieId())
                .orElseThrow(() -> new ResourceNotFoundException("RefSiegeCategorie", "id", dto.getRefSiegeCategorieId()));

        RefDevise devise = refDeviseRepository.findById(dto.getRefDeviseId())
                .orElseThrow(() -> new ResourceNotFoundException("RefDevise", "id", dto.getRefDeviseId()));

        DepartTarifSiege entity = new DepartTarifSiege();
        entity.setDepart(depart);
        entity.setRefSiegeCategorie(categorie);
        entity.setRefDevise(devise);
        entity.setMontant(dto.getMontant());

        DepartTarifSiege saved = departTarifSiegeRepository.save(entity);
        reservationService.recalculateConfirmedReservationsForDepart(dto.getDepartId());
        return departTarifSiegeMapper.toDTO(saved);
    }

    @Transactional
    public DepartTarifSiegeDTO update(Long id, DepartTarifSiegeDTO dto) {
        DepartTarifSiege existing = departTarifSiegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartTarifSiege", "id", id));

        Long departId = existing.getDepart() != null ? existing.getDepart().getId() : null;

        if (dto.getDepartId() != null &&
            (existing.getDepart() == null || !dto.getDepartId().equals(existing.getDepart().getId()))) {
            Depart depart = departRepository.findById(dto.getDepartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", dto.getDepartId()));
            existing.setDepart(depart);
        }

        if (dto.getRefSiegeCategorieId() != null &&
            (existing.getRefSiegeCategorie() == null || !dto.getRefSiegeCategorieId().equals(existing.getRefSiegeCategorie().getId()))) {
            RefSiegeCategorie categorie = refSiegeCategorieRepository.findById(dto.getRefSiegeCategorieId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefSiegeCategorie", "id", dto.getRefSiegeCategorieId()));
            existing.setRefSiegeCategorie(categorie);
        }

        if (dto.getRefDeviseId() != null &&
            (existing.getRefDevise() == null || !dto.getRefDeviseId().equals(existing.getRefDevise().getId()))) {
            RefDevise devise = refDeviseRepository.findById(dto.getRefDeviseId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefDevise", "id", dto.getRefDeviseId()));
            existing.setRefDevise(devise);
        }

        if (dto.getMontant() != null) {
            existing.setMontant(dto.getMontant());
        }

        DepartTarifSiege saved = departTarifSiegeRepository.save(existing);
        reservationService.recalculateConfirmedReservationsForDepart(departId != null ? departId : dto.getDepartId());
        return departTarifSiegeMapper.toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        DepartTarifSiege existing = departTarifSiegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartTarifSiege", "id", id));
        Long departId = existing.getDepart() != null ? existing.getDepart().getId() : null;
        departTarifSiegeRepository.delete(existing);
        reservationService.recalculateConfirmedReservationsForDepart(departId);
    }
}
