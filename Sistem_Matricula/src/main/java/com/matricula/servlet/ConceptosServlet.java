package com.matricula.servlet;

import com.matricula.dao.CatalogoDAO;
import com.matricula.dao.ConceptoDAO;
import com.matricula.model.Concepto;
import com.matricula.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/conceptos")
public class ConceptosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            ConceptoDAO dao = new ConceptoDAO();
            request.setAttribute("conceptos", dao.listarTodos());
            
            CatalogoDAO catDao = new CatalogoDAO();
            request.setAttribute("anios", catDao.listarAnios());
            request.setAttribute("tiposConcepto", catDao.listarTiposConcepto());

            request.getRequestDispatcher("/modulos/conceptos.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error al listar conceptos", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        try {
            ConceptoDAO dao = new ConceptoDAO();
            
            if ("crear".equals(accion)) {
                Usuario u = (Usuario) request.getSession().getAttribute("usuario");
                String ip = request.getRemoteAddr();
                int idUsuario = (u != null) ? u.getIdUsuario() : 0;
                
                Concepto c = new Concepto();
                c.setCodAnioAcademico(Integer.parseInt(request.getParameter("codAnioAcademico")));
                c.setCodTipoConcepto(Integer.parseInt(request.getParameter("codTipoConcepto")));
                c.setNombreConcepto(request.getParameter("nombreConcepto"));
                c.setMonto(new BigDecimal(request.getParameter("monto")));
                c.setOrdenPago(Integer.parseInt(request.getParameter("ordenPago")));
                c.setObligatorio("1".equals(request.getParameter("obligatorio")));
                c.setEstado("1".equals(request.getParameter("estado")));
                dao.insertar(c, idUsuario, ip);
                request.getSession().setAttribute("mensajeExito", "Concepto creado correctamente.");
            } else if ("editar".equals(accion)) {
                Usuario u = (Usuario) request.getSession().getAttribute("usuario");
                String ip = request.getRemoteAddr();
                int idUsuario = (u != null) ? u.getIdUsuario() : 0;
                
                Concepto c = new Concepto();
                c.setCodConcepto(Integer.parseInt(request.getParameter("codConcepto")));
                c.setCodAnioAcademico(Integer.parseInt(request.getParameter("codAnioAcademico")));
                c.setCodTipoConcepto(Integer.parseInt(request.getParameter("codTipoConcepto")));
                c.setNombreConcepto(request.getParameter("nombreConcepto"));
                c.setMonto(new BigDecimal(request.getParameter("monto")));
                c.setOrdenPago(Integer.parseInt(request.getParameter("ordenPago")));
                c.setObligatorio("1".equals(request.getParameter("obligatorio")));
                c.setEstado("1".equals(request.getParameter("estado")));
                c.setVersion(Integer.parseInt(request.getParameter("version"))); // Versión para Optimistic Locking
                
                try {
                    dao.actualizar(c, idUsuario, ip);
                    request.getSession().setAttribute("mensajeExito", "Concepto actualizado correctamente.");
                } catch (SQLException ex) {
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
                
                int codConcepto = Integer.parseInt(request.getParameter("codConcepto"));
                dao.eliminarLogico(codConcepto, idUsuario, ip);
                request.getSession().setAttribute("mensajeExito", "Concepto eliminado correctamente.");
            } else if ("clonar".equals(accion)) {
                int codAnioOrigen = Integer.parseInt(request.getParameter("codAnioOrigen"));
                int codAnioDestino = Integer.parseInt(request.getParameter("codAnioDestino"));
                
                if (codAnioOrigen == codAnioDestino) {
                    request.getSession().setAttribute("mensajeError", "El año origen y destino no pueden ser iguales.");
                } else {
                    int clonados = dao.clonarConceptos(codAnioOrigen, codAnioDestino);
                    if (clonados > 0) {
                        request.getSession().setAttribute("mensajeExito", "Se clonaron " + clonados + " concepto(s) exitosamente.");
                    } else {
                        request.getSession().setAttribute("mensajeError", "No se clonaron conceptos. Es posible que ya existan en el año destino o no haya conceptos activos en el año origen.");
                    }
                }
            }
            
            response.sendRedirect(request.getContextPath() + "/conceptos");
        } catch (Exception e) {
            String msj = e.getMessage();
            if (msj != null && msj.contains("Duplicate entry")) {
                request.getSession().setAttribute("mensajeError", "Error: Ya existe un concepto con ese nombre para el año académico.");
            } else {
                request.getSession().setAttribute("mensajeError", "Ocurrió un error en la operación: " + msj);
            }
            response.sendRedirect(request.getContextPath() + "/conceptos");
        }
    }
}
