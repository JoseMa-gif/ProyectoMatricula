package com.matricula.util;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.matricula.model.Auditoria;

/**
 * Registra en la tabla auditoria cada operación relevante del sistema.
 * Debe invocarse dentro de la MISMA transacción que la operación auditada,
 * usando la misma Connection, para que el rollback también revierta el
 * registro de auditoría si algo falla.
 */
public class AuditoriaUtil {

    private static final Gson gson = new Gson();

    private AuditoriaUtil() {
    }

    public static void registrar(Connection con, String modulo,
            String tablaAfectada, String operacion, Integer codigoRegistro,
            Object valorAnterior, Object valorNuevo) throws SQLException {

        Auditoria contexto = AuditoriaContext.getAuditoria();
        if (contexto == null) {
            contexto = new Auditoria();
            contexto.setIpOrigen("127.0.0.1");
            contexto.setEquipo("Sistema");
            contexto.setNavegador("N/A");
            contexto.setCodUsuario(0);
        }

        String sql = "INSERT INTO auditoria (codUsuario, modulo, tablaAfectada, operacion, "
                + "codigoRegistro, valorAnterior, valorNuevo, ipOrigen, equipo, navegador) VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (contexto.getCodUsuario() > 0) {
                ps.setInt(1, contexto.getCodUsuario());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, modulo);
            ps.setString(3, tablaAfectada);
            ps.setString(4, operacion);
            if (codigoRegistro != null) {
                ps.setInt(5, codigoRegistro);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setString(6, valorAnterior != null ? gson.toJson(valorAnterior) : null);
            ps.setString(7, valorNuevo != null ? gson.toJson(valorNuevo) : null);
            ps.setString(8, contexto.getIpOrigen());
            ps.setString(9, contexto.getEquipo());
            ps.setString(10, contexto.getNavegador());
            ps.executeUpdate();
        }
    }
}
