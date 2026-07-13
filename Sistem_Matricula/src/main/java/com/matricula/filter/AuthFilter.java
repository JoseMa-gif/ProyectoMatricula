package com.matricula.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Verifica que exista una sesión activa antes de permitir el acceso a
 * cualquier página protegida. Las páginas de login y los recursos
 * estáticos quedan excluidos mediante el urlPatterns.
 */
@WebFilter(urlPatterns = {"/dashboard.jsp", "/modulos/*", "/cambiar_password"})
public class AuthFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        boolean sesionValida = session != null && session.getAttribute("usuario") != null;

        if (sesionValida) {
            chain.doFilter(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
