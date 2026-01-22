package com.taxi_brousse.mapper;

import com.taxi_brousse.dto.BilletDTO;
import com.taxi_brousse.entity.Billet;
import org.springframework.stereotype.Component;

@Component
public class BilletMapper {

    public BilletDTO toDTO(Billet billet) {
        if (billet == null) {
            return null;
        }

        BilletDTO dto = new BilletDTO();
        dto.setId(billet.getId());
        dto.setNumero(billet.getNumero());
        dto.setCode(billet.getCode());
        dto.setPassagerNom(billet.getPassagerNom());
        dto.setPassagerPrenom(billet.getPassagerPrenom());
        dto.setNumeroSiege(billet.getNumeroSiege());
        dto.setDateEmission(billet.getDateEmission());
        dto.setContenuQr(billet.getContenuQr());

        if (billet.getReservation() != null) {
            dto.setReservationId(billet.getReservation().getId());
            dto.setReservationCode(billet.getReservation().getCode());
        }

        if (billet.getRefBilletStatut() != null) {
            dto.setRefBilletStatutId(billet.getRefBilletStatut().getId());
            dto.setRefBilletStatutCode(billet.getRefBilletStatut().getCode());
            dto.setRefBilletStatutLibelle(billet.getRefBilletStatut().getLibelle());
        }

        return dto;
    }

    public Billet toEntity(BilletDTO dto) {
        if (dto == null) {
            return null;
        }

        Billet billet = new Billet();
        billet.setId(dto.getId());
        billet.setNumero(dto.getNumero());
        billet.setCode(dto.getCode());
        billet.setPassagerNom(dto.getPassagerNom());
        billet.setPassagerPrenom(dto.getPassagerPrenom());
        billet.setNumeroSiege(dto.getNumeroSiege());
        billet.setDateEmission(dto.getDateEmission());
        billet.setContenuQr(dto.getContenuQr());

        return billet;
    }
}
