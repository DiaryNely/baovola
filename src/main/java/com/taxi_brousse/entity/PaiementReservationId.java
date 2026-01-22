package com.taxi_brousse.entity;

import java.io.Serializable;
import java.util.Objects;

public class PaiementReservationId implements Serializable {

    private Long paiementId;
    private Long reservationId;

    public PaiementReservationId() {
    }

    public PaiementReservationId(Long paiementId, Long reservationId) {
        this.paiementId = paiementId;
        this.reservationId = reservationId;
    }

    public Long getPaiementId() {
        return paiementId;
    }

    public void setPaiementId(Long paiementId) {
        this.paiementId = paiementId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaiementReservationId that = (PaiementReservationId) o;
        return Objects.equals(paiementId, that.paiementId) && Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paiementId, reservationId);
    }
}
