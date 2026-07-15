package com.matricula.servlet;

import com.matricula.dao.AlumnoDAO;
import com.matricula.dao.CatalogoDAO;
import com.matricula.model.Alumno;
import com.matricula.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/alumnos")
public class AlumnosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            AlumnoDAO dao = new AlumnoDAO();
            request.setAttribute("alumnos", dao.listarTodos());
            
            CatalogoDAO catDao = new CatalogoDAO();
            request.setAttribute("tiposDocumento", catDao.listarTiposDocumento());

            request.getRequestDispatcher("/modulos/alumnos.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error al listar alumnos", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        try {
            AlumnoDAO dao = new AlumnoDAO();
            
            if ("crear".equals(accion)) {
                Usuario u = (Usuario) request.getSession().getAttribute("usuario");
                String ip = request.getRemoteAddr();
                int idUsuario = (u != null) ? u.getIdUsuario() : 0;
                
                Alumno a = new Alumno();
                a.setCodTipoDocumento(Integer.parseInt(request.getParameter("codTipoDocumento")));
                a.setNumeroDocumento(request.getParameter("numeroDocumento"));
                a.setNombres(request.getParameter("nombres"));
                a.setApellidoPaterno(request.getParameter("apellidoPaterno"));
                a.setApellidoMaterno(request.getParameter("apellidoMaterno"));
                a.setFechaNacimiento(request.getParameter("fechaNacimiento"));
                a.setEstado("1".equals(request.getParameter("estado")));
                dao.insertar(a, idUsuario, ip);
                request.getSession().setAttribute("mensajeExito", "Alumno creado correctamente. Documento y Fecha han sido cifrados con AES.");
            } else if ("editar".equals(accion)) {
                Usuario u = (Usuario) request.getSession().getAttribute("usuario");
                String ip = request.getRemoteAddr();
                int idUsuario = (u != null) ? u.getIdUsuario() : 0;
                
                Alumno a = new Alumno();
                a.setCodAlumno(Integer.parseInt(request.getParameter("codAlumno")));
                a.setCodTipoDocumento(Integer.parseInt(request.getParameter("codTipoDocumento")));
                a.setNumeroDocumento(request.getParameter("numeroDocumento"));
                a.setNombres(request.getParameter("nombres"));
                a.setApellidoPaterno(request.getParameter("apellidoPaterno"));
                a.setApellidoMaterno(request.getParameter("apellidoMaterno"));
                a.setFechaNacimiento(request.getParameter("fechaNacimiento"));
                a.setEstado("1".equals(request.getParameter("estado")));
                dao.actualizar(a, idUsuario, ip);
                request.getSession().setAttribute("mensajeExito", "Alumno actualizado correctamente.");
            } else if ("eliminar".equals(accion)) {
                Usuario u = (Usuario) request.getSession().getAttribute("usuario");
                String ip = request.getRemoteAddr();
                int idUsuario = (u != null) ? u.getIdUsuario() : 0;
                
                int codAlumno = Integer.parseInt(request.getParameter("codAlumno"));
                dao.eliminarLogico(codAlumno, idUsuario, ip);
                request.getSession().setAttribute("mensajeExito", "Alumno eliminado correctamente.");
            }
            
            response.sendRedirect(request.getContextPath() + "/alumnos");
        } catch (Exception e) {
            String msj = e.getMessage();
            if (msj != null && msj.contains("Duplicate entry")) {
                request.getSession().setAttribute("mensajeError", "Error: Ya existe un alumno registrado con ese número de documento.");
            } else {
                request.getSession().setAttribute("mensajeError", "Ocurrió un error en la operación: " + msj);
            }
            response.sendRedirect(request.getContextPath() + "/alumnos");
        }
    }
 
}
