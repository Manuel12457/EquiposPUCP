package com.example.equipospucp.DTOs;

import java.io.Serializable;

public class DispositivoDetalleDto implements Serializable {

    private String id;
    private Dispositivo dispositivo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Dispositivo getDispositivoDto() {
        return dispositivo;
    }

    public void setDispositivoDto(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }
}
