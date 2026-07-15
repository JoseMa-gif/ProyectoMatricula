package com.matricula.servlet;

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

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario != null) {
                try (Connection con = ConexionBD.getConexion()) {
                    AuditoriaUtil.registrar(con, "Seguridad",
                            "usuario", "LOGOUT", usuario.getIdUsuario(), null, null);
                } catch (Exception e) {
 
                }
            }
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
