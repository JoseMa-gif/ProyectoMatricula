package com.matricula.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Cuota {
    private int codCuota;
    private int codMatricula;
    private int codConcepto;
    private BigDecimal monto;
    private int ordenPago;
    private int version;
    private String estado;
    private Timestamp fechaPago;

    private String nombreConcepto;

    public Cuota() {
    }

    public int getCodCuota() { return codCuota; }
    public void setCodCuota(int codCuota) { this.codCuota = codCuota; }

    public int getCodMatricula() { return codMatricula; }
    public void setCodMatricula(int codMatricula) { this.codMatricula = codMatricula; }

    public int getCodConcepto() { return codConcepto; }
    public void setCodConcepto(int codConcepto) { this.codConcepto = codConcepto; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public int getOrdenPago() { return ordenPago; }
    public void setOrdenPago(int ordenPago) { this.ordenPago = ordenPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Timestamp getFechaPago() { return fechaPago; }
    public void setFechaPago(Timestamp fechaPago) { this.fechaPago = fechaPago; }

    public String getNombreConcepto() { return nombreConcepto; }
    public void setNombreConcepto(String nombreConcepto) { this.nombreConcepto = nombreConcepto; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
