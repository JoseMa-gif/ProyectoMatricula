/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.matricula.servlet;

import com.matricula.dao.RolDAO;
import com.matricula.dao.UsuarioDAO;
import com.matricula.model.Rol;
import com.matricula.model.Usuario;
import com.matricula.util.SeguridadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/usuarios")
public class UsuariosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            UsuarioDAO dao = new UsuarioDAO();
            List<Usuario> lista = dao.listarTodos();
            request.setAttribute("usuarios", lista);
            
            
            RolDAO rolDao = new RolDAO();
            List<Rol> roles = rolDao.listarTodos();
            request.setAttribute("roles", roles);

            request.getRequestDispatcher("/modulos/usuarios.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error al listar usuarios", e);
        }
    } 
     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        try {
            UsuarioDAO dao = new UsuarioDAO();
            
            if ("crear".equals(accion)) {
                Usuario u = new Usuario();
                u.setUsuario(request.getParameter("usuario"));
                String rawPassword = request.getParameter("password");
                u.setPassword(SeguridadUtil.hashPassword(rawPassword));
                u.setIdRol(Integer.parseInt(request.getParameter("idRol")));
                u.setEstado("1".equals(request.getParameter("estado")));
                dao.insertar(u);
                request.getSession().setAttribute("mensajeExito", "Usuario creado correctamente.");
            } else if ("editar".equals(accion)) {
                Usuario u = new Usuario();
                u.setIdUsuario(Integer.parseInt(request.getParameter("idUsuario")));
                u.setUsuario(request.getParameter("usuario"));
                u.setIdRol(Integer.parseInt(request.getParameter("idRol")));
                u.setEstado("1".equals(request.getParameter("estado")));
                
                String rawPassword = request.getParameter("password");
                if (rawPassword != null && !rawPassword.trim().isEmpty()) {
                    u.setPassword(SeguridadUtil.hashPassword(rawPassword));
                    dao.actualizarConPassword(u);
                } else {
                    dao.actualizar(u);
                }
                
                request.getSession().setAttribute("mensajeExito", "Usuario actualizado correctamente.");
            } else if ("eliminar".equals(accion)) {
                int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
                try {
                    dao.eliminarLogico(idUsuario);
                    request.getSession().setAttribute(
                            "mensajeExito",
                            "Usuario eliminado correctamente."
                    );
                } catch (SQLException e) {
                    request.getSession().setAttribute(
                            "mensajeError",
                            e.getMessage()
                    );
                }
            } else if ("resetear_password".equals(accion)) {
                
                int idUsuario     = Integer.parseInt(request.getParameter("idUsuario"));
                String nuevaPw    = request.getParameter("nuevaPassword");
                String confirmarPw = request.getParameter("confirmarPassword");

                if (nuevaPw == null || nuevaPw.trim().length() < 6) {
                    request.getSession().setAttribute("mensajeError", "La nueva contraseña debe tener al menos 6 caracteres.");
                    response.sendRedirect(request.getContextPath() + "/usuarios");
                    return;
                }
                if (!nuevaPw.equals(confirmarPw)) {
                    request.getSession().setAttribute("mensajeError", "Las contraseñas no coinciden.");
                    response.sendRedirect(request.getContextPath() + "/usuarios");
                    return;
                }

                Usuario uTarget = dao.buscarPorId(idUsuario);
                if (uTarget == null) {
                    request.getSession().setAttribute("mensajeError", "Usuario no encontrado.");
                    response.sendRedirect(request.getContextPath() + "/usuarios");
                    return;
                }

                
                String nuevoHash = SeguridadUtil.hashPassword(nuevaPw);
                dao.cambiarSoloPassword(idUsuario, nuevoHash);

                Usuario adminSesion = (Usuario) request.getSession().getAttribute("usuario");
                String adminNombre = (adminSesion != null) ? adminSesion.getUsuario() : "desconocido";
                request.getSession().setAttribute("mensajeExito",
                    "✅ Contraseña de '" + uTarget.getUsuario() + "' actualizada por " + adminNombre + ".");
            }
            
            response.sendRedirect(request.getContextPath() + "/usuarios");
        } catch (Exception e) {
            String msj = e.getMessage();
            if (msj != null && msj.contains("Duplicate entry")) {
                request.getSession().setAttribute("mensajeError", "Error: Ya existe un usuario registrado con ese nombre.");
            } else {
                request.getSession().setAttribute("mensajeError", "Ocurrió un error en la operación.");
            }
            response.sendRedirect(request.getContextPath() + "/usuarios");
        }
    }
}
