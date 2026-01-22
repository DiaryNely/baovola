package com.taxi_brousse.service;

@org.springframework.stereotype.Service
@lombok.RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class PubliciteService {

    private final com.taxi_brousse.repository.PubliciteRepository publiciteRepository;
    private final com.taxi_brousse.repository.SocietePublicitaireRepository societePublicitaireRepository;
    private final com.taxi_brousse.repository.DepartPubliciteRepository departPubliciteRepository;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<com.taxi_brousse.dto.PubliciteDTO> findAll() {
        return publiciteRepository.findAll().stream()
            .map(this::toDTO)
            .collect(java.util.stream.Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<com.taxi_brousse.dto.PubliciteDTO> findBySocieteId(Long societeId) {
        return publiciteRepository.findBySocietePublicitaireId(societeId).stream()
            .map(this::toDTO)
            .collect(java.util.stream.Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public com.taxi_brousse.dto.PubliciteDTO findById(Long id) {
        com.taxi_brousse.entity.Publicite publicite = publiciteRepository.findById(id)
                .orElseThrow(() -> new com.taxi_brousse.exception.ResourceNotFoundException("Publicite", "id", id));
        return toDTO(publicite);
    }

    public com.taxi_brousse.dto.PubliciteDTO create(com.taxi_brousse.dto.PubliciteDTO dto) {
        validate(dto, null);

        com.taxi_brousse.entity.SocietePublicitaire societe = societePublicitaireRepository.findById(dto.getSocietePublicitaireId())
                .orElseThrow(() -> new com.taxi_brousse.exception.ResourceNotFoundException("SocietePublicitaire", "id", dto.getSocietePublicitaireId()));

        com.taxi_brousse.entity.Publicite entity = new com.taxi_brousse.entity.Publicite();
        updateEntity(entity, dto, societe);
        entity = publiciteRepository.save(entity);
        return toDTO(entity);
    }

    public com.taxi_brousse.dto.PubliciteDTO update(Long id, com.taxi_brousse.dto.PubliciteDTO dto) {
        com.taxi_brousse.entity.Publicite entity = publiciteRepository.findById(id)
                .orElseThrow(() -> new com.taxi_brousse.exception.ResourceNotFoundException("Publicite", "id", id));

        validate(dto, entity.getId());

        com.taxi_brousse.entity.SocietePublicitaire societe = entity.getSocietePublicitaire();
        if (dto.getSocietePublicitaireId() != null && !dto.getSocietePublicitaireId().equals(societe.getId())) {
            societe = societePublicitaireRepository.findById(dto.getSocietePublicitaireId())
                    .orElseThrow(() -> new com.taxi_brousse.exception.ResourceNotFoundException("SocietePublicitaire", "id", dto.getSocietePublicitaireId()));
        }

        updateEntity(entity, dto, societe);
        entity = publiciteRepository.save(entity);
        return toDTO(entity);
    }

    public void delete(Long id) {
        com.taxi_brousse.entity.Publicite entity = publiciteRepository.findById(id)
                .orElseThrow(() -> new com.taxi_brousse.exception.ResourceNotFoundException("Publicite", "id", id));

        Long count = departPubliciteRepository.countByPubliciteId(id);
        if (count != null && count > 0) {
            throw new com.taxi_brousse.exception.BadRequestException("Impossible de supprimer: la publicité a déjà des diffusions");
        }
        publiciteRepository.delete(entity);
    }

    private void validate(com.taxi_brousse.dto.PubliciteDTO dto, Long excludeId) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new com.taxi_brousse.exception.BadRequestException("Le code est obligatoire");
        }
        if (dto.getTitre() == null || dto.getTitre().isBlank()) {
            throw new com.taxi_brousse.exception.BadRequestException("Le titre est obligatoire");
        }
        if (dto.getSocietePublicitaireId() == null) {
            throw new com.taxi_brousse.exception.BadRequestException("La société est obligatoire");
        }
        if (dto.getDateDebutValidite() == null) {
            throw new com.taxi_brousse.exception.BadRequestException("La date de début est obligatoire");
        }
        if (dto.getDateFinValidite() == null) {
            throw new com.taxi_brousse.exception.BadRequestException("La date de fin est obligatoire");
        }
        if (dto.getDateFinValidite().isBefore(dto.getDateDebutValidite())) {
            throw new com.taxi_brousse.exception.BadRequestException("La date de fin doit être >= date de début");
        }
        if (dto.getDureeSecondes() != null && dto.getDureeSecondes() <= 0) {
            throw new com.taxi_brousse.exception.BadRequestException("La durée doit être positive");
        }

        com.taxi_brousse.entity.Publicite existing = publiciteRepository.findByCode(dto.getCode()).orElse(null);
        if (existing != null && (excludeId == null || !existing.getId().equals(excludeId))) {
            throw new com.taxi_brousse.exception.BadRequestException("Le code existe déjà");
        }
    }

    private void updateEntity(com.taxi_brousse.entity.Publicite entity, com.taxi_brousse.dto.PubliciteDTO dto, com.taxi_brousse.entity.SocietePublicitaire societe) {
        if (dto.getCode() != null) {
            entity.setCode(dto.getCode());
        }
        if (dto.getTitre() != null) {
            entity.setTitre(dto.getTitre());
        }
        entity.setDescription(dto.getDescription());
        entity.setUrlVideo(dto.getUrlVideo());
        entity.setDureeSecondes(dto.getDureeSecondes());
        if (dto.getDateDebutValidite() != null) {
            entity.setDateDebutValidite(dto.getDateDebutValidite());
        }
        if (dto.getDateFinValidite() != null) {
            entity.setDateFinValidite(dto.getDateFinValidite());
        }
        if (dto.getActif() != null) {
            entity.setActif(dto.getActif());
        }
        if (societe != null) {
            entity.setSocietePublicitaire(societe);
        }
    }

    private com.taxi_brousse.dto.PubliciteDTO toDTO(com.taxi_brousse.entity.Publicite entity) {
        com.taxi_brousse.dto.PubliciteDTO dto = new com.taxi_brousse.dto.PubliciteDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setTitre(entity.getTitre());
        dto.setDescription(entity.getDescription());
        dto.setUrlVideo(entity.getUrlVideo());
        dto.setDureeSecondes(entity.getDureeSecondes());
        dto.setDateDebutValidite(entity.getDateDebutValidite());
        dto.setDateFinValidite(entity.getDateFinValidite());
        dto.setActif(entity.getActif());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getSocietePublicitaire() != null) {
            dto.setSocietePublicitaireId(entity.getSocietePublicitaire().getId());
            dto.setSocietePublicitaireNom(entity.getSocietePublicitaire().getNom());
        }
        return dto;
    }
}
