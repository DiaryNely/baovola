package com.taxi_brousse.service;

import com.taxi_brousse.dto.ClientDTO;
import com.taxi_brousse.entity.Client;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.ClientMapper;
import com.taxi_brousse.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public List<ClientDTO> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ClientDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
        return clientMapper.toDTO(client);
    }

    public ClientDTO create(ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toDTO(savedClient);
    }

    public ClientDTO update(Long id, ClientDTO clientDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        existingClient.setNom(clientDTO.getNom());
        existingClient.setPrenom(clientDTO.getPrenom());
        existingClient.setTelephone(clientDTO.getTelephone());
        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setNumeroCin(clientDTO.getNumeroCin());

        Client updatedClient = clientRepository.save(existingClient);
        return clientMapper.toDTO(updatedClient);
    }

    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
        clientRepository.delete(client);
    }
}
