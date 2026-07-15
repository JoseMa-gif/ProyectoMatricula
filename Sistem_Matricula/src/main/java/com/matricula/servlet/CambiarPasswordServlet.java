package com.matricula.servlet;

import com.matricula.dao.UsuarioDAO;
import com.matricula.model.Usuario;
import com.matricula.util.AuditoriaUtil;
import com.matricula.util.ConexionBD;
import com.matricula.util.SeguridadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/cambiar_password")
public class CambiarPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 
        request.getRequestDispatcher("/modulos/cambiar_password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuarioSesion = (Usuario) request.getSession().getAttribute("usuario");
        if (usuarioSesion == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String passwordActual    = request.getParameter("passwordActual");
        String passwordNueva     = request.getParameter("passwordNueva");
        String passwordConfirmar = request.getParameter("passwordConfirmar");

  
        if (passwordActual == null || passwordActual.trim().isEmpty()
                || passwordNueva == null || passwordNueva.trim().isEmpty()
                || passwordConfirmar == null || passwordConfirmar.trim().isEmpty()) {
            request.getSession().setAttribute("mensajeError", "Todos los campos son obligatorios.");
            response.sendRedirect(request.getContextPath() + "/cambiar_password");
            return;
        }

        if (!passwordNueva.equals(passwordConfirmar)) {
            request.getSession().setAttribute("mensajeError", "La nueva contraseña y su confirmación no coinciden.");
            response.sendRedirect(request.getContextPath() + "/cambiar_password");
            return;
        }

        if (passwordNueva.length() < 6) {
            request.getSession().setAttribute("mensajeError", "La nueva contraseña debe tener al menos 6 caracteres.");
            response.sendRedirect(request.getContextPath() + "/cambiar_password");
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAO();

   
            Usuario usuarioBD = dao.buscarPorUsuario(usuarioSesion.getUsuario());
            if (usuarioBD == null) {
                request.getSession().setAttribute("mensajeError", "No se encontró el usuario en el sistema.");
                response.sendRedirect(request.getContextPath() + "/cambiar_password");
                return;
            }

    
            if (!SeguridadUtil.verificarPassword(passwordActual, usuarioBD.getPassword())) {
                request.getSession().setAttribute("mensajeError", "La contraseña actual ingresada es incorrecta.");
                response.sendRedirect(request.getContextPath() + "/cambiar_password");
                return;
            }

     
            String nuevoHash = SeguridadUtil.hashPassword(passwordNueva);
            dao.cambiarSoloPassword(usuarioBD.getIdUsuario(), nuevoHash);

            request.getSession().setAttribute("mensajeExito", "✅ Contraseña actualizada correctamente.");
            response.sendRedirect(request.getContextPath() + "/cambiar_password");

        } catch (Exception e) {
            request.getSession().setAttribute("mensajeError", "Error al cambiar la contraseña: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cambiar_password");
        }
    }
}
