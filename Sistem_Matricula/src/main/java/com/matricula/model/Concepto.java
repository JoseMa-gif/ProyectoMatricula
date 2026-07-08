package com.matricula.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Concepto {
    private int codConcepto;
    private int codAnioAcademico;
    private int codTipoConcepto;
    private String nombreConcepto;
    private BigDecimal monto;
    private int ordenPago;
    private boolean obligatorio;
    private int version;
    private boolean estado;
    private Timestamp fechaRegistro;

    private String nombreAnio;
    private String nombreTipoConcepto;

    public Concepto() {
    }

    public int getCodConcepto() { return codConcepto; }
    public void setCodConcepto(int codConcepto) { this.codConcepto = codConcepto; }

    public int getCodAnioAcademico() { return codAnioAcademico; }
    public void setCodAnioAcademico(int codAnioAcademico) { this.codAnioAcademico = codAnioAcademico; }

    public int getCodTipoConcepto() { return codTipoConcepto; }
    public void setCodTipoConcepto(int codTipoConcepto) { this.codTipoConcepto = codTipoConcepto; }

    public String getNombreConcepto() { return nombreConcepto; }
    public void setNombreConcepto(String nombreConcepto) { this.nombreConcepto = nombreConcepto; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public int getOrdenPago() { return ordenPago; }
    public void setOrdenPago(int ordenPago) { this.ordenPago = ordenPago; }

    public boolean isObligatorio() { return obligatorio; }
    public void setObligatorio(boolean obligatorio) { this.obligatorio = obligatorio; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getNombreAnio() { return nombreAnio; }
    public void setNombreAnio(String nombreAnio) { this.nombreAnio = nombreAnio; }

    public String getNombreTipoConcepto() { return nombreTipoConcepto; }
    public void setNombreTipoConcepto(String nombreTipoConcepto) { this.nombreTipoConcepto = nombreTipoConcepto; }
}
