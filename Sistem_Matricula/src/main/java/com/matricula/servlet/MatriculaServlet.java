package com.matricula.servlet;

import com.matricula.dao.AlumnoDAO;
import com.matricula.dao.AulaDAO;
import com.matricula.dao.CatalogoDAO;
import com.matricula.dao.ConceptoDAO;
import com.matricula.dao.MatriculaDAO;
import com.matricula.dao.UsuarioDAO;
import com.matricula.model.Concepto;
import com.matricula.model.Matricula;
import com.matricula.model.Usuario;
import com.matricula.util.TOTPUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/matricula")
public class MatriculaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            MatriculaDAO dao = new MatriculaDAO();
            request.setAttribute("matriculas", dao.listarTodos());
            
            CatalogoDAO catDao = new CatalogoDAO();
            request.setAttribute("anios", catDao.listarAnios());
            
            AlumnoDAO alumnoDao = new AlumnoDAO();
            request.setAttribute("alumnos", alumnoDao.listarActivos());
            
            AulaDAO aulaDao = new AulaDAO();
            request.setAttribute("aulas", aulaDao.listarActivas());

            Usuario u = (Usuario) request.getSession().getAttribute("usuario");
            if (u.getSecret2FA() == null || u.getSecret2FA().isEmpty()) {
                String secret = TOTPUtil.generarClaveSecreta();
                String qrUrl = TOTPUtil.generarURLCodigoQR(secret, u.getUsuario());
                request.setAttribute("nuevoSecret2FA", secret);
                request.setAttribute("qrUrl2FA", qrUrl);
            }

            request.getRequestDispatcher("/modulos/matricula.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error al listar matrículas", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        try {
            MatriculaDAO dao = new MatriculaDAO();
            
            if ("crear".equals(accion)) {
                Usuario u = (Usuario) request.getSession().getAttribute("usuario");
                String tokenStr = request.getParameter("token2FA");
                int token2FA = tokenStr != null && !tokenStr.isEmpty() ? Integer.parseInt(tokenStr) : 0;
                
                String currentSecret = u.getSecret2FA();
                if (currentSecret == null || currentSecret.isEmpty()) {
                    String newSecret = request.getParameter("nuevoSecret2FA");
                    if (!TOTPUtil.validarToken(newSecret, token2FA)) {
                        throw new Exception("Código de Autenticación incorrecto. Escanee el QR nuevamente y asegúrese de ingresar el código actual.");
                    }
                    UsuarioDAO uDao = new UsuarioDAO();
                    uDao.actualizarSecret2FA(u.getIdUsuario(), newSecret);
                    u.setSecret2FA(newSecret);
                    request.getSession().setAttribute("usuario", u);
                } else {
                    if (!TOTPUtil.validarToken(currentSecret, token2FA)) {
                        throw new Exception("Código de Autenticación de 6 dígitos incorrecto.");
                    }
                }

                Matricula m = new Matricula();
                int codAlumno = Integer.parseInt(request.getParameter("codAlumno"));
                int codAula = Integer.parseInt(request.getParameter("codAula"));
                int codAnioAcademico = Integer.parseInt(request.getParameter("codAnioAcademico"));
                
                if (dao.tieneDeudasAnteriores(codAlumno)) {
                    throw new Exception("El alumno tiene deudas anteriores pendientes. Regularice sus pagos en Caja antes de matricularlo.");
                }
                
                int vacantesOcupadas = dao.contarMatriculadosPorAula(codAula, codAnioAcademico);
                int capacidadMax = dao.obtenerCapacidadMaximaAula(codAula);
                if (vacantesOcupadas >= capacidadMax) {
                    throw new Exception("El aula seleccionada no tiene vacantes disponibles (Capacidad Máxima: " + capacidadMax + ").");
                }

                ConceptoDAO conceptoDao = new ConceptoDAO();
                List<Concepto> conceptos = conceptoDao.listarPorAnio(codAnioAcademico);
                if (conceptos == null || conceptos.isEmpty()) {
                    throw new Exception("No existen conceptos en el tarifario activos para este año. Configure el tarifario primero.");
                }

                m.setCodAlumno(codAlumno);
                m.setCodAula(codAula);
                m.setCodAnioAcademico(codAnioAcademico);
                m.setEstado(true); 
                m.setUsuarioRegistro(u.getIdUsuario());
                
                // Llamada Transaccional
                dao.registrarMatriculaTransaccional(m, conceptos);
                
                request.getSession().setAttribute("mensajeExito", "Matrícula registrada correctamente tras validación 2FA. Se generaron las cuotas.");
            } else if ("anular".equals(accion)) {
                int codMatricula = Integer.parseInt(request.getParameter("codMatricula"));
                dao.anularMatricula(codMatricula);
                request.getSession().setAttribute("mensajeExito", "Matrícula anulada correctamente.");
            } else if ("restaurar".equals(accion)) {
                int codMatricula = Integer.parseInt(request.getParameter("codMatricula"));
                dao.restaurarMatricula(codMatricula);
                request.getSession().setAttribute("mensajeExito", "Matrícula restaurada a estado Activo.");
            }
            
            response.sendRedirect(request.getContextPath() + "/matricula");
        } catch (Exception e) {
            String msj = e.getMessage();
            if (msj != null && msj.contains("Duplicate entry")) {
                request.getSession().setAttribute("mensajeError", "Error: El alumno ya se encuentra matriculado en este Año Académico.");
            } else {
                request.getSession().setAttribute("mensajeError", "Ocurrió un error en la operación: " + msj);
            }
            response.sendRedirect(request.getContextPath() + "/matricula");
        }
    }
}
