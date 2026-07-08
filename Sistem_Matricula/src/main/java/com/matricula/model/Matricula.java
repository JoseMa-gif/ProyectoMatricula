package com.matricula.model;

import java.sql.Timestamp;

public class Matricula {
    private int codMatricula;
    private int codAlumno;
    private int codAula;
    private int codAnioAcademico;
    private Timestamp fechaMatricula;
    private int version;
    private boolean estado;
    private int usuarioRegistro;

    private String nombreAlumno;
    private String descripcionAula;
    private String nombreAnio;
    private String nombreUsuario;

    public Matricula() {
    }

    public int getCodMatricula() { return codMatricula; }
    public void setCodMatricula(int codMatricula) { this.codMatricula = codMatricula; }

    public int getCodAlumno() { return codAlumno; }
    public void setCodAlumno(int codAlumno) { this.codAlumno = codAlumno; }

    public int getCodAula() { return codAula; }
    public void setCodAula(int codAula) { this.codAula = codAula; }

    public int getCodAnioAcademico() { return codAnioAcademico; }
    public void setCodAnioAcademico(int codAnioAcademico) { this.codAnioAcademico = codAnioAcademico; }

    public Timestamp getFechaMatricula() { return fechaMatricula; }
    public void setFechaMatricula(Timestamp fechaMatricula) { this.fechaMatricula = fechaMatricula; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public int getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(int usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }

    public String getNombreAlumno() { return nombreAlumno; }
    public void setNombreAlumno(String nombreAlumno) { this.nombreAlumno = nombreAlumno; }

    public String getDescripcionAula() { return descripcionAula; }
    public void setDescripcionAula(String descripcionAula) { this.descripcionAula = descripcionAula; }

    public String getNombreAnio() { return nombreAnio; }
    public void setNombreAnio(String nombreAnio) { this.nombreAnio = nombreAnio; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
