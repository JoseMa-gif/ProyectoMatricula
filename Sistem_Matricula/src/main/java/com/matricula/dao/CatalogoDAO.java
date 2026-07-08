package com.matricula.dao;

import com.matricula.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogoDAO {

    public List<Map<String, Object>> listarAnios() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT codAnioAcademico, anio FROM anioAcademico WHERE estado = 1 ORDER BY anio DESC";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("codAnioAcademico", rs.getInt("codAnioAcademico"));
                map.put("anio", rs.getInt("anio"));
                lista.add(map);
            }
        }
        return lista;
    }

    public List<Map<String, Object>> listarNiveles() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT codNivel, nombre FROM nivel ORDER BY codNivel";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("codNivel", rs.getInt("codNivel"));
                map.put("nombre", rs.getString("nombre"));
                lista.add(map);
            }
        }
        return lista;
    }

    public List<Map<String, Object>> listarGrados() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT codGrado, codNivel, nombre FROM grado ORDER BY codNivel, codGrado";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("codGrado", rs.getInt("codGrado"));
                map.put("codNivel", rs.getInt("codNivel"));
                map.put("nombre", rs.getString("nombre"));
                lista.add(map);
            }
        }
        return lista;
    }

    public List<Map<String, Object>> listarTiposDocumento() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT codTipoDocumento, nombre FROM tipoDocumento ORDER BY codTipoDocumento";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("codTipoDocumento", rs.getInt("codTipoDocumento"));
                map.put("nombre", rs.getString("nombre"));
                lista.add(map);
            }
        }
        return lista;
    }

    public List<Map<String, Object>> listarTiposConcepto() throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT codTipoConcepto, nombre FROM tipoConcepto ORDER BY codTipoConcepto";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("codTipoConcepto", rs.getInt("codTipoConcepto"));
                map.put("nombre", rs.getString("nombre"));
                lista.add(map);
            }
        }
        return lista;
    }
}
