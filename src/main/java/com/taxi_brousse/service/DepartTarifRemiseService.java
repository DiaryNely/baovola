package com.taxi_brousse.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.DepartTarifRemiseDTO;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.DepartTarifRemise;
import com.taxi_brousse.entity.reference.RefPassagerCategorie;
import com.taxi_brousse.entity.reference.RefSiegeCategorie;
import com.taxi_brousse.exception.BadRequestException;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.DepartTarifRemiseRepository;
import com.taxi_brousse.repository.RefPassagerCategorieRepository;
import com.taxi_brousse.repository.RefSiegeCategorieRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartTarifRemiseService {

    private final DepartTarifRemiseRepository departTarifRemiseRepository;
    private final DepartRepository departRepository;
    private final RefSiegeCategorieRepository refSiegeCategorieRepository;
    private final RefPassagerCategorieRepository refPassagerCategorieRepository;
    private final ReservationService reservationService;

    @Transactional(readOnly = true)
    public List<DepartTarifRemiseDTO> getRemisesByDepart(Long departId) {
        return departTarifRemiseRepository.findByDepartId(departId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DepartTarifRemiseDTO> getAll() {
        return departTarifRemiseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DepartTarifRemiseDTO getById(Long id) {
        DepartTarifRemise remise = departTarifRemiseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartTarifRemise", "id", id));
        return toDTO(remise);
    }

    @Transactional
    public DepartTarifRemiseDTO create(DepartTarifRemiseDTO dto) {
        Depart depart = departRepository.findById(dto.getDepartId())
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", dto.getDepartId()));

        RefSiegeCategorie siegeCategorie = refSiegeCategorieRepository.findById(dto.getRefSiegeCategorieId())
                .orElseThrow(() -> new ResourceNotFoundException("RefSiegeCategorie", "id", dto.getRefSiegeCategorieId()));

        RefPassagerCategorie passagerCategorie = refPassagerCategorieRepository.findById(dto.getRefPassagerCategorieId())
                .orElseThrow(() -> new ResourceNotFoundException("RefPassagerCategorie", "id", dto.getRefPassagerCategorieId()));

        if (departTarifRemiseRepository.existsByDepartIdAndRefSiegeCategorieIdAndRefPassagerCategorieId(
                dto.getDepartId(), dto.getRefSiegeCategorieId(), dto.getRefPassagerCategorieId())) {
            throw new BadRequestException("Une remise existe déjà pour ce départ, cette catégorie de siège et cette catégorie de passager");
        }

        DepartTarifRemise entity = new DepartTarifRemise();
        entity.setDepart(depart);
        entity.setRefSiegeCategorie(siegeCategorie);
        entity.setRefPassagerCategorie(passagerCategorie);
        entity.setTypeRemise(dto.getTypeRemise());
        entity.setMontant(dto.getMontant());

        DepartTarifRemise saved = departTarifRemiseRepository.save(entity);
        reservationService.recalculateConfirmedReservationsForDepart(dto.getDepartId());
        return toDTO(saved);
    }

    @Transactional
    public DepartTarifRemiseDTO update(Long id, DepartTarifRemiseDTO dto) {
        DepartTarifRemise existing = departTarifRemiseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartTarifRemise", "id", id));

        Long departId = existing.getDepart() != null ? existing.getDepart().getId() : null;

        Long newDepartId = dto.getDepartId() != null ? dto.getDepartId() : (existing.getDepart() != null ? existing.getDepart().getId() : null);
        Long newSiegeCategorieId = dto.getRefSiegeCategorieId() != null ? dto.getRefSiegeCategorieId() : (existing.getRefSiegeCategorie() != null ? existing.getRefSiegeCategorie().getId() : null);
        Long newPassagerCategorieId = dto.getRefPassagerCategorieId() != null ? dto.getRefPassagerCategorieId() : (existing.getRefPassagerCategorie() != null ? existing.getRefPassagerCategorie().getId() : null);

        if (newDepartId != null && newSiegeCategorieId != null && newPassagerCategorieId != null) {
            boolean duplicate = departTarifRemiseRepository
                    .existsByDepartIdAndRefSiegeCategorieIdAndRefPassagerCategorieIdAndIdNot(
                            newDepartId, newSiegeCategorieId, newPassagerCategorieId, id);
            if (duplicate) {
                throw new BadRequestException("Une remise existe déjà pour ce départ, cette catégorie de siège et cette catégorie de passager");
            }
        }

        if (dto.getDepartId() != null && (existing.getDepart() == null || !dto.getDepartId().equals(existing.getDepart().getId()))) {
            Depart depart = departRepository.findById(dto.getDepartId())
                    .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", dto.getDepartId()));
            existing.setDepart(depart);
        }

        if (dto.getRefSiegeCategorieId() != null && (existing.getRefSiegeCategorie() == null || !dto.getRefSiegeCategorieId().equals(existing.getRefSiegeCategorie().getId()))) {
            RefSiegeCategorie siegeCategorie = refSiegeCategorieRepository.findById(dto.getRefSiegeCategorieId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefSiegeCategorie", "id", dto.getRefSiegeCategorieId()));
            existing.setRefSiegeCategorie(siegeCategorie);
        }

        if (dto.getRefPassagerCategorieId() != null && (existing.getRefPassagerCategorie() == null || !dto.getRefPassagerCategorieId().equals(existing.getRefPassagerCategorie().getId()))) {
            RefPassagerCategorie passagerCategorie = refPassagerCategorieRepository.findById(dto.getRefPassagerCategorieId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefPassagerCategorie", "id", dto.getRefPassagerCategorieId()));
            existing.setRefPassagerCategorie(passagerCategorie);
        }

        if (dto.getTypeRemise() != null) {
            existing.setTypeRemise(dto.getTypeRemise());
        }

        if (dto.getMontant() != null) {
            existing.setMontant(dto.getMontant());
        }

        DepartTarifRemise saved = departTarifRemiseRepository.save(existing);
        reservationService.recalculateConfirmedReservationsForDepart(departId != null ? departId : dto.getDepartId());
        return toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        DepartTarifRemise existing = departTarifRemiseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartTarifRemise", "id", id));
        Long departId = existing.getDepart() != null ? existing.getDepart().getId() : null;
        departTarifRemiseRepository.delete(existing);
        reservationService.recalculateConfirmedReservationsForDepart(departId);
    }

    private DepartTarifRemiseDTO toDTO(DepartTarifRemise entity) {
        DepartTarifRemiseDTO dto = new DepartTarifRemiseDTO();
        dto.setId(entity.getId());
        if (entity.getDepart() != null) {
            dto.setDepartId(entity.getDepart().getId());
        }
        if (entity.getRefSiegeCategorie() != null) {
            dto.setRefSiegeCategorieId(entity.getRefSiegeCategorie().getId());
            dto.setRefSiegeCategorieCode(entity.getRefSiegeCategorie().getCode());
            dto.setRefSiegeCategorieLibelle(entity.getRefSiegeCategorie().getLibelle());
        }
        if (entity.getRefPassagerCategorie() != null) {
            dto.setRefPassagerCategorieId(entity.getRefPassagerCategorie().getId());
            dto.setRefPassagerCategorieCode(entity.getRefPassagerCategorie().getCode());
            dto.setRefPassagerCategorieLibelle(entity.getRefPassagerCategorie().getLibelle());
        }
        dto.setTypeRemise(entity.getTypeRemise());
        dto.setMontant(entity.getMontant());
        return dto;
    }
}
