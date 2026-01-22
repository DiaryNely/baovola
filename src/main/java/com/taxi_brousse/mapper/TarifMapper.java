package com.taxi_brousse.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.taxi_brousse.dto.TarifDTO;
import com.taxi_brousse.entity.Cooperative;
import com.taxi_brousse.entity.Tarif;
import com.taxi_brousse.entity.Trajet;
import com.taxi_brousse.entity.reference.RefDevise;
import com.taxi_brousse.entity.reference.RefVehiculeType;

@Component
public class TarifMapper {

    public TarifDTO toDTO(Tarif tarif) {
        if (tarif == null) {
            return null;
        }

        TarifDTO dto = new TarifDTO();
        dto.setId(tarif.getId());
        
        // IDs
        dto.setCooperativeId(tarif.getCooperative() != null ? tarif.getCooperative().getId() : null);
        dto.setTrajetId(tarif.getTrajet() != null ? tarif.getTrajet().getId() : null);
        dto.setRefVehiculeTypeId(tarif.getRefVehiculeType() != null ? tarif.getRefVehiculeType().getId() : null);
        dto.setRefDeviseId(tarif.getRefDevise() != null ? tarif.getRefDevise().getId() : null);
        
        // Données enrichies
        dto.setCooperativeNom(tarif.getCooperative() != null ? tarif.getCooperative().getNom() : null);
        dto.setTrajetLibelle(tarif.getTrajet() != null ? tarif.getTrajet().getLibelle() : null);
        dto.setTrajetCode(tarif.getTrajet() != null ? tarif.getTrajet().getCode() : null);
        dto.setVehiculeTypeLibelle(tarif.getRefVehiculeType() != null ? tarif.getRefVehiculeType().getLibelle() : null);
        dto.setDeviseCode(tarif.getRefDevise() != null ? tarif.getRefDevise().getCode() : null);
        
        // Données du tarif
        dto.setMontant(tarif.getMontant());
        dto.setDateDebut(tarif.getDateDebut());
        dto.setDateFin(tarif.getDateFin());
        
        // Vérifier si actif
        LocalDate today = LocalDate.now();
        boolean actif = (tarif.getDateDebut() == null || !tarif.getDateDebut().isAfter(today))
                     && (tarif.getDateFin() == null || !tarif.getDateFin().isBefore(today));
        dto.setActif(actif);
        
        return dto;
    }

    public Tarif toEntity(TarifDTO dto) {
        if (dto == null) {
            return null;
        }

        Tarif tarif = new Tarif();
        tarif.setId(dto.getId());
        
        // Relations - on crée des objets avec juste l'ID
        // Le service devra charger les objets complets
        if (dto.getCooperativeId() != null) {
            Cooperative cooperative = new Cooperative();
            cooperative.setId(dto.getCooperativeId());
            tarif.setCooperative(cooperative);
        }
        
        if (dto.getTrajetId() != null) {
            Trajet trajet = new Trajet();
            trajet.setId(dto.getTrajetId());
            tarif.setTrajet(trajet);
        }
        
        if (dto.getRefVehiculeTypeId() != null) {
            RefVehiculeType vehiculeType = new RefVehiculeType();
            vehiculeType.setId(dto.getRefVehiculeTypeId());
            tarif.setRefVehiculeType(vehiculeType);
        }
        
        if (dto.getRefDeviseId() != null) {
            RefDevise devise = new RefDevise();
            devise.setId(dto.getRefDeviseId());
            tarif.setRefDevise(devise);
        }
        
        // Données du tarif
        tarif.setMontant(dto.getMontant());
        tarif.setDateDebut(dto.getDateDebut());
        tarif.setDateFin(dto.getDateFin());
        
        return tarif;
    }
}
