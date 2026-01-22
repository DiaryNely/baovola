package com.taxi_brousse.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taxi_brousse.dto.DepartPubliciteDTO;
import com.taxi_brousse.entity.Depart;
import com.taxi_brousse.entity.DepartPublicite;
import com.taxi_brousse.entity.Publicite;
import com.taxi_brousse.entity.TarifPublicite;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.exception.BadRequestException;
import com.taxi_brousse.exception.ResourceNotFoundException;
import com.taxi_brousse.mapper.DepartPubliciteMapper;
import com.taxi_brousse.repository.DepartPubliciteRepository;
import com.taxi_brousse.repository.DepartRepository;
import com.taxi_brousse.repository.PubliciteRepository;
import com.taxi_brousse.repository.TarifPubliciteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartPubliciteService {
    
    private final DepartPubliciteRepository departPubliciteRepository;
    private final DepartRepository departRepository;
    private final PubliciteRepository publiciteRepository;
    private final TarifPubliciteRepository tarifPubliciteRepository;
    private final DepartPubliciteMapper departPubliciteMapper;
    
    /**
     * Récupère toutes les diffusions d'un départ
     */
    @Transactional(readOnly = true)
    public List<DepartPubliciteDTO> findByDepartId(Long departId) {
        return departPubliciteRepository.findByDepartId(departId).stream()
                .map(departPubliciteMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère toutes les diffusions d'une publicité
     */
    @Transactional(readOnly = true)
    public List<DepartPubliciteDTO> findByPubliciteId(Long publiciteId) {
        return departPubliciteRepository.findByPubliciteId(publiciteId).stream()
                .map(departPubliciteMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère une diffusion par ID
     */
    @Transactional(readOnly = true)
    public DepartPubliciteDTO findById(Long id) {
        DepartPublicite diffusion = departPubliciteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartPublicite", "id", id));
        return departPubliciteMapper.toDTO(diffusion);
    }
    
    /**
     * Crée une nouvelle diffusion pour un départ
     * Logique métier :
     * - Vérifier que la publicité est active et valide à la date du départ
     * - Utiliser le tarif en vigueur à la date du départ
     * - Calculer automatiquement le montant à facturer
     * - Vérifier qu'on ne duplique pas une association départ-publicité
     */
    public DepartPubliciteDTO create(DepartPubliciteDTO dto) {
        // 1. Récupérer les entités
        Depart depart = departRepository.findById(dto.getDepartId())
                .orElseThrow(() -> new ResourceNotFoundException("Depart", "id", dto.getDepartId()));
        
        Publicite publicite = publiciteRepository.findById(dto.getPubliciteId())
                .orElseThrow(() -> new ResourceNotFoundException("Publicite", "id", dto.getPubliciteId()));
        
        // 2. Validations métier
        validerDiffusion(dto, depart, publicite);
        
        // 3. Récupérer le tarif en vigueur à la date du départ
        LocalDate dateDepart = depart.getDateHeureDepart().toLocalDate();
        TarifPublicite tarif = tarifPubliciteRepository.findTarifEnVigueur(dateDepart)
                .orElseThrow(() -> new BadRequestException(
                    "Aucun tarif de publicité n'est défini pour la date du départ : " + dateDepart));
        
        // 4. Récupérer la devise du tarif
        RefDevise devise = tarif.getRefDevise();
        
        // 5. Valider et définir le nombre de répétitions
        if (dto.getNombreRepetitions() == null || dto.getNombreRepetitions() < 1) {
            dto.setNombreRepetitions(1);
        }
        
        System.out.println("=== DEBUG CALCUL MONTANT ===");
        System.out.println("Tarif unitaire: " + tarif.getMontant());
        System.out.println("Nombre de répétitions: " + dto.getNombreRepetitions());
        
        // 6. Calculer le montant total : tarif unitaire × nombre de répétitions
        java.math.BigDecimal montantTotal = tarif.getMontant()
                .multiply(java.math.BigDecimal.valueOf(dto.getNombreRepetitions()));
        
        System.out.println("Montant total calculé: " + montantTotal);
        
        dto.setMontantFacture(montantTotal);
        dto.setRefDeviseId(devise.getId());
        dto.setTarifPubliciteId(tarif.getId());
        
        // 7. Définir la date de diffusion si non fournie
        if (dto.getDateDiffusion() == null) {
            dto.setDateDiffusion(depart.getDateHeureDepart());
        }
        
        // 8. Définir le statut par défaut
        if (dto.getStatutDiffusion() == null) {
            dto.setStatutDiffusion("PLANIFIE");
        }
        
        // 9. Créer l'entité
        DepartPublicite diffusion = departPubliciteMapper.toEntity(dto, depart, publicite, tarif, devise);
        diffusion = departPubliciteRepository.save(diffusion);
        
        return departPubliciteMapper.toDTO(diffusion);
    }
    
    /**
     * Supprime une diffusion
     */
    public void delete(Long id) {
        DepartPublicite diffusion = departPubliciteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartPublicite", "id", id));
        
        // Vérifier que la diffusion n'a pas encore eu lieu
        if ("DIFFUSE".equals(diffusion.getStatutDiffusion())) {
            throw new BadRequestException("Impossible de supprimer une diffusion déjà effectuée");
        }
        
        departPubliciteRepository.delete(diffusion);
    }
    
    /**
     * Met à jour le statut d'une diffusion
     */
    public DepartPubliciteDTO updateStatut(Long id, String statut) {
        DepartPublicite diffusion = departPubliciteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DepartPublicite", "id", id));
        
        // Valider le statut
        if (!List.of("PLANIFIE", "DIFFUSE", "ANNULE").contains(statut)) {
            throw new BadRequestException("Statut invalide. Valeurs autorisées : PLANIFIE, DIFFUSE, ANNULE");
        }
        
        diffusion.setStatutDiffusion(statut);
        diffusion = departPubliciteRepository.save(diffusion);
        
        return departPubliciteMapper.toDTO(diffusion);
    }
    
    /**
     * Récupère les statistiques d'un départ
     */
    @Transactional(readOnly = true)
    public DepartPubliciteStatsDTO getStatsByDepart(Long departId) {
        List<DepartPublicite> diffusions = departPubliciteRepository.findByDepartId(departId);
        List<DepartPublicite> diffusionsDiffusees = diffusions.stream()
            .filter(d -> "DIFFUSE".equalsIgnoreCase(d.getStatutDiffusion()))
            .toList();
        
        DepartPubliciteStatsDTO stats = new DepartPubliciteStatsDTO();
        
        // Sommer le nombre de répétitions de toutes les diffusions
        long totalRepetitions = diffusionsDiffusees.stream()
            .mapToLong(d -> d.getNombreRepetitions() == null ? 1L : d.getNombreRepetitions().longValue())
                .sum();
        
        stats.setNombreDiffusions(totalRepetitions);
        stats.setMontantTotal(diffusionsDiffusees.stream()
                .map(DepartPublicite::getMontantFacture)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        
        if (!diffusionsDiffusees.isEmpty()) {
            stats.setDeviseCode(diffusionsDiffusees.get(0).getRefDevise().getCode());
            stats.setDeviseSymbole(diffusionsDiffusees.get(0).getRefDevise().getSymbole());
        }
        
        return stats;
    }
    
    /**
     * Validations métier
     */
    private void validerDiffusion(DepartPubliciteDTO dto, Depart depart, Publicite publicite) {
        // 1. Vérifier que la publicité est active
        if (!publicite.getActif()) {
            throw new BadRequestException("La publicité '" + publicite.getTitre() + "' n'est pas active");
        }
        
        // 2. Vérifier que la publicité est valide à la date du départ
        LocalDate dateDepart = depart.getDateHeureDepart().toLocalDate();
        
        if (dateDepart.isBefore(publicite.getDateDebutValidite())) {
            throw new BadRequestException(
                "La publicité '" + publicite.getTitre() + "' n'est pas encore valide à la date du départ. " +
                "Date de début : " + publicite.getDateDebutValidite());
        }
        
        if (publicite.getDateFinValidite() != null && dateDepart.isAfter(publicite.getDateFinValidite())) {
            throw new BadRequestException(
                "La publicité '" + publicite.getTitre() + "' n'est plus valide à la date du départ. " +
                "Date de fin : " + publicite.getDateFinValidite());
        }
        
        // 3. Vérifier qu'il n'y a pas déjà une diffusion de cette publicité pour ce départ
        if (departPubliciteRepository.existsByDepartIdAndPubliciteId(dto.getDepartId(), dto.getPubliciteId())) {
            throw new BadRequestException(
                "Cette publicité est déjà associée à ce départ");
        }
    }
    
    /**
     * Classe interne pour les statistiques
     */
    @lombok.Data
    public static class DepartPubliciteStatsDTO {
        private Long nombreDiffusions;
        private java.math.BigDecimal montantTotal;
        private String deviseCode;
        private String deviseSymbole;
    }
}
