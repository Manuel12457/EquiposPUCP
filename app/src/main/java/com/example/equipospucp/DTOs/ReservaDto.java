package com.example.equipospucp.DTOs;

import java.io.Serializable;

public class ReservaDto implements Serializable {

    private String id;
    private Reserva reserva;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
}
