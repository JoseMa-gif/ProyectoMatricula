/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.matricula.dao;

import com.matricula.model.RolFuncionalidad;
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
public class RolFuncionalidadDAO {
    
      public List<RolFuncionalidad> listarPorRol(int idRol) throws SQLException {
        List<RolFuncionalidad> lista = new ArrayList<>();
        String sql = "SELECT rf.*, f.nombre, f.padre " +
                     "FROM rol_funcionalidad rf " +
                     "INNER JOIN funcionalidad f ON rf.idFuncionalidad = f.idFuncionalidad " +
                     "WHERE rf.idRol = ? " +
                     "ORDER BY f.padre, f.idFuncionalidad";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RolFuncionalidad rf = new RolFuncionalidad();
                    rf.setIdRolFuncionalidad(rs.getInt("idRolFuncionalidad"));
                    rf.setIdRol(rs.getInt("idRol"));
                    rf.setIdFuncionalidad(rs.getInt("idFuncionalidad"));
                    rf.setVer(rs.getBoolean("ver"));
                    rf.setCrear(rs.getBoolean("crear"));
                    rf.setEditar(rs.getBoolean("editar"));
                    rf.setEliminar(rs.getBoolean("eliminar"));
                    rf.setImprimir(rs.getBoolean("imprimir"));
                    rf.setNombreFuncionalidad(rs.getString("nombre"));
                    
                    int padre = rs.getInt("padre");
                    rf.setPadreFuncionalidad(rs.wasNull() ? null : padre);
                    
                    lista.add(rf);
                }
            }
        }
        return lista;
    }
      
       private RolFuncionalidad buscarPorId(Connection con, int idRolFuncionalidad) throws SQLException {
        String sql = "SELECT * FROM rol_funcionalidad WHERE idRolFuncionalidad = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRolFuncionalidad);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RolFuncionalidad rf = new RolFuncionalidad();
                    rf.setIdRolFuncionalidad(rs.getInt("idRolFuncionalidad"));
                    rf.setIdRol(rs.getInt("idRol"));
                    rf.setIdFuncionalidad(rs.getInt("idFuncionalidad"));
                    rf.setVer(rs.getBoolean("ver"));
                    rf.setCrear(rs.getBoolean("crear"));
                    rf.setEditar(rs.getBoolean("editar"));
                    rf.setEliminar(rs.getBoolean("eliminar"));
                    rf.setImprimir(rs.getBoolean("imprimir"));
                    return rf;
                }
            }
        }
        return null;
    }

    public void actualizarPermisos(Connection con, int idRolFuncionalidad, boolean ver, boolean crear, boolean editar, boolean eliminar) throws SQLException {
        RolFuncionalidad anterior = buscarPorId(con, idRolFuncionalidad);
        
        String sql = "UPDATE rol_funcionalidad SET ver = ?, crear = ?, editar = ?, eliminar = ? WHERE idRolFuncionalidad = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, ver);
            ps.setBoolean(2, crear);
            ps.setBoolean(3, editar);
            ps.setBoolean(4, eliminar);
            ps.setInt(5, idRolFuncionalidad);
            ps.executeUpdate();
        }
        
        RolFuncionalidad nuevo = buscarPorId(con, idRolFuncionalidad);
        AuditoriaUtil.registrar(con, "Seguridad", "rol_funcionalidad", "UPDATE_PERMISOS", idRolFuncionalidad, anterior, nuevo);
    }
    
  
}
