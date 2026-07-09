package com.matricula.dao;

import com.matricula.model.Concepto;
import com.matricula.model.Matricula;
import com.matricula.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.matricula.util.AuditoriaUtil;

public class MatriculaDAO {

    public List<Matricula> listarTodos() throws SQLException {
        List<Matricula> lista = new ArrayList<>();
        String sql = "SELECT m.codMatricula, m.codAlumno, m.codAula, m.codAnioAcademico, m.fechaMatricula, m.version, m.estado, "
                   + "CONCAT(al.apellidoPaterno, ' ', al.apellidoMaterno, ', ', al.nombres) as nombreAlumno, "
                   + "CONCAT(n.nombre, ' ', g.nombre, ' \"', a.seccion, '\"') as descripcionAula, "
                   + "an.anio as nombreAnio "
                   + "FROM matricula m "
                   + "INNER JOIN alumno al ON m.codAlumno = al.codAlumno "
                   + "INNER JOIN aula a ON m.codAula = a.codAula "
                   + "INNER JOIN nivel n ON a.codNivel = n.codNivel "
                   + "INNER JOIN grado g ON a.codGrado = g.codGrado "
                   + "INNER JOIN anioAcademico an ON m.codAnioAcademico = an.codAnioAcademico "
                   + "ORDER BY m.codMatricula DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Matricula m = new Matricula();
                m.setCodMatricula(rs.getInt("codMatricula"));
                m.setCodAlumno(rs.getInt("codAlumno"));
                m.setCodAula(rs.getInt("codAula"));
                m.setCodAnioAcademico(rs.getInt("codAnioAcademico"));
                m.setFechaMatricula(rs.getTimestamp("fechaMatricula"));
                m.setVersion(rs.getInt("version"));
                m.setEstado(rs.getBoolean("estado"));
                m.setNombreAlumno(rs.getString("nombreAlumno"));
                m.setDescripcionAula(rs.getString("descripcionAula"));
                m.setNombreAnio(rs.getString("nombreAnio"));
                lista.add(m);
            }
        }
        return lista;
    }

