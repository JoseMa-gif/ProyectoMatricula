package com.matricula.model;

import java.io.Serializable;

public class RolFuncionalidad implements Serializable {

    private int idRolFuncionalidad;
    private int idRol;
    private int idFuncionalidad;
    private boolean ver;
    private boolean crear;
    private boolean editar;
    private boolean eliminar;
    private boolean imprimir;

    private String nombreFuncionalidad;
    private Integer padreFuncionalidad;

    public RolFuncionalidad() {
    }

    public int getIdRolFuncionalidad() {
        return idRolFuncionalidad;
    }

    public void setIdRolFuncionalidad(int idRolFuncionalidad) {
        this.idRolFuncionalidad = idRolFuncionalidad;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public int getIdFuncionalidad() {
        return idFuncionalidad;
    }

    public void setIdFuncionalidad(int idFuncionalidad) {
        this.idFuncionalidad = idFuncionalidad;
    }

    public boolean isVer() {
        return ver;
    }

    public void setVer(boolean ver) {
        this.ver = ver;
    }

    public boolean isCrear() {
        return crear;
    }

    public void setCrear(boolean crear) {
        this.crear = crear;
    }

    public boolean isEditar() {
        return editar;
    }

    public void setEditar(boolean editar) {
        this.editar = editar;
    }

    public boolean isEliminar() {
        return eliminar;
    }

    public void setEliminar(boolean eliminar) {
        this.eliminar = eliminar;
    }

    public boolean isImprimir() {
        return imprimir;
    }

    public void setImprimir(boolean imprimir) {
        this.imprimir = imprimir;
    }

    public String getNombreFuncionalidad() {
        return nombreFuncionalidad;
    }

    public void setNombreFuncionalidad(String nombreFuncionalidad) {
        this.nombreFuncionalidad = nombreFuncionalidad;
    }

    public Integer getPadreFuncionalidad() {
        return padreFuncionalidad;
    }

    public void setPadreFuncionalidad(Integer padreFuncionalidad) {
        this.padreFuncionalidad = padreFuncionalidad;
    }
}
