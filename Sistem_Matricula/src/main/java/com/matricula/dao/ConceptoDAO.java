package com.matricula.dao;

import com.matricula.model.Concepto;
import com.matricula.util.AuditoriaUtil;
import com.matricula.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConceptoDAO {

    public List<Concepto> listarTodos() throws SQLException {
        List<Concepto> lista = new ArrayList<>();
        String sql = "SELECT c.codConcepto, c.codAnioAcademico, c.codTipoConcepto, c.nombreConcepto, c.monto, c.ordenPago, c.obligatorio, c.version, c.estado, "
                   + "a.anio as nombreAnio, t.nombre as nombreTipoConcepto "
                   + "FROM concepto c "
                   + "INNER JOIN anioAcademico a ON c.codAnioAcademico = a.codAnioAcademico "
                   + "INNER JOIN tipoConcepto t ON c.codTipoConcepto = t.codTipoConcepto "
                   + "ORDER BY a.anio DESC, c.ordenPago ASC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Concepto c = new Concepto();
                c.setCodConcepto(rs.getInt("codConcepto"));
                c.setCodAnioAcademico(rs.getInt("codAnioAcademico"));
                c.setCodTipoConcepto(rs.getInt("codTipoConcepto"));
                c.setNombreConcepto(rs.getString("nombreConcepto"));
                c.setMonto(rs.getBigDecimal("monto"));
                c.setOrdenPago(rs.getInt("ordenPago"));
                c.setObligatorio(rs.getBoolean("obligatorio"));
                c.setVersion(rs.getInt("version"));
                c.setEstado(rs.getBoolean("estado"));
                c.setNombreAnio(rs.getString("nombreAnio"));
                c.setNombreTipoConcepto(rs.getString("nombreTipoConcepto"));
                lista.add(c);
            }
        }
        return lista;
    }

    public List<Concepto> listarPorAnio(int codAnioAcademico) throws SQLException {
        List<Concepto> lista = new ArrayList<>();
        String sql = "SELECT codConcepto, codAnioAcademico, codTipoConcepto, nombreConcepto, monto, ordenPago, obligatorio, version, estado "
                   + "FROM concepto "
                   + "WHERE codAnioAcademico = ? AND estado = 1 "
                   + "ORDER BY ordenPago ASC";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codAnioAcademico);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Concepto c = new Concepto();
                    c.setCodConcepto(rs.getInt("codConcepto"));
                    c.setCodAnioAcademico(rs.getInt("codAnioAcademico"));
                    c.setCodTipoConcepto(rs.getInt("codTipoConcepto"));
                    c.setNombreConcepto(rs.getString("nombreConcepto"));
                    c.setMonto(rs.getBigDecimal("monto"));
                    c.setOrdenPago(rs.getInt("ordenPago"));
                    c.setObligatorio(rs.getBoolean("obligatorio"));
                    c.setVersion(rs.getInt("version"));
                    c.setEstado(rs.getBoolean("estado"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }

    public void insertar(Concepto c, int idUsuario, String ip) throws SQLException {
        String sql = "INSERT INTO concepto (codAnioAcademico, codTipoConcepto, nombreConcepto, monto, ordenPago, obligatorio, estado, version) VALUES (?, ?, ?, ?, ?, ?, ?, 1)";
        try (Connection con = ConexionBD.getConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, c.getCodAnioAcademico());
                ps.setInt(2, c.getCodTipoConcepto());
                ps.setString(3, c.getNombreConcepto());
                ps.setBigDecimal(4, c.getMonto());
                ps.setInt(5, c.getOrdenPago());
                ps.setBoolean(6, c.isObligatorio());
                ps.setBoolean(7, c.isEstado());
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int codGenerado = rs.getInt(1);
                        c.setCodConcepto(codGenerado);
                    }
                }
            }
            AuditoriaUtil.registrar(con, "Conceptos", "concepto", "INSERT", c.getCodConcepto(), null, c);
        }
    }

    public void actualizar(Concepto c, int idUsuario, String ip) throws SQLException {
 
        String sql = "UPDATE concepto SET codAnioAcademico = ?, codTipoConcepto = ?, nombreConcepto = ?, monto = ?, ordenPago = ?, obligatorio = ?, estado = ?, version = ? WHERE codConcepto = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Concepto anterior = buscarPorId(con, c.getCodConcepto());
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, c.getCodAnioAcademico());
                ps.setInt(2, c.getCodTipoConcepto());
                ps.setString(3, c.getNombreConcepto());
                ps.setBigDecimal(4, c.getMonto());
                ps.setInt(5, c.getOrdenPago());
                ps.setBoolean(6, c.isObligatorio());
                ps.setBoolean(7, c.isEstado());
                ps.setInt(8, c.getVersion());
                ps.setInt(9, c.getCodConcepto());
                ps.executeUpdate();
            }
            Concepto nuevo = buscarPorId(con, c.getCodConcepto());
            AuditoriaUtil.registrar(con, "Conceptos", "concepto", "UPDATE", c.getCodConcepto(), anterior, nuevo);
        }
    }

    public void eliminarLogico(int codConcepto, int idUsuario, String ip) throws SQLException {
        String sql = "UPDATE concepto SET estado = 0 WHERE codConcepto = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Concepto anterior = buscarPorId(con, codConcepto);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, codConcepto);
                ps.executeUpdate();
            }
            Concepto nuevo = buscarPorId(con, codConcepto);
            AuditoriaUtil.registrar(con, "Conceptos", "concepto", "DELETE_LOGICO", codConcepto, anterior, nuevo);
        }
    }

    private Concepto buscarPorId(Connection con, int codConcepto) throws SQLException {
        String sql = "SELECT codConcepto, codAnioAcademico, codTipoConcepto, nombreConcepto, monto, ordenPago, obligatorio, version, estado FROM concepto WHERE codConcepto = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codConcepto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Concepto c = new Concepto();
                    c.setCodConcepto(rs.getInt("codConcepto"));
                    c.setCodAnioAcademico(rs.getInt("codAnioAcademico"));
                    c.setCodTipoConcepto(rs.getInt("codTipoConcepto"));
                    c.setNombreConcepto(rs.getString("nombreConcepto"));
                    c.setMonto(rs.getBigDecimal("monto"));
                    c.setOrdenPago(rs.getInt("ordenPago"));
                    c.setObligatorio(rs.getBoolean("obligatorio"));
                    c.setVersion(rs.getInt("version"));
                    c.setEstado(rs.getBoolean("estado"));
                    return c;
                }
            }
        }
        return null;
    }

     
    public int clonarConceptos(int codAnioOrigen, int codAnioDestino) throws SQLException {
        String sql = "INSERT INTO concepto (codAnioAcademico, codTipoConcepto, nombreConcepto, monto, ordenPago, obligatorio, estado, version) "
                   + "SELECT ?, codTipoConcepto, nombreConcepto, monto, ordenPago, obligatorio, 1, 1 "
                   + "FROM concepto "
                   + "WHERE codAnioAcademico = ? AND estado = 1 "
                   + "AND NOT EXISTS ("
                   + "  SELECT 1 FROM concepto dest "
                   + "  WHERE dest.codAnioAcademico = ? "
                   + "  AND dest.nombreConcepto = concepto.nombreConcepto "
                   + "  AND dest.codTipoConcepto = concepto.codTipoConcepto"
                   + ")";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codAnioDestino);
            ps.setInt(2, codAnioOrigen);
            ps.setInt(3, codAnioDestino);
            return ps.executeUpdate();
        }
    }
}
