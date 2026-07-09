package com.matricula.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ReciboDTO {
    private String correlativo;
    private BigDecimal monto;
    private String concepto;
    private Timestamp fecha;
    private String alumno;

    public ReciboDTO() {}

    public String getCorrelativo() { return correlativo; }
    public void setCorrelativo(String correlativo) { this.correlativo = correlativo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public String getAlumno() { return alumno; }
    public void setAlumno(String alumno) { this.alumno = alumno; }
}
