package com.example.equipospucp.DTOs;

import java.io.Serializable;

public class DispositivoDetalleDto implements Serializable {

    private String id;
    private DispositivoDto dispositivoDto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DispositivoDto getDispositivoDto() {
        return dispositivoDto;
    }

    public void setDispositivoDto(DispositivoDto dispositivoDto) {
        this.dispositivoDto = dispositivoDto;
    }
}
