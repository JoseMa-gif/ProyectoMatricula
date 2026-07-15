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

 
@WebFilter(urlPatterns = "/*")
public class AuditoriaFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            Auditoria auditoria = new Auditoria();
            
  
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            auditoria.setIpOrigen(ip);
            
   
            String userAgent = request.getHeader("User-Agent");
            if (userAgent != null && userAgent.length() > 150) {
                userAgent = userAgent.substring(0, 150);  
            }
            auditoria.setNavegador(userAgent);
            
    
            auditoria.setEquipo(request.getRemoteHost());
            
             
            HttpSession session = request.getSession(false);
            if (session != null) {
                Usuario u = (Usuario) session.getAttribute("usuario");
                if (u != null) {
                    auditoria.setCodUsuario(u.getIdUsuario());
                    auditoria.setNombreUsuario(u.getUsuario());
                }
            }
            
             
            AuditoriaContext.setAuditoria(auditoria);
            
            chain.doFilter(request, response);
            
        } finally {
             
            AuditoriaContext.clear();
        }
    }
}

