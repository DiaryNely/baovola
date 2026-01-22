package com.taxi_brousse.service;

import com.taxi_brousse.dto.BilletDTO;
import com.taxi_brousse.entity.Billet;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.BilletMapper;
import com.taxi_brousse.repository.BilletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BilletService {

    private final BilletRepository billetRepository;
    private final BilletMapper billetMapper;

    public List<BilletDTO> findAll() {
        return billetRepository.findAll().stream()
                .map(this::enrichBilletDTO)
                .collect(Collectors.toList());
    }

    public BilletDTO findById(Long id) {
        Billet billet = billetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Billet", "id", id));
        return enrichBilletDTO(billet);
    }

    public List<BilletDTO> findByReservationId(Long reservationId) {
        return billetRepository.findByReservationId(reservationId).stream()
                .map(this::enrichBilletDTO)
                .collect(Collectors.toList());
    }

    public BilletDTO findByNumero(String numero) {
        Billet billet = billetRepository.findByNumero(numero)
                .orElseThrow(() -> new ResourceNotFoundException("Billet", "numero", numero));
        return enrichBilletDTO(billet);
    }

    private BilletDTO enrichBilletDTO(Billet billet) {
        BilletDTO dto = billetMapper.toDTO(billet);
        
        if (billet.getReservation() != null && billet.getReservation().getDepart() != null) {
            var depart = billet.getReservation().getDepart();
            
            dto.setDepartCode(depart.getCode());
            dto.setDateHeureDepart(depart.getDateHeureDepart());
            
            if (depart.getTrajet() != null) {
                dto.setTrajetLibelle(depart.getTrajet().getLibelle());
            }
            
            if (depart.getLieuDepart() != null) {
                dto.setLieuDepartNom(depart.getLieuDepart().getNom());
            }
            
            if (depart.getLieuArrivee() != null) {
                dto.setLieuArriveeNom(depart.getLieuArrivee().getNom());
            }
            
            if (depart.getCooperative() != null) {
                dto.setCooperativeNom(depart.getCooperative().getNom());
            }
            
            if (depart.getVehicule() != null) {
                dto.setVehiculeImmatriculation(depart.getVehicule().getImmatriculation());
            }
        }
        
        return dto;
    }
}
