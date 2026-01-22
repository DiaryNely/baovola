package com.taxi_brousse.service;

import com.taxi_brousse.dto.CooperativeDTO;
import com.taxi_brousse.entity.Cooperative;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.CooperativeMapper;
import com.taxi_brousse.repository.CooperativeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CooperativeService {

    private final CooperativeRepository cooperativeRepository;
    private final CooperativeMapper cooperativeMapper;

    public List<CooperativeDTO> findAll() {
        return cooperativeRepository.findAll().stream()
                .map(cooperativeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CooperativeDTO findById(Long id) {
        Cooperative cooperative = cooperativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative", "id", id));
        return cooperativeMapper.toDTO(cooperative);
    }

    public CooperativeDTO create(CooperativeDTO cooperativeDTO) {
        Cooperative cooperative = cooperativeMapper.toEntity(cooperativeDTO);
        Cooperative savedCooperative = cooperativeRepository.save(cooperative);
        return cooperativeMapper.toDTO(savedCooperative);
    }

    public CooperativeDTO update(Long id, CooperativeDTO cooperativeDTO) {
        Cooperative existingCooperative = cooperativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative", "id", id));

        existingCooperative.setCode(cooperativeDTO.getCode());
        existingCooperative.setNom(cooperativeDTO.getNom());
        existingCooperative.setTelephone(cooperativeDTO.getTelephone());
        existingCooperative.setEmail(cooperativeDTO.getEmail());
        existingCooperative.setAdresse(cooperativeDTO.getAdresse());

        Cooperative updatedCooperative = cooperativeRepository.save(existingCooperative);
        return cooperativeMapper.toDTO(updatedCooperative);
    }

    public void delete(Long id) {
        Cooperative cooperative = cooperativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative", "id", id));
        cooperativeRepository.delete(cooperative);
    }
}
