package com.taxi_brousse.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi_brousse.dto.SocietePublicitaireLiteDTO;
import com.taxi_brousse.repository.SocietePublicitaireRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/societes-publicitaires")
@RequiredArgsConstructor
public class SocietePublicitaireController {

    private final SocietePublicitaireRepository societePublicitaireRepository;

    @GetMapping("/actives")
    public List<SocietePublicitaireLiteDTO> listActives() {
        return societePublicitaireRepository.findByActifTrueOrderByNomAsc().stream()
                .map(s -> new SocietePublicitaireLiteDTO(s.getId(), s.getCode(), s.getNom()))
                .collect(Collectors.toList());
    }
}
