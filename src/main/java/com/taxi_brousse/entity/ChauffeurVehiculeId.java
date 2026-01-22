package com.taxi_brousse.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChauffeurVehiculeId implements Serializable {

    private Long chauffeurId;
    private Long vehiculeId;
    private LocalDateTime dateDebut;

    public ChauffeurVehiculeId() {
    }

    public ChauffeurVehiculeId(Long chauffeurId, Long vehiculeId, LocalDateTime dateDebut) {
        this.chauffeurId = chauffeurId;
        this.vehiculeId = vehiculeId;
        this.dateDebut = dateDebut;
    }

    public Long getChauffeurId() {
        return chauffeurId;
    }

    public void setChauffeurId(Long chauffeurId) {
        this.chauffeurId = chauffeurId;
    }

    public Long getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Long vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChauffeurVehiculeId that = (ChauffeurVehiculeId) o;
        return Objects.equals(chauffeurId, that.chauffeurId) 
            && Objects.equals(vehiculeId, that.vehiculeId) 
            && Objects.equals(dateDebut, that.dateDebut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chauffeurId, vehiculeId, dateDebut);
    }
}
