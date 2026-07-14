package com.matricula.dao;

import com.matricula.model.Alumno;
import com.matricula.util.AuditoriaUtil;
import com.matricula.util.CifradoAESUtil;
import com.matricula.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {

    public List<Alumno> listarTodos() throws Exception {
        List<Alumno> lista = new ArrayList<>();
        String sql = "SELECT a.codAlumno, a.codTipoDocumento, a.numeroDocumento, a.nombres, a.apellidoPaterno, a.apellidoMaterno, a.fechaNacimiento, a.estado, t.nombre as nombreTipoDocumento "
                   + "FROM alumno a INNER JOIN tipoDocumento t ON a.codTipoDocumento = t.codTipoDocumento "
                   + "ORDER BY a.apellidoPaterno, a.apellidoMaterno";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Alumno a = new Alumno();
                a.setCodAlumno(rs.getInt("codAlumno"));
                a.setCodTipoDocumento(rs.getInt("codTipoDocumento"));
                a.setNombreTipoDocumento(rs.getString("nombreTipoDocumento"));
                
                a.setNumeroDocumento(CifradoAESUtil.descifrar(rs.getString("numeroDocumento")));
                a.setFechaNacimiento(CifradoAESUtil.descifrar(rs.getString("fechaNacimiento")));
                
                a.setNombres(rs.getString("nombres"));
                a.setApellidoPaterno(rs.getString("apellidoPaterno"));
                a.setApellidoMaterno(rs.getString("apellidoMaterno"));
                a.setEstado(rs.getBoolean("estado"));
                lista.add(a);
            }
        }
        return lista;
    }

    public List<Alumno> listarActivos() throws Exception {
        List<Alumno> lista = new ArrayList<>();
        String sql = "SELECT a.codAlumno, a.codTipoDocumento, a.numeroDocumento, a.nombres, a.apellidoPaterno, a.apellidoMaterno, a.fechaNacimiento, a.estado, t.nombre as nombreTipoDocumento "
                   + "FROM alumno a INNER JOIN tipoDocumento t ON a.codTipoDocumento = t.codTipoDocumento "
                   + "WHERE a.estado = 1 "
                   + "ORDER BY a.apellidoPaterno, a.apellidoMaterno";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Alumno a = new Alumno();
                a.setCodAlumno(rs.getInt("codAlumno"));
                a.setCodTipoDocumento(rs.getInt("codTipoDocumento"));
                a.setNombreTipoDocumento(rs.getString("nombreTipoDocumento"));
                a.setNumeroDocumento(CifradoAESUtil.descifrar(rs.getString("numeroDocumento")));
                a.setFechaNacimiento(CifradoAESUtil.descifrar(rs.getString("fechaNacimiento")));
                a.setNombres(rs.getString("nombres"));
                a.setApellidoPaterno(rs.getString("apellidoPaterno"));
                a.setApellidoMaterno(rs.getString("apellidoMaterno"));
                a.setEstado(rs.getBoolean("estado"));
                lista.add(a);
            }
        }
        return lista;
    }

    public void insertar(Alumno a, int idUsuario, String ip) throws Exception {
        String sql = "INSERT INTO alumno (codTipoDocumento, numeroDocumento, nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, a.getCodTipoDocumento());
                               
                ps.setString(2, CifradoAESUtil.cifrar(a.getNumeroDocumento()));
                ps.setString(3, a.getNombres());
                ps.setString(4, a.getApellidoPaterno());
                ps.setString(5, a.getApellidoMaterno());
                ps.setString(6, CifradoAESUtil.cifrar(a.getFechaNacimiento()));
                ps.setBoolean(7, a.isEstado());
                
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int codGenerado = rs.getInt(1);
                        a.setCodAlumno(codGenerado);
                    }
                }
            }
            AuditoriaUtil.registrar(con, "Alumnos", "alumno", "INSERT", a.getCodAlumno(), null, a);
        }
    }

    public void actualizar(Alumno a, int idUsuario, String ip) throws Exception {
        String sql = "UPDATE alumno SET codTipoDocumento = ?, numeroDocumento = ?, nombres = ?, apellidoPaterno = ?, apellidoMaterno = ?, fechaNacimiento = ?, estado = ? WHERE codAlumno = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Alumno anterior = buscarPorId(con, a.getCodAlumno());
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, a.getCodTipoDocumento());
                ps.setString(2, CifradoAESUtil.cifrar(a.getNumeroDocumento()));
                ps.setString(3, a.getNombres());
                ps.setString(4, a.getApellidoPaterno());
                ps.setString(5, a.getApellidoMaterno());
                ps.setString(6, CifradoAESUtil.cifrar(a.getFechaNacimiento()));
                ps.setBoolean(7, a.isEstado());
                ps.setInt(8, a.getCodAlumno());
                ps.executeUpdate();
            }
            Alumno nuevo = buscarPorId(con, a.getCodAlumno());
            AuditoriaUtil.registrar(con, "Alumnos", "alumno", "UPDATE", a.getCodAlumno(), anterior, nuevo);
        }
    }

    public void eliminarLogico(int codAlumno, int idUsuario, String ip) throws Exception {
        String sql = "UPDATE alumno SET estado = 0 WHERE codAlumno = ?";
        try (Connection con = ConexionBD.getConexion()) {
            Alumno anterior = buscarPorId(con, codAlumno);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, codAlumno);
                ps.executeUpdate();
            }
            Alumno nuevo = buscarPorId(con, codAlumno);
            AuditoriaUtil.registrar(con, "Alumnos", "alumno", "DELETE_LOGICO", codAlumno, anterior, nuevo);
        }
    }

    private Alumno buscarPorId(Connection con, int codAlumno) throws Exception {
        String sql = "SELECT codAlumno, codTipoDocumento, numeroDocumento, nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, estado FROM alumno WHERE codAlumno = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codAlumno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Alumno a = new Alumno();
                    a.setCodAlumno(rs.getInt("codAlumno"));
                    a.setCodTipoDocumento(rs.getInt("codTipoDocumento"));
                    a.setNumeroDocumento(CifradoAESUtil.descifrar(rs.getString("numeroDocumento")));
                    a.setFechaNacimiento(CifradoAESUtil.descifrar(rs.getString("fechaNacimiento")));
                    a.setNombres(rs.getString("nombres"));
                    a.setApellidoPaterno(rs.getString("apellidoPaterno"));
                    a.setApellidoMaterno(rs.getString("apellidoMaterno"));
                    a.setEstado(rs.getBoolean("estado"));
                    return a;
                }
            }
        }
        return null;
    }
}
