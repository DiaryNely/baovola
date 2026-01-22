package com.taxi_brousse.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class CooperativeVehiculeId implements Serializable {

    private Long cooperativeId;
    private Long vehiculeId;
    private LocalDate dateDebut;

    public CooperativeVehiculeId() {
    }

    public CooperativeVehiculeId(Long cooperativeId, Long vehiculeId, LocalDate dateDebut) {
        this.cooperativeId = cooperativeId;
        this.vehiculeId = vehiculeId;
        this.dateDebut = dateDebut;
    }

    public Long getCooperativeId() {
        return cooperativeId;
    }

    public void setCooperativeId(Long cooperativeId) {
        this.cooperativeId = cooperativeId;
    }

    public Long getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Long vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CooperativeVehiculeId that = (CooperativeVehiculeId) o;
        return Objects.equals(cooperativeId, that.cooperativeId) 
            && Objects.equals(vehiculeId, that.vehiculeId) 
            && Objects.equals(dateDebut, that.dateDebut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cooperativeId, vehiculeId, dateDebut);
    }
}
