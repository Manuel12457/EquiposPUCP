package com.example.equipospucp.DTOs;

import java.io.Serializable;

public class UsuarioDto implements Serializable {

    private String id;
    private Usuario usuario;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
