package com.example.equipospucp.DTOs;

public class TipoDispositivoDto {

    private String tipo;
    private Integer cantidadMarcas = 0;
    private Integer cantidad = 0;

    public TipoDispositivoDto() {}
    public TipoDispositivoDto(String tipo, Integer cantidadMarcas, Integer cantidad) {
        this.tipo = tipo;
        this.cantidadMarcas = cantidadMarcas;
        this.cantidad = cantidad;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidadMarcas() {
        return cantidadMarcas;
    }

    public void setCantidadMarcas(Integer cantidadMarcas) {
        this.cantidadMarcas = cantidadMarcas;
    }
}
