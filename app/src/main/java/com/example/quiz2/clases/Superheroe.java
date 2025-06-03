package com.example.quiz2.clases;

import java.util.List;
import java.util.ArrayList;

public class Superheroe implements java.io.Serializable {
    private int id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private List<String> poderes;
    private List<String> comics;
    private String universo;
    private String primeraAparicion;
    private String estado; // Vivo, Fallecido, Desconocido
    private int popularidad;
    private boolean favorito;
    private List<String> grupos; // Lista de grupos a los que pertenece el superhéroe
    private List<String> series;
    private List<String> stories;
    private List<String> events;

    public Superheroe() {
        this.grupos = new ArrayList<>();
        this.poderes = new ArrayList<>();
        this.comics = new ArrayList<>();
    }

    public Superheroe(int id, String nombre, String descripcion, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.grupos = new ArrayList<>();
        this.poderes = new ArrayList<>();
        this.comics = new ArrayList<>();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public List<String> getPoderes() {
        return poderes;
    }

    public void setPoderes(List<String> poderes) {
        this.poderes = poderes;
    }

    public List<String> getComics() {
        return comics;
    }

    public void setComics(List<String> comics) {
        this.comics = comics;
    }

    public String getUniverso() {
        return universo;
    }

    public void setUniverso(String universo) {
        this.universo = universo;
    }

    public String getPrimeraAparicion() {
        return primeraAparicion;
    }

    public void setPrimeraAparicion(String primeraAparicion) {
        this.primeraAparicion = primeraAparicion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getPopularidad() {
        return popularidad;
    }

    public void setPopularidad(int popularidad) {
        this.popularidad = popularidad;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public List<String> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<String> grupos) {
        this.grupos = grupos;
    }

    public void addGrupo(String grupo) {
        if (this.grupos == null) {
            this.grupos = new ArrayList<>();
        }
        this.grupos.add(grupo);
    }

    public List<String> getSeries() {
        return series;
    }

    public void setSeries(List<String> series) {
        this.series = series;
    }

    public List<String> getStories() {
        return stories;
    }

    public void setStories(List<String> stories) {
        this.stories = stories;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }
} 