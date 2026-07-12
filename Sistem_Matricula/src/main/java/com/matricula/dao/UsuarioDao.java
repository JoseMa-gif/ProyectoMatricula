/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.matricula.dao;

import com.matricula.model.Usuario;
import com.matricula.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JOSE
 */
public class UsuarioDAO {
    
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
}
