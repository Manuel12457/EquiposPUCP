package com.example.equipospucp.DTOs;

import java.io.Serializable;

public class Reserva implements Serializable {

    private UsuarioDto usuario;
    private DispositivoDetalleDto dispositivo;
    private String fechayhora;
    private String motivo;
    private String curso;
    private Integer tiempoReserva;
    private String programasInstalados;
    private String dni;
    private String detallesAdicionales;
    private String estado;
    private String latitud;
    private String longitud;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Integer getTiempoReserva() {
        return tiempoReserva;
    }

    public void setTiempoReserva(Integer tiempoReserva) {
        this.tiempoReserva = tiempoReserva;
    }

    public String getProgramasInstalados() {
        return programasInstalados;
    }

    public void setProgramasInstalados(String programasInstalados) {
        this.programasInstalados = programasInstalados;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDetallesAdicionales() {
        return detallesAdicionales;
    }

    public void setDetallesAdicionales(String detallesAdicionales) {
        this.detallesAdicionales = detallesAdicionales;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getFechayhora() {
        return fechayhora;
    }

    public void setFechayhora(String fechayhora) {
        this.fechayhora = fechayhora;
    }

    public UsuarioDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }

    public DispositivoDetalleDto getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(DispositivoDetalleDto dispositivo) {
        this.dispositivo = dispositivo;
    }
}
