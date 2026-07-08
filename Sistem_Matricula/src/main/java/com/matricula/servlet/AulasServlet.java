package com.matricula.servlet;

import com.matricula.dao.AulaDAO;
import com.matricula.dao.CatalogoDAO;
import com.matricula.model.Aula;
import com.matricula.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/aulas")
public class AulasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            AulaDAO dao = new AulaDAO();
            request.setAttribute("aulas", dao.listarTodos());
            
            CatalogoDAO catDao = new CatalogoDAO();
            request.setAttribute("anios", catDao.listarAnios());
            request.setAttribute("niveles", catDao.listarNiveles());
            request.setAttribute("grados", catDao.listarGrados());

            request.getRequestDispatcher("/modulos/aulas.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error al listar aulas", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        try {
            AulaDAO dao = new AulaDAO();
            
            if ("crear".equals(accion)) {
                Usuario u = (Usuario) request.getSession().getAttribute("usuario");
                String ip = request.getRemoteAddr();
                int idUsuario = (u != null) ? u.getIdUsuario() : 0;
                
                Aula a = new Aula();
                a.setCodAnioAcademico(Integer.parseInt(request.getParameter("codAnioAcademico")));
                a.setCodNivel(Integer.parseInt(request.getParameter("codNivel")));
                a.setCodGrado(Integer.parseInt(request.getParameter("codGrado")));
                a.setSeccion(request.getParameter("seccion"));
                a.setCapacidadMaxima(Integer.parseInt(request.getParameter("capacidadMaxima")));
                a.setEstado("1".equals(request.getParameter("estado")));
                dao.insertar(a, idUsuario, ip);
                request.getSession().setAttribute("mensajeExito", "Aula creada correctamente.");
            } else if ("editar".equals(accion)) {
                Usuario u = (Usuario) request.getSession().getAttribute("usuario");
                String ip = request.getRemoteAddr();
                int idUsuario = (u != null) ? u.getIdUsuario() : 0;
                
                Aula a = new Aula();
                a.setCodAula(Integer.parseInt(request.getParameter("codAula")));
                a.setCodAnioAcademico(Integer.parseInt(request.getParameter("codAnioAcademico")));
                a.setCodNivel(Integer.parseInt(request.getParameter("codNivel")));
                a.setCodGrado(Integer.parseInt(request.getParameter("codGrado")));
                a.setSeccion(request.getParameter("seccion"));
                a.setCapacidadMaxima(Integer.parseInt(request.getParameter("capacidadMaxima")));
                a.setEstado("1".equals(request.getParameter("estado")));
                a.setVersion(Integer.parseInt(request.getParameter("version")));
                
                try {
                    dao.actualizar(a, idUsuario, ip);
                    request.getSession().setAttribute("mensajeExito", "Aula actualizada correctamente.");
                } catch (java.sql.SQLException ex) {
                    if (ex.getSQLState() != null && ex.getSQLState().equals("45000")) {
                        request.getSession().setAttribute("mensajeError", "Error de concurrencia: " + ex.getMessage());
                    } else {
                        throw ex;
                    }
                }
            } else if ("eliminar".equals(accion)) {
                Usuario u = (Usuario) request.getSession().getAttribute("usuario");
                String ip = request.getRemoteAddr();
                int idUsuario = (u != null) ? u.getIdUsuario() : 0;
                
                int codAula = Integer.parseInt(request.getParameter("codAula"));
                dao.eliminarLogico(codAula, idUsuario, ip);
                request.getSession().setAttribute("mensajeExito", "Aula eliminada correctamente.");
            }
            
            response.sendRedirect(request.getContextPath() + "/aulas");
        } catch (Exception e) {
            String msj = e.getMessage();
            if (msj != null && msj.contains("Duplicate entry")) {
                request.getSession().setAttribute("mensajeError", "Error: Ya existe un aula registrada con ese Grado y Sección en el mismo Año Académico.");
            } else {
                request.getSession().setAttribute("mensajeError", "Ocurrió un error en la operación.");
            }
            response.sendRedirect(request.getContextPath() + "/aulas");
        }
    }
}
