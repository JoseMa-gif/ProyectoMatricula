package com.matricula.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Recibo {
    private int codRecibo;
    private String correlativo;
    private int codCuota;
    private BigDecimal monto;
    private Timestamp fechaEmision;
    private int usuarioRegistro;

    public Recibo() {
    }

    public int getCodRecibo() { return codRecibo; }
    public void setCodRecibo(int codRecibo) { this.codRecibo = codRecibo; }

    public String getCorrelativo() { return correlativo; }
    public void setCorrelativo(String correlativo) { this.correlativo = correlativo; }

    public int getCodCuota() { return codCuota; }
    public void setCodCuota(int codCuota) { this.codCuota = codCuota; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public Timestamp getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(Timestamp fechaEmision) { this.fechaEmision = fechaEmision; }

    public int getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(int usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }
}
