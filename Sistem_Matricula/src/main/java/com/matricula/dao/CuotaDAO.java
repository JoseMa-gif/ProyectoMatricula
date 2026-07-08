package com.matricula.dao;

import com.matricula.model.Cuota;
import com.matricula.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CuotaDAO {

    public List<Cuota> listarPorMatricula(int codMatricula) throws SQLException {
        List<Cuota> lista = new ArrayList<>();
        String sql = "SELECT cu.codCuota, cu.codMatricula, cu.codConcepto, cu.monto, cu.ordenPago, cu.version, cu.estado, cu.fechaPago, "
                   + "c.nombreConcepto "
                   + "FROM cuota cu "
                   + "INNER JOIN concepto c ON cu.codConcepto = c.codConcepto "
                   + "WHERE cu.codMatricula = ? "
                   + "ORDER BY cu.ordenPago ASC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codMatricula);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cuota cu = new Cuota();
                    cu.setCodCuota(rs.getInt("codCuota"));
                    cu.setCodMatricula(rs.getInt("codMatricula"));
                    cu.setCodConcepto(rs.getInt("codConcepto"));
                    cu.setMonto(rs.getBigDecimal("monto"));
                    cu.setOrdenPago(rs.getInt("ordenPago"));
                    cu.setVersion(rs.getInt("version"));
                    cu.setEstado(rs.getString("estado"));
                    cu.setFechaPago(rs.getTimestamp("fechaPago"));
                    cu.setNombreConcepto(rs.getString("nombreConcepto"));
                    lista.add(cu);
                }
            }
        }
        return lista;
    }
}
