package com.matricula.model;

import java.sql.Timestamp;

public class Aula {
    private int codAula;
    private int codAnioAcademico;
    private int codNivel;
    private int codGrado;
    private String seccion;
    private int capacidadMaxima;
    private int version;
    private boolean estado;
    private Timestamp fechaRegistro;


    private String nombreAnio;
    private String nombreNivel;
    private String nombreGrado;
    private int matriculados; 

    public Aula() {
    }

    public int getCodAula() { return codAula; }
    public void setCodAula(int codAula) { this.codAula = codAula; }

    public int getCodAnioAcademico() { return codAnioAcademico; }
    public void setCodAnioAcademico(int codAnioAcademico) { this.codAnioAcademico = codAnioAcademico; }

    public int getCodNivel() { return codNivel; }
    public void setCodNivel(int codNivel) { this.codNivel = codNivel; }

    public int getCodGrado() { return codGrado; }
    public void setCodGrado(int codGrado) { this.codGrado = codGrado; }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getNombreAnio() { return nombreAnio; }
    public void setNombreAnio(String nombreAnio) { this.nombreAnio = nombreAnio; }

    public String getNombreNivel() { return nombreNivel; }
    public void setNombreNivel(String nombreNivel) { this.nombreNivel = nombreNivel; }

    public String getNombreGrado() { return nombreGrado; }
    public void setNombreGrado(String nombreGrado) { this.nombreGrado = nombreGrado; }

    public int getMatriculados() { return matriculados; }
    public void setMatriculados(int matriculados) { this.matriculados = matriculados; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
