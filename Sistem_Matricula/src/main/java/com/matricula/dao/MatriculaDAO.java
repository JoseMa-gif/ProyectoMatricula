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

    public List<Matricula> listarActivas() throws SQLException {
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
                   + "WHERE m.estado = 1 "
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

    public int contarMatriculadosPorAula(int codAula, int codAnioAcademico) throws SQLException {
        String sql = "SELECT COUNT(*) FROM matricula WHERE codAula = ? AND codAnioAcademico = ? AND estado = 1";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codAula);
            ps.setInt(2, codAnioAcademico);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int obtenerCapacidadMaximaAula(int codAula) throws SQLException {
         
        String sql = "SELECT valor FROM parametro WHERE nombre = 'CAPACIDAD_MAXIMA_DEFECTO'";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Integer.parseInt(rs.getString("valor"));
            }
        }
        return 35; 
    }

    public boolean tieneDeudasAnteriores(int codAlumno) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cuota c "
                   + "INNER JOIN matricula m ON c.codMatricula = m.codMatricula "
                   + "WHERE m.codAlumno = ? AND c.estado = 'PENDIENTE' AND m.estado = 1";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codAlumno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public void registrarMatriculaTransaccional(Matricula m, List<Concepto> conceptosDelAnio) throws SQLException {
        Connection con = null;
        PreparedStatement psMatricula = null;
        PreparedStatement psCuota = null;
        ResultSet rsKeys = null;

        String sqlMatricula = "INSERT INTO matricula (codAlumno, codAula, codAnioAcademico, estado, usuarioRegistro) VALUES (?, ?, ?, ?, ?)";
        String sqlCuota = "INSERT INTO cuota (codMatricula, codConcepto, monto, ordenPago, estado) VALUES (?, ?, ?, ?, 'PENDIENTE')";

        try {
            con = ConexionBD.getConexion();
            con.setAutoCommit(false);  

             
            psMatricula = con.prepareStatement(sqlMatricula, Statement.RETURN_GENERATED_KEYS);
            psMatricula.setInt(1, m.getCodAlumno());
            psMatricula.setInt(2, m.getCodAula());
            psMatricula.setInt(3, m.getCodAnioAcademico());
            psMatricula.setBoolean(4, m.isEstado());
            psMatricula.setInt(5, m.getUsuarioRegistro());
            psMatricula.executeUpdate();

             
            rsKeys = psMatricula.getGeneratedKeys();
            int codMatriculaGenerado = 0;
            if (rsKeys.next()) {
                codMatriculaGenerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID de la matrícula generada.");
            }

             
            if (conceptosDelAnio != null && !conceptosDelAnio.isEmpty()) {
                psCuota = con.prepareStatement(sqlCuota);
                for (Concepto c : conceptosDelAnio) {
                    if (c.isObligatorio()) {
                        psCuota.setInt(1, codMatriculaGenerado);
                        psCuota.setInt(2, c.getCodConcepto());
                        psCuota.setBigDecimal(3, c.getMonto());
                        psCuota.setInt(4, c.getOrdenPago());
                        psCuota.addBatch();
                    }
                }
                psCuota.executeBatch();
            }

             
            m.setCodMatricula(codMatriculaGenerado);
            AuditoriaUtil.registrar(con, "Matriculas", "matricula", "MATRÍCULA", codMatriculaGenerado, null, m);

            con.commit(); // Confirmar Transacción
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback(); // Deshacer en caso de error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Cerrar recursos
            if (rsKeys != null) try { rsKeys.close(); } catch (SQLException e) {}
            if (psMatricula != null) try { psMatricula.close(); } catch (SQLException e) {}
            if (psCuota != null) try { psCuota.close(); } catch (SQLException e) {}
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    public void anularMatricula(int codMatricula) throws SQLException {
         
         
        String sql = "UPDATE matricula SET estado = 0 WHERE codMatricula = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Matricula anterior = buscarPorIdSimple(con, codMatricula);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, codMatricula);
                ps.executeUpdate();
            }
            Matricula nuevo = buscarPorIdSimple(con, codMatricula);
            AuditoriaUtil.registrar(con, "Matriculas", "matricula", "ANULAR", codMatricula, anterior, nuevo);
        }
    }
    
    public void restaurarMatricula(int codMatricula) throws SQLException {
        String sql = "UPDATE matricula SET estado = 1 WHERE codMatricula = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Matricula anterior = buscarPorIdSimple(con, codMatricula);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, codMatricula);
                ps.executeUpdate();
            }
            Matricula nuevo = buscarPorIdSimple(con, codMatricula);
            AuditoriaUtil.registrar(con, "Matriculas", "matricula", "RESTAURAR", codMatricula, anterior, nuevo);
        }
    }
    
    private Matricula buscarPorIdSimple(Connection con, int codMatricula) throws SQLException {
        String sql = "SELECT codMatricula, codAlumno, codAula, codAnioAcademico, version, estado FROM matricula WHERE codMatricula = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codMatricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Matricula m = new Matricula();
                    m.setCodMatricula(rs.getInt("codMatricula"));
                    m.setCodAlumno(rs.getInt("codAlumno"));
                    m.setCodAula(rs.getInt("codAula"));
                    m.setCodAnioAcademico(rs.getInt("codAnioAcademico"));
                    m.setVersion(rs.getInt("version"));
                    m.setEstado(rs.getBoolean("estado"));
                    return m;
                }
            }
        }
        return null;
    }

