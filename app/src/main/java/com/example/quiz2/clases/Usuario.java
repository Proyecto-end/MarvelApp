package com.example.quiz2.clases;

import java.util.Date;

public class Usuario {
    private String nombre;
    private String correo;
    private String password;
    private String fechaNacimiento;
    private int id;

    public Usuario() {
    }

    public Usuario(String nombre, String correo, String password, String fechaNacimiento) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
} 