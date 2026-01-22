package com.taxi_brousse.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taxi_brousse.dto.PubliciteDTO;
import com.taxi_brousse.entity.Publicite;
import com.taxi_brousse.repository.PubliciteRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/publicites")
@RequiredArgsConstructor
public class PubliciteController {
    
    private final PubliciteRepository publiciteRepository;
    
    /**
     * Récupère toutes les publicités actives
     */
    @GetMapping("/actives")
    public ResponseEntity<List<PubliciteDTO>> getPublicitesActives() {
        List<Publicite> publicites = publiciteRepository.findByActifTrue();
        List<PubliciteDTO> dtos = publicites.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Récupère les publicités valides à une date donnée
     */
    @GetMapping("/valides")
    public ResponseEntity<List<PubliciteDTO>> getPublicitesValides(
            @RequestParam(required = false) String date) {
        LocalDate dateRecherche = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        List<Publicite> publicites = publiciteRepository.findValidesADate(dateRecherche);
        List<PubliciteDTO> dtos = publicites.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Récupère une publicité par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PubliciteDTO> getById(@PathVariable Long id) {
        Publicite publicite = publiciteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicité non trouvée"));
        return ResponseEntity.ok(toDTO(publicite));
    }
    
    /**
     * Convertit une entité en DTO
     */
    private PubliciteDTO toDTO(Publicite entity) {
        PubliciteDTO dto = new PubliciteDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setTitre(entity.getTitre());
        dto.setDescription(entity.getDescription());
        dto.setUrlVideo(entity.getUrlVideo());
        dto.setDureeSecondes(entity.getDureeSecondes());
        dto.setDateDebutValidite(entity.getDateDebutValidite());
        dto.setDateFinValidite(entity.getDateFinValidite());
        dto.setActif(entity.getActif());
        dto.setSocietePublicitaireId(entity.getSocietePublicitaire().getId());
        dto.setSocietePublicitaireNom(entity.getSocietePublicitaire().getNom());
        return dto;
    }
}
