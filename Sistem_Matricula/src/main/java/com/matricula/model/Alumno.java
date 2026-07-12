package com.matricula.model;

import java.sql.Timestamp;

public class Alumno {
    private int codAlumno;
    private int codTipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String fechaNacimiento;
    private boolean estado;
    private Timestamp fechaRegistro;

    private String nombreTipoDocumento;

    public Alumno() {
    }

    public int getCodAlumno() { return codAlumno; }
    public void setCodAlumno(int codAlumno) { this.codAlumno = codAlumno; }

    public int getCodTipoDocumento() { return codTipoDocumento; }
    public void setCodTipoDocumento(int codTipoDocumento) { this.codTipoDocumento = codTipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getNombreTipoDocumento() { return nombreTipoDocumento; }
    public void setNombreTipoDocumento(String nombreTipoDocumento) { this.nombreTipoDocumento = nombreTipoDocumento; }
//Campos
}
