package com.matricula.servlet;

import com.matricula.dao.UsuarioDAO;
import com.matricula.model.Usuario;
import com.matricula.util.AuditoriaUtil;
import com.matricula.util.ConexionBD;
import com.matricula.util.SeguridadUtil;
import com.matricula.dao.RolFuncionalidadDAO;
import com.matricula.model.RolFuncionalidad;
import com.matricula.util.AuditoriaContext;
import com.matricula.model.Auditoria;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuarioIngresado = request.getParameter("usuario");
        String passwordIngresado = request.getParameter("password");
        String ip = request.getRemoteAddr();

        try {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuario = dao.buscarPorUsuario(usuarioIngresado);

            boolean loginValido = usuario != null
                    && usuario.isEstado()
                    && SeguridadUtil.verificarPassword(passwordIngresado, usuario.getPassword());

            try (Connection con = ConexionBD.getConexion()) {
                if (loginValido) {
 
                    HttpSession session = request.getSession(true);
                    session.setAttribute("usuario", usuario);

                    RolFuncionalidadDAO rfDao = new RolFuncionalidadDAO();
                    List<RolFuncionalidad> permisos = rfDao.listarPorRol(usuario.getIdRol());
                    session.setAttribute("permisos", permisos);

                    session.setMaxInactiveInterval(30 * 60); 
                    
                     
                    Auditoria ctx = AuditoriaContext.getAuditoria();
                    if (ctx != null) {
                        ctx.setCodUsuario(usuario.getIdUsuario());
                        ctx.setNombreUsuario(usuario.getUsuario());
                    }

                    AuditoriaUtil.registrar(con, "Seguridad",
                            "usuario", "LOGIN", usuario.getIdUsuario(), null, null);

                    response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
                } else {
                    Auditoria ctx = AuditoriaContext.getAuditoria();
                    if (ctx != null && usuario != null) {
                        ctx.setCodUsuario(usuario.getIdUsuario());
                    }
                    AuditoriaUtil.registrar(con, "Seguridad", "usuario", "LOGIN_FALLIDO", 
                            usuario != null ? usuario.getIdUsuario() : null, null, null);

                    request.setAttribute("error", "Usuario o contraseña incorrectos.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
            }
        } catch (Exception e) {
            throw new ServletException("Error al procesar el inicio de sesión", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
