package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.ClientDTO;
import com.taxi_brousse.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientDTO toDTO(Client entity) {
        if (entity == null) {
            return null;
        }
        
        ClientDTO dto = new ClientDTO();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        dto.setTelephone(entity.getTelephone());
        dto.setEmail(entity.getEmail());
        dto.setNumeroCin(entity.getNumeroCin());
        dto.setCreatedAt(entity.getCreatedAt());
        
        return dto;
    }

    public Client toEntity(ClientDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Client entity = new Client();
        entity.setId(dto.getId());
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setTelephone(dto.getTelephone());
        entity.setEmail(dto.getEmail());
        entity.setNumeroCin(dto.getNumeroCin());
        
        return entity;
    }
}
