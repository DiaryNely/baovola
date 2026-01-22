package com.taxi_brousse.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi_brousse.dto.LieuDTO;
import com.taxi_brousse.entity.Lieu;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.repository.LieuRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lieux")
@RequiredArgsConstructor
public class LieuController {

    private final LieuRepository lieuRepository;

    @GetMapping
    public ResponseEntity<List<LieuDTO>> findAll() {
        List<LieuDTO> lieux = lieuRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lieux);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LieuDTO> findById(@PathVariable Long id) {
        Lieu lieu = lieuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lieu", "id", id));
        return ResponseEntity.ok(toDto(lieu));
    }

    private LieuDTO toDto(Lieu lieu) {
        if (lieu == null) {
            return null;
        }
        LieuDTO dto = new LieuDTO();
        dto.setId(lieu.getId());
        dto.setNom(lieu.getNom());
        dto.setDescription(lieu.getDescription());
        dto.setLatitude(lieu.getLatitude());
        dto.setLongitude(lieu.getLongitude());
        if (lieu.getRefLieuType() != null) {
            dto.setRefLieuTypeId(lieu.getRefLieuType().getId());
            dto.setRefLieuTypeLibelle(lieu.getRefLieuType().getLibelle());
        }
        return dto;
    }
}
