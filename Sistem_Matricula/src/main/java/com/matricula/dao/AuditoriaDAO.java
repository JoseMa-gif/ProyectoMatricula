package com.matricula.dao;

import com.matricula.model.Auditoria;
import com.matricula.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    public List<Auditoria> listarTodos() throws SQLException {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT a.codAuditoria, a.codUsuario, u.usuario as nombreUsuario, a.modulo, a.tablaAfectada, "
                   + "a.operacion, a.codigoRegistro, a.valorAnterior, a.valorNuevo, a.fechaHora, a.ipOrigen, a.equipo, a.navegador "
                   + "FROM auditoria a "
                   + "LEFT JOIN usuario u ON a.codUsuario = u.idUsuario "
                   + "ORDER BY a.codAuditoria DESC LIMIT 500"; // Límite para no sobrecargar
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Auditoria a = new Auditoria();
                a.setCodAuditoria(rs.getInt("codAuditoria"));
                a.setCodUsuario(rs.getInt("codUsuario"));
                a.setNombreUsuario(rs.getString("nombreUsuario"));
                a.setModulo(rs.getString("modulo"));
                a.setTablaAfectada(rs.getString("tablaAfectada"));
                a.setOperacion(rs.getString("operacion"));
                a.setCodigoRegistro(rs.getInt("codigoRegistro"));
                a.setValorAnterior(rs.getString("valorAnterior"));
                a.setValorNuevo(rs.getString("valorNuevo"));
                a.setFechaHora(rs.getTimestamp("fechaHora"));
                a.setIpOrigen(rs.getString("ipOrigen"));
                a.setEquipo(rs.getString("equipo"));
                a.setNavegador(rs.getString("navegador"));
                lista.add(a);
            }
        }
        return lista;
    }

    public void registrar(Auditoria a) throws SQLException {
        String sql = "INSERT INTO auditoria (codUsuario, modulo, tablaAfectada, operacion, codigoRegistro, "
                   + "valorAnterior, valorNuevo, ipOrigen, equipo, navegador) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (a.getCodUsuario() > 0) {
                ps.setInt(1, a.getCodUsuario());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            
            ps.setString(2, a.getModulo());
            ps.setString(3, a.getTablaAfectada());
            ps.setString(4, a.getOperacion());
            
            if (a.getCodigoRegistro() > 0) {
                ps.setInt(5, a.getCodigoRegistro());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            
            ps.setString(6, a.getValorAnterior());
            ps.setString(7, a.getValorNuevo());
            ps.setString(8, a.getIpOrigen());
            ps.setString(9, a.getEquipo());
            ps.setString(10, a.getNavegador());
            
            ps.executeUpdate();
        }
    }
}
