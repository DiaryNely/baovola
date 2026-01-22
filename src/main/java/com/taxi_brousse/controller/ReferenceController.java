package com.taxi_brousse.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxi_brousse.entity.reference.RefChauffeurVehiculeRole;
import com.taxi_brousse.entity.reference.RefDepartStatut;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefLieuType;
import com.taxi_brousse.entity.reference.RefPassagerCategorie;
import com.taxi_brousse.entity.reference.RefSiegeCategorie;
import com.taxi_brousse.entity.reference.RefVehiculeEtat;
import com.taxi_brousse.entity.reference.RefVehiculeType;
import com.taxi_brousse.repository.RefChauffeurVehiculeRoleRepository;
import com.taxi_brousse.repository.RefDepartStatutRepository;
import com.taxi_brousse.repository.RefDeviseRepository;
import com.taxi_brousse.repository.RefLieuTypeRepository;
import com.taxi_brousse.repository.RefPassagerCategorieRepository;
import com.taxi_brousse.repository.RefSiegeCategorieRepository;
import com.taxi_brousse.repository.RefVehiculeEtatRepository;
import com.taxi_brousse.repository.RefVehiculeTypeRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final RefDepartStatutRepository refDepartStatutRepository;
    private final RefLieuTypeRepository refLieuTypeRepository;
    private final RefVehiculeEtatRepository refVehiculeEtatRepository;
    private final RefVehiculeTypeRepository refVehiculeTypeRepository;
    private final RefChauffeurVehiculeRoleRepository refChauffeurVehiculeRoleRepository;
    private final RefDeviseRepository refDeviseRepository;
    private final RefPassagerCategorieRepository refPassagerCategorieRepository;
    private final RefSiegeCategorieRepository refSiegeCategorieRepository;
    private final com.taxi_brousse.repository.RefPaiementMethodeRepository refPaiementMethodeRepository;
    private final com.taxi_brousse.repository.RefPaiementStatutRepository refPaiementStatutRepository;

    @GetMapping("/depart-statuts")
    public ResponseEntity<List<RefDepartStatut>> getDepartStatuts() {
        List<RefDepartStatut> statuts = refDepartStatutRepository.findByActifTrue();
        return ResponseEntity.ok(statuts);
    }
    
    @GetMapping("/depart-statuts/all")
    public ResponseEntity<List<RefDepartStatut>> getAllDepartStatuts() {
        List<RefDepartStatut> statuts = refDepartStatutRepository.findAll();
        return ResponseEntity.ok(statuts);
    }
    
    @GetMapping("/depart-statuts/{id}")
    public ResponseEntity<RefDepartStatut> getDepartStatutById(@PathVariable Long id) {
        RefDepartStatut statut = refDepartStatutRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Statut de départ non trouvé avec l'ID: " + id));
        return ResponseEntity.ok(statut);
    }

    @GetMapping("/lieu-types")
    public ResponseEntity<List<RefLieuType>> getLieuTypes() {
        List<RefLieuType> types = refLieuTypeRepository.findByActifTrue();
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/lieu-types/all")
    public ResponseEntity<List<RefLieuType>> getAllLieuTypes() {
        List<RefLieuType> types = refLieuTypeRepository.findAll();
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/lieu-types/{id}")
    public ResponseEntity<RefLieuType> getLieuTypeById(@PathVariable Long id) {
        RefLieuType type = refLieuTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Type de lieu non trouvé avec l'ID: " + id));
        return ResponseEntity.ok(type);
    }

    @GetMapping("/vehicule-etats")
    public ResponseEntity<List<RefVehiculeEtat>> getVehiculeEtats() {
        List<RefVehiculeEtat> etats = refVehiculeEtatRepository.findByActifTrue();
        return ResponseEntity.ok(etats);
    }
    
    @GetMapping("/vehicule-etats/all")
    public ResponseEntity<List<RefVehiculeEtat>> getAllVehiculeEtats() {
        List<RefVehiculeEtat> etats = refVehiculeEtatRepository.findAll();
        return ResponseEntity.ok(etats);
    }
    
    @GetMapping("/vehicule-etats/{id}")
    public ResponseEntity<RefVehiculeEtat> getVehiculeEtatById(@PathVariable Long id) {
        RefVehiculeEtat etat = refVehiculeEtatRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("État de véhicule non trouvé avec l'ID: " + id));
        return ResponseEntity.ok(etat);
    }

    @GetMapping("/vehicule-types")
    public ResponseEntity<List<RefVehiculeType>> getVehiculeTypes() {
        List<RefVehiculeType> types = refVehiculeTypeRepository.findByActifTrue();
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/vehicule-types/all")
    public ResponseEntity<List<RefVehiculeType>> getAllVehiculeTypes() {
        List<RefVehiculeType> types = refVehiculeTypeRepository.findAll();
        return ResponseEntity.ok(types);
    }
    
    @GetMapping("/vehicule-types/{id}")
    public ResponseEntity<RefVehiculeType> getVehiculeTypeById(@PathVariable Long id) {
        RefVehiculeType type = refVehiculeTypeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Type de véhicule non trouvé avec l'ID: " + id));
        return ResponseEntity.ok(type);
    }

    @GetMapping("/chauffeur-vehicule-roles")
    public ResponseEntity<List<RefChauffeurVehiculeRole>> getChauffeurVehiculeRoles() {
        List<RefChauffeurVehiculeRole> roles = refChauffeurVehiculeRoleRepository.findByActifTrueOrderByLibelleAsc();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/chauffeur-vehicule-roles/all")
    public ResponseEntity<List<RefChauffeurVehiculeRole>> getAllChauffeurVehiculeRoles() {
        List<RefChauffeurVehiculeRole> roles = refChauffeurVehiculeRoleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/chauffeur-vehicule-roles/{id}")
    public ResponseEntity<RefChauffeurVehiculeRole> getChauffeurVehiculeRoleById(@PathVariable Long id) {
        RefChauffeurVehiculeRole role = refChauffeurVehiculeRoleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rôle chauffeur-véhicule non trouvé avec l'ID: " + id));
        return ResponseEntity.ok(role);
    }

    @GetMapping("/devises")
    public ResponseEntity<List<RefDevise>> getDevises() {
        List<RefDevise> devises = refDeviseRepository.findByActifTrue();
        return ResponseEntity.ok(devises);
    }
    
    @GetMapping("/devises/all")
    public ResponseEntity<List<RefDevise>> getAllDevises() {
        List<RefDevise> devises = refDeviseRepository.findAll();
        return ResponseEntity.ok(devises);
    }
    
    @GetMapping("/devises/{id}")
    public ResponseEntity<RefDevise> getDeviseById(@PathVariable Long id) {
        RefDevise devise = refDeviseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Devise non trouvée avec l'ID: " + id));
        return ResponseEntity.ok(devise);
    }

    @GetMapping("/siege-categories")
    public ResponseEntity<List<RefSiegeCategorie>> getSiegeCategories() {
        List<RefSiegeCategorie> categories = refSiegeCategorieRepository.findByActifTrueOrderByOrdreAsc();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/siege-categories/all")
    public ResponseEntity<List<RefSiegeCategorie>> getAllSiegeCategories() {
        List<RefSiegeCategorie> categories = refSiegeCategorieRepository.findAllByOrderByOrdreAsc();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/siege-categories/{id}")
    public ResponseEntity<RefSiegeCategorie> getSiegeCategorieById(@PathVariable Long id) {
        RefSiegeCategorie categorie = refSiegeCategorieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Catégorie de siège non trouvée avec l'ID: " + id));
        return ResponseEntity.ok(categorie);
    }

    @GetMapping("/passager-categories")
    public ResponseEntity<List<RefPassagerCategorie>> getPassagerCategories() {
        List<RefPassagerCategorie> categories = refPassagerCategorieRepository.findByActifTrueOrderByOrdreAsc();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/passager-categories/all")
    public ResponseEntity<List<RefPassagerCategorie>> getAllPassagerCategories() {
        List<RefPassagerCategorie> categories = refPassagerCategorieRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/passager-categories/{id}")
    public ResponseEntity<RefPassagerCategorie> getPassagerCategorieById(@PathVariable Long id) {
        RefPassagerCategorie categorie = refPassagerCategorieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Catégorie de passager non trouvée avec l'ID: " + id));
        return ResponseEntity.ok(categorie);
    }

    @GetMapping("/paiement-methodes")
    public ResponseEntity<List<com.taxi_brousse.entity.reference.RefPaiementMethode>> getPaiementMethodes() {
        List<com.taxi_brousse.entity.reference.RefPaiementMethode> methodes = refPaiementMethodeRepository.findByActifTrue();
        return ResponseEntity.ok(methodes);
    }

    @GetMapping("/paiement-methodes/all")
    public ResponseEntity<List<com.taxi_brousse.entity.reference.RefPaiementMethode>> getAllPaiementMethodes() {
        List<com.taxi_brousse.entity.reference.RefPaiementMethode> methodes = refPaiementMethodeRepository.findAll();
        return ResponseEntity.ok(methodes);
    }

    @GetMapping("/paiement-methodes/{id}")
    public ResponseEntity<com.taxi_brousse.entity.reference.RefPaiementMethode> getPaiementMethodeById(@PathVariable Long id) {
        com.taxi_brousse.entity.reference.RefPaiementMethode methode = refPaiementMethodeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Méthode de paiement non trouvée avec l'ID: " + id));
        return ResponseEntity.ok(methode);
    }

    @GetMapping("/paiement-statuts")
    public ResponseEntity<List<com.taxi_brousse.entity.reference.RefPaiementStatut>> getPaiementStatuts() {
        List<com.taxi_brousse.entity.reference.RefPaiementStatut> statuts = refPaiementStatutRepository.findByActifTrue();
        return ResponseEntity.ok(statuts);
    }

    @GetMapping("/paiement-statuts/all")
    public ResponseEntity<List<com.taxi_brousse.entity.reference.RefPaiementStatut>> getAllPaiementStatuts() {
        List<com.taxi_brousse.entity.reference.RefPaiementStatut> statuts = refPaiementStatutRepository.findAll();
        return ResponseEntity.ok(statuts);
    }

    @GetMapping("/paiement-statuts/{id}")
    public ResponseEntity<com.taxi_brousse.entity.reference.RefPaiementStatut> getPaiementStatutById(@PathVariable Long id) {
        com.taxi_brousse.entity.reference.RefPaiementStatut statut = refPaiementStatutRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Statut de paiement non trouvé avec l'ID: " + id));
        return ResponseEntity.ok(statut);
    }
}
