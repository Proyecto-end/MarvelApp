package com.example.quiz2.clases;

public class Noticia {
    private String nombre;
    private String info;
    private String imagen;

    public Noticia(String nombre, String info, String imagen) {
        this.nombre = nombre;
        this.info = info;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImagen() { 
        return imagen; 
    }

    public void setImagen(String imagen) { 
        this.imagen = imagen; 
    }
} 