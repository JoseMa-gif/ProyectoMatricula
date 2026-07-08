package com.matricula.dao;

import com.matricula.model.Aula;
import com.matricula.util.AuditoriaUtil;
import com.matricula.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AulaDAO {

    public List<Aula> listarTodos() throws SQLException {
        List<Aula> lista = new ArrayList<>();
        String sql = "SELECT a.codAula, a.seccion, a.capacidadMaxima, a.version, a.estado, a.codAnioAcademico, a.codNivel, a.codGrado, "
                   + "an.anio as nombreAnio, n.nombre as nombreNivel, g.nombre as nombreGrado "
                   + "FROM aula a "
                   + "INNER JOIN anioAcademico an ON a.codAnioAcademico = an.codAnioAcademico "
                   + "INNER JOIN nivel n ON a.codNivel = n.codNivel "
                   + "INNER JOIN grado g ON a.codGrado = g.codGrado "
                   + "ORDER BY an.anio DESC, n.codNivel, g.codGrado, a.seccion";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Aula a = new Aula();
                a.setCodAula(rs.getInt("codAula"));
                a.setCodAnioAcademico(rs.getInt("codAnioAcademico"));
                a.setCodNivel(rs.getInt("codNivel"));
                a.setCodGrado(rs.getInt("codGrado"));
                a.setSeccion(rs.getString("seccion"));
                a.setCapacidadMaxima(rs.getInt("capacidadMaxima"));
                a.setVersion(rs.getInt("version"));
                a.setEstado(rs.getBoolean("estado"));
                a.setNombreAnio(rs.getString("nombreAnio"));
                a.setNombreNivel(rs.getString("nombreNivel"));
                a.setNombreGrado(rs.getString("nombreGrado"));
                lista.add(a);
            }
        }
        return lista;
    }

    public List<Aula> listarActivas() throws SQLException {
        List<Aula> lista = new ArrayList<>();
        String sql = "SELECT a.codAula, a.seccion, a.capacidadMaxima, a.version, a.estado, a.codAnioAcademico, a.codNivel, a.codGrado, "
                   + "an.anio as nombreAnio, n.nombre as nombreNivel, g.nombre as nombreGrado, "
                   + "(SELECT COUNT(*) FROM matricula m WHERE m.codAula = a.codAula AND m.estado = 1) as matriculados "
                   + "FROM aula a "
                   + "INNER JOIN anioAcademico an ON a.codAnioAcademico = an.codAnioAcademico "
                   + "INNER JOIN nivel n ON a.codNivel = n.codNivel "
                   + "INNER JOIN grado g ON a.codGrado = g.codGrado "
                   + "WHERE a.estado = 1 "
                   + "ORDER BY an.anio DESC, n.codNivel, g.codGrado, a.seccion";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Aula a = new Aula();
                a.setCodAula(rs.getInt("codAula"));
                a.setCodAnioAcademico(rs.getInt("codAnioAcademico"));
                a.setCodNivel(rs.getInt("codNivel"));
                a.setCodGrado(rs.getInt("codGrado"));
                a.setSeccion(rs.getString("seccion"));
                a.setCapacidadMaxima(rs.getInt("capacidadMaxima"));
                a.setVersion(rs.getInt("version"));
                a.setEstado(rs.getBoolean("estado"));
                a.setNombreAnio(rs.getString("nombreAnio"));
                a.setNombreNivel(rs.getString("nombreNivel"));
                a.setNombreGrado(rs.getString("nombreGrado"));
                a.setMatriculados(rs.getInt("matriculados"));
                lista.add(a);
            }
        }
        return lista;
    }

    public void insertar(Aula a, int idUsuario, String ip) throws SQLException {
        String sql = "INSERT INTO aula (codAnioAcademico, codNivel, codGrado, seccion, capacidadMaxima, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, a.getCodAnioAcademico());
                ps.setInt(2, a.getCodNivel());
                ps.setInt(3, a.getCodGrado());
                ps.setString(4, a.getSeccion());
                ps.setInt(5, a.getCapacidadMaxima());
                ps.setBoolean(6, a.isEstado());
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int codGenerado = rs.getInt(1);
                        a.setCodAula(codGenerado);
                    }
                }
            }
            AuditoriaUtil.registrar(con, "Aulas", "aula", "INSERT", a.getCodAula(), null, a);
        }
    }

    public void actualizar(Aula a, int idUsuario, String ip) throws SQLException {
        String sql = "UPDATE aula SET codAnioAcademico = ?, codNivel = ?, codGrado = ?, seccion = ?, capacidadMaxima = ?, estado = ?, version = ? WHERE codAula = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Aula anterior = buscarPorId(con, a.getCodAula());
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, a.getCodAnioAcademico());
                ps.setInt(2, a.getCodNivel());
                ps.setInt(3, a.getCodGrado());
                ps.setString(4, a.getSeccion());
                ps.setInt(5, a.getCapacidadMaxima());
                ps.setBoolean(6, a.isEstado());
                ps.setInt(7, a.getVersion());
                ps.setInt(8, a.getCodAula());
                ps.executeUpdate();
            }
            Aula nuevo = buscarPorId(con, a.getCodAula());
            AuditoriaUtil.registrar(con, "Aulas", "aula", "UPDATE", a.getCodAula(), anterior, nuevo);
        }
    }

    public void eliminarLogico(int codAula, int idUsuario, String ip) throws SQLException {
        String sql = "UPDATE aula SET estado = 0 WHERE codAula = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Aula anterior = buscarPorId(con, codAula);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, codAula);
                ps.executeUpdate();
            }
            Aula nuevo = buscarPorId(con, codAula);
            AuditoriaUtil.registrar(con, "Aulas", "aula", "DELETE_LOGICO", codAula, anterior, nuevo);
        }
    }

    private Aula buscarPorId(Connection con, int codAula) throws SQLException {
        String sql = "SELECT codAula, codAnioAcademico, codNivel, codGrado, seccion, capacidadMaxima, version, estado FROM aula WHERE codAula = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codAula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Aula a = new Aula();
                    a.setCodAula(rs.getInt("codAula"));
                    a.setCodAnioAcademico(rs.getInt("codAnioAcademico"));
                    a.setCodNivel(rs.getInt("codNivel"));
                    a.setCodGrado(rs.getInt("codGrado"));
                    a.setSeccion(rs.getString("seccion"));
                    a.setCapacidadMaxima(rs.getInt("capacidadMaxima"));
                    a.setVersion(rs.getInt("version"));
                    a.setEstado(rs.getBoolean("estado"));
                    return a;
                }
            }
        }
        return null;
    }
}
