package com.example.equipospucp.DTOs;

public class Image {
    private String dispositivo;
    private String imagen;

    public Image() {}

    public Image(String dispositivo, String imagen) {
        this.dispositivo = dispositivo;
        this.imagen = imagen;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
