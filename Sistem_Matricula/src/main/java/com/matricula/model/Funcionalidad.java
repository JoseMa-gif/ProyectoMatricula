package com.matricula.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Funcionalidad implements Serializable {

    private int idFuncionalidad;
    private String nombre;
    private String icono;
    private Integer padre;

    private List<Funcionalidad> hijos = new ArrayList<>();

    public Funcionalidad() {
    }

    public int getIdFuncionalidad() {
        return idFuncionalidad;
    }

    public void setIdFuncionalidad(int idFuncionalidad) {
        this.idFuncionalidad = idFuncionalidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public Integer getPadre() {
        return padre;
    }

    public void setPadre(Integer padre) {
        this.padre = padre;
    }

    public List<Funcionalidad> getHijos() {
        return hijos;
    }

    public void setHijos(List<Funcionalidad> hijos) {
        this.hijos = hijos;
    }
}
