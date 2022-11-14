package com.example.equipospucp.DTOs;

import java.util.ArrayList;

public class TipoDispositivoDto {

    private String tipo;
    private ArrayList<String> listaMarcas;
    private Integer cantidadMarcas = 0;
    private Integer cantidad = 0;

    public TipoDispositivoDto() {}
    public TipoDispositivoDto(String tipo, Integer cantidadMarcas, Integer cantidad, ArrayList<String> listaMarcas) {
        this.tipo = tipo;
        this.cantidadMarcas = cantidadMarcas;
        this.cantidad = cantidad;
        this.listaMarcas = listaMarcas;
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

    public ArrayList<String> getListaMarcas() {
        return listaMarcas;
    }

    public void setListaMarcas(ArrayList<String> listaMarcas) {
        this.listaMarcas = listaMarcas;
    }
}
