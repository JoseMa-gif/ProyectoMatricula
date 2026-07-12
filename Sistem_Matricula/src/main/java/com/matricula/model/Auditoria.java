package com.matricula.model;

import java.sql.Timestamp;

public class Auditoria {
    private int codAuditoria;
    private int codUsuario;
    private String nombreUsuario; // Para mostrar en la vista
    private String modulo;
    private String tablaAfectada;
    private String operacion;
    private int codigoRegistro;
    private String valorAnterior;
    private String valorNuevo;
    private Timestamp fechaHora;
    private String ipOrigen;
    private String equipo;
    private String navegador;

    public Auditoria() {}

    public int getCodAuditoria() { return codAuditoria; }
    public void setCodAuditoria(int codAuditoria) { this.codAuditoria = codAuditoria; }

    public int getCodUsuario() { return codUsuario; }
    public void setCodUsuario(int codUsuario) { this.codUsuario = codUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public String getTablaAfectada() { return tablaAfectada; }
    public void setTablaAfectada(String tablaAfectada) { this.tablaAfectada = tablaAfectada; }

    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }

    public int getCodigoRegistro() { return codigoRegistro; }
    public void setCodigoRegistro(int codigoRegistro) { this.codigoRegistro = codigoRegistro; }

    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

    public String getValorNuevo() { return valorNuevo; }
    public void setValorNuevo(String valorNuevo) { this.valorNuevo = valorNuevo; }

    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }

    public String getIpOrigen() { return ipOrigen; }
    public void setIpOrigen(String ipOrigen) { this.ipOrigen = ipOrigen; }

    public String getEquipo() { return equipo; }
    public void setEquipo(String equipo) { this.equipo = equipo; }

    public String getNavegador() { return navegador; }
    public void setNavegador(String navegador) { this.navegador = navegador; }
}
