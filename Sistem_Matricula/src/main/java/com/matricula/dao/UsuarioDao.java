package com.matricula.dao;

import com.matricula.model.Usuario;
import com.matricula.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.matricula.util.AuditoriaUtil;

public class UsuarioDAO {

    /** Busca un usuario activo por su nombre de usuario, junto con el nombre de su rol. */
    public Usuario buscarPorUsuario(String usuario) throws SQLException {
        String sql = "SELECT u.idUsuario, u.usuario, u.password, u.secret2FA, u.idRol, "
                + "u.estado, r.nombreRol "
                + "FROM usuario u INNER JOIN rol r ON u.idRol = r.idRol "
                + "WHERE u.usuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("idUsuario"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setPassword(rs.getString("password"));
                    u.setSecret2FA(rs.getString("secret2FA"));
                    u.setIdRol(rs.getInt("idRol"));
                    u.setNombreRol(rs.getString("nombreRol"));
                    u.setEstado(rs.getBoolean("estado"));
                    return u;
                }
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.idUsuario, u.usuario, u.estado, r.nombreRol "
                   + "FROM usuario u INNER JOIN rol r ON u.idRol = r.idRol "
                   + "ORDER BY u.idUsuario";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setUsuario(rs.getString("usuario"));
                u.setEstado(rs.getBoolean("estado"));
                u.setNombreRol(rs.getString("nombreRol"));
                lista.add(u);
            }
        }
        return lista;
    }

    public void actualizarSecret2FA(int idUsuario, String secret) throws Exception {
        String sql = "UPDATE usuario SET secret2FA = ? WHERE idUsuario = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Usuario anterior = buscarPorId(con, idUsuario);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, secret);
                ps.setInt(2, idUsuario);
                ps.executeUpdate();
            }
            Usuario nuevo = buscarPorId(con, idUsuario);
            AuditoriaUtil.registrar(con, "Seguridad", "usuario", "UPDATE_2FA", idUsuario, anterior, nuevo);
        }
    }

    private Usuario buscarPorId(Connection con, int idUsuario) throws SQLException {
        String sql = "SELECT idUsuario, usuario, password, secret2FA, idRol, estado FROM usuario WHERE idUsuario = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("idUsuario"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setPassword(rs.getString("password"));
                    u.setSecret2FA(rs.getString("secret2FA"));
                    u.setIdRol(rs.getInt("idRol"));
                    u.setEstado(rs.getBoolean("estado"));
                    return u;
                }
            }
        }
        return null;
    }

    /** Versión pública: abre su propia conexión. Útil desde servlets. */
    public Usuario buscarPorId(int idUsuario) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            return buscarPorId(con, idUsuario);
        }
    }

    public void insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (usuario, password, idRol, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getPassword());
            ps.setInt(3, u.getIdRol());
            ps.setBoolean(4, u.isEstado());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setIdUsuario(rs.getInt(1));
                }
            }
            AuditoriaUtil.registrar(con, "Seguridad", "usuario", "INSERT", u.getIdUsuario(), null, u);
        }
    }

    public void actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET usuario = ?, idRol = ?, estado = ? WHERE idUsuario = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Usuario anterior = buscarPorId(con, u.getIdUsuario());
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, u.getUsuario());
                ps.setInt(2, u.getIdRol());
                ps.setBoolean(3, u.isEstado());
                ps.setInt(4, u.getIdUsuario());
                ps.executeUpdate();
            }
            Usuario nuevo = buscarPorId(con, u.getIdUsuario());
            AuditoriaUtil.registrar(con, "Seguridad", "usuario", "UPDATE", u.getIdUsuario(), anterior, nuevo);
        }
    }

    public void actualizarConPassword(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET usuario = ?, password = ?, idRol = ?, estado = ? WHERE idUsuario = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Usuario anterior = buscarPorId(con, u.getIdUsuario());
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, u.getUsuario());
                ps.setString(2, u.getPassword());
                ps.setInt(3, u.getIdRol());
                ps.setBoolean(4, u.isEstado());
                ps.setInt(5, u.getIdUsuario());
                ps.executeUpdate();
            }
            Usuario nuevo = buscarPorId(con, u.getIdUsuario());
            AuditoriaUtil.registrar(con, "Seguridad", "usuario", "UPDATE", u.getIdUsuario(), anterior, nuevo);
        }
    }

    /**
     * Cambia ÚNICAMENTE la contraseña de un usuario.
     * Método directo y seguro: solo toca la columna password.
     */
    public void cambiarSoloPassword(int idUsuario, String nuevoHash) throws SQLException {
        String sql = "UPDATE usuario SET password = ? WHERE idUsuario = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Usuario anterior = buscarPorId(con, idUsuario);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nuevoHash);
                ps.setInt(2, idUsuario);
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    throw new SQLException("No se actualizó ninguna fila. El usuario con ID " + idUsuario + " no existe.");
                }
            }
            Usuario nuevo = buscarPorId(con, idUsuario);
            AuditoriaUtil.registrar(con, "Seguridad", "usuario", "CAMBIO_PASSWORD", idUsuario, anterior, nuevo);
        }
    }

    public void eliminarLogico(int idUsuario) throws SQLException {
        String sql = "UPDATE usuario SET estado = false WHERE idUsuario = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Usuario anterior = buscarPorId(con, idUsuario);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }
            Usuario nuevo = buscarPorId(con, idUsuario);
            AuditoriaUtil.registrar(con, "Seguridad", "usuario", "DELETE", idUsuario, anterior, nuevo);
        }
    }
}
