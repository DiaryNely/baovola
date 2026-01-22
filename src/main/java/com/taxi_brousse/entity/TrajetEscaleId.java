package com.taxi_brousse.entity;

import java.io.Serializable;
import java.util.Objects;

public class TrajetEscaleId implements Serializable {

    private Long trajetId;
    private Integer ordre;

    public TrajetEscaleId() {
    }

    public TrajetEscaleId(Long trajetId, Integer ordre) {
        this.trajetId = trajetId;
        this.ordre = ordre;
    }

    public Long getTrajetId() {
        return trajetId;
    }

    public void setTrajetId(Long trajetId) {
        this.trajetId = trajetId;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrajetEscaleId that = (TrajetEscaleId) o;
        return Objects.equals(trajetId, that.trajetId) && Objects.equals(ordre, that.ordre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trajetId, ordre);
    }
}
