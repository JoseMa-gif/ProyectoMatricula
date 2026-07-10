package com.matricula.dao;

import com.matricula.util.AuditoriaUtil;
import com.matricula.util.ConexionBD;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PagoDAO {

    public String registrarPagoTransaccional(int codCuota, BigDecimal monto, int idUsuarioLogueado, String ip) throws SQLException {
        Connection con = null;
        PreparedStatement psUpdateParam = null;
        PreparedStatement psSelectParam = null;
        PreparedStatement psInsertRecibo = null;
        PreparedStatement psUpdateCuota = null;
        ResultSet rsParam = null;

        String correlativoGenerado = null;

        String sqlUpdateParam = "UPDATE parametro SET valor = valor + 1 WHERE nombre = 'CORRELATIVO_RECIBO'";
        String sqlSelectParam = "SELECT valor FROM parametro WHERE nombre = 'CORRELATIVO_RECIBO'";
        String sqlInsertRecibo = "INSERT INTO recibo (correlativo, codCuota, monto, usuarioRegistro) VALUES (?, ?, ?, ?)";
        String sqlUpdateCuota = "UPDATE cuota SET estado = 'PAGADO', fechaPago = CURRENT_TIMESTAMP WHERE codCuota = ?";

        try {
            con = ConexionBD.getConexion();
            con.setAutoCommit(false); // Iniciar Transacción

      
            psUpdateParam = con.prepareStatement(sqlUpdateParam);
            psUpdateParam.executeUpdate();

       
            psSelectParam = con.prepareStatement(sqlSelectParam);
            rsParam = psSelectParam.executeQuery();
            int numCorrelativo = 0;
            if (rsParam.next()) {
                numCorrelativo = Integer.parseInt(rsParam.getString("valor"));
            } else {
                throw new SQLException("No se encontró el parámetro CORRELATIVO_RECIBO.");
            }
            correlativoGenerado = String.format("BOL-%06d", numCorrelativo);

        
            psInsertRecibo = con.prepareStatement(sqlInsertRecibo);
            psInsertRecibo.setString(1, correlativoGenerado);
            psInsertRecibo.setInt(2, codCuota);
            psInsertRecibo.setBigDecimal(3, monto);
            psInsertRecibo.setInt(4, idUsuarioLogueado);
            psInsertRecibo.executeUpdate();

         
            psUpdateCuota = con.prepareStatement(sqlUpdateCuota);
            psUpdateCuota.setInt(1, codCuota);
            psUpdateCuota.executeUpdate();

          
            AuditoriaUtil.registrar(con, "Caja", "cuota", "PAGO", codCuota, null, "{\"recibo\":\"" + correlativoGenerado + "\"}");

            con.commit(); 
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();  
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (rsParam != null) try { rsParam.close(); } catch (SQLException e) {}
            if (psUpdateParam != null) try { psUpdateParam.close(); } catch (SQLException e) {}
            if (psSelectParam != null) try { psSelectParam.close(); } catch (SQLException e) {}
            if (psInsertRecibo != null) try { psInsertRecibo.close(); } catch (SQLException e) {}
            if (psUpdateCuota != null) try { psUpdateCuota.close(); } catch (SQLException e) {}
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {}
            }
        }
        
        return correlativoGenerado;
    }
    
