package com.matricula.filter;

import com.matricula.model.Auditoria;
import com.matricula.model.Usuario;
import com.matricula.util.AuditoriaContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro que captura los datos del usuario (IP, equipo, navegador, ID)
 * y los almacena en el ThreadLocal para que los DAOs puedan registrar la auditoría.
 */
@WebFilter(urlPatterns = "/*")
public class AuditoriaFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            Auditoria auditoria = new Auditoria();
            
            // 1. Capturar IP
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            auditoria.setIpOrigen(ip);
            
            // 2. Capturar Navegador (User-Agent)
            String userAgent = request.getHeader("User-Agent");
            if (userAgent != null && userAgent.length() > 150) {
                userAgent = userAgent.substring(0, 150); // truncar si es muy largo
            }
            auditoria.setNavegador(userAgent);
            
            // 3. Equipo (opcional, difícil de obtener real desde web, podemos usar Hostname del cliente o IP)
            auditoria.setEquipo(request.getRemoteHost());
            
            // 4. Capturar Usuario si está logueado
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario u = (Usuario) session.getAttribute("usuario");
                if (u != null) {
                    auditoria.setCodUsuario(u.getIdUsuario());
                    auditoria.setNombreUsuario(u.getUsuario());
                }
            }
            
            // Almacenar en el ThreadLocal
            AuditoriaContext.setAuditoria(auditoria);
            
            chain.doFilter(request, response);
            
        } finally {
            // Limpiar siempre al terminar la petición para evitar fugas de memoria
            AuditoriaContext.clear();
        }
    }
}

