package com.matricula.servlet;

import com.matricula.dao.RolFuncionalidadDAO;
import com.matricula.model.RolFuncionalidad;
import com.matricula.model.Usuario;
import com.matricula.util.AuditoriaUtil;
import com.matricula.util.ConexionBD;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/guardar_permisos")
public class GuardarPermisosServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuarioActual = (Usuario) session.getAttribute("usuario");
        String idRolStr = request.getParameter("idRol");

        if (idRolStr != null && !idRolStr.isEmpty()) {
            int idRol = Integer.parseInt(idRolStr);
            String ip = request.getRemoteAddr();

            try (Connection con = ConexionBD.getConexion()) {
                con.setAutoCommit(false); 

                try {
                    RolFuncionalidadDAO rfDao = new RolFuncionalidadDAO();
                    List<RolFuncionalidad> permisosActuales = rfDao.listarPorRol(idRol);

                    for (RolFuncionalidad rf : permisosActuales) {
                        if (rf.getPadreFuncionalidad() != null) {
                            int idFunc = rf.getIdFuncionalidad();
                            boolean ver = request.getParameter("ver_" + idFunc) != null;
                            boolean crear = request.getParameter("crear_" + idFunc) != null;
                            boolean editar = request.getParameter("editar_" + idFunc) != null;
                            boolean eliminar = request.getParameter("eliminar_" + idFunc) != null;

                            
                            rfDao.actualizarPermisos(con, rf.getIdRolFuncionalidad(), ver, crear, editar, eliminar);
                        }
                    }

                   
                    AuditoriaUtil.registrar(con, "Seguridad", "rol_funcionalidad", "UPDATE_PERMISOS", idRol, null, "Actualizacion de permisos");

                    con.commit(); 
                    session.setAttribute("mensajeExito", "Los permisos se actualizaron correctamente.");
                } catch (Exception ex) {
                    con.rollback();
                    session.setAttribute("mensajeError", "Ocurrió un error al guardar los permisos.");
                    throw ex;
                }
            } catch (Exception e) {
                session.setAttribute("mensajeError", "Error interno al procesar los permisos.");
                throw new ServletException("Error al guardar permisos", e);
            }
        }

        response.sendRedirect(request.getContextPath() + "/permisos?idRol=" + idRolStr);
    }
}
