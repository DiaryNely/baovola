package com.taxi_brousse.entity;

import java.io.Serializable;
import java.util.Objects;

public class CooperativeTrajetId implements Serializable {

    private Long cooperativeId;
    private Long trajetId;

    public CooperativeTrajetId() {
    }

    public CooperativeTrajetId(Long cooperativeId, Long trajetId) {
        this.cooperativeId = cooperativeId;
        this.trajetId = trajetId;
    }

    public Long getCooperativeId() {
        return cooperativeId;
    }

    public void setCooperativeId(Long cooperativeId) {
        this.cooperativeId = cooperativeId;
    }

    public Long getTrajetId() {
        return trajetId;
    }

    public void setTrajetId(Long trajetId) {
        this.trajetId = trajetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CooperativeTrajetId that = (CooperativeTrajetId) o;
        return Objects.equals(cooperativeId, that.cooperativeId) && Objects.equals(trajetId, that.trajetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cooperativeId, trajetId);
    }
}
