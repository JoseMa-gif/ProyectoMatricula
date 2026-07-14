<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.matricula.model.Usuario" %>
<%@ page import="com.matricula.model.RolFuncionalidad" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel Principal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
</head>
<body>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
%>
<div class="dashboard-layout">
    <!-- Menú Lateral -->
    <nav class="menu-lateral">
        <div class="menu-lateral-header">
            <span>📚 App Matrícula</span>
        </div>
        <nav>
        <%
            List<RolFuncionalidad> permisos = (List<RolFuncionalidad>) session.getAttribute("permisos");
            if (permisos != null) {
                for (RolFuncionalidad p : permisos) {
                    if (p.isVer() && p.getPadreFuncionalidad() == null) {
                        boolean hasChildren = false;
                        for (RolFuncionalidad checkHijo : permisos) {
                            if (checkHijo.getPadreFuncionalidad() != null && checkHijo.getPadreFuncionalidad() == p.getIdFuncionalidad()) {
                                hasChildren = true;
                                break;
                            }
                        }
                        
                        if (hasChildren) {
                            out.print("<div class='menu-grupo'><strong>" + p.getNombreFuncionalidad() + "</strong></div>");
                            for (RolFuncionalidad hijo : permisos) {
                                if (hijo.isVer() && hijo.getPadreFuncionalidad() != null && hijo.getPadreFuncionalidad() == p.getIdFuncionalidad()) {
                                    String nombreModulo = java.text.Normalizer.normalize(hijo.getNombreFuncionalidad().toLowerCase(), java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                                    String link = nombreModulo;
                                    out.print("<a href='" + request.getContextPath() + "/" + link + "'>&nbsp;&nbsp;" + hijo.getNombreFuncionalidad() + "</a>");
                                }
                            }
                        } else {
                            String nombreModulo = java.text.Normalizer.normalize(p.getNombreFuncionalidad().toLowerCase(), java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                            String link = nombreModulo;
                            out.print("<a href='" + request.getContextPath() + "/" + link + "'><strong>" + p.getNombreFuncionalidad() + "</strong></a>");
                        }
                    }
                }
            }
        %>
        </nav>
    </nav>

    
    <div class="main-wrapper">
        <header class="cabecera">
            <div class="cabecera-info">
                <span>Bienvenido, <%= usuario.getUsuario() %> (<%= usuario.getNombreRol() %>)</span>
            </div>
            <div style="display:flex; gap:0.75rem; align-items:center;">
                <a href="${pageContext.request.contextPath}/cambiar_password" class="btn-logout" style="background:#EEF2FF; color:var(--primary);">🔑 Cambiar Contraseña</a>
                <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Cerrar sesión</a>
            </div>
        </header>

        <main class="contenido">
            
            <div class="dashboard-hero">
                <h1 style="margin-bottom: 0.5rem; font-size: 2.2rem;">¡Hola, <%= usuario.getUsuario() %>! 👋</h1>
                <p style="color: #E0E7FF; font-size: 1.1rem; max-width: 600px;">
                    Bienvenido al Panel Principal de App Matrícula. Selecciona una de las opciones rápidas a continuación o navega por el menú lateral para comenzar.
                </p>
            </div>

            
            <h2 class="page-title" style="font-size: 1.25rem; margin-bottom: 1rem;">Accesos Rápidos a Módulos</h2>
            <div class="quick-links-grid">
                <%
                    if (permisos != null) {
                        
                        for (RolFuncionalidad p : permisos) {
                            if (p.isVer() && p.getPadreFuncionalidad() != null) {
                                String nombreModulo = java.text.Normalizer.normalize(p.getNombreFuncionalidad().toLowerCase(), java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                                String link = request.getContextPath() + "/" + nombreModulo;
                                
                                String icono = "📁";
                                String desc = "Gestionar " + p.getNombreFuncionalidad().toLowerCase();
                                if (p.getNombreFuncionalidad().equalsIgnoreCase("Usuarios")) { icono = "👥"; desc = "Cuentas y contraseñas"; }
                                else if (p.getNombreFuncionalidad().equalsIgnoreCase("Roles")) { icono = "🛡️"; desc = "Configuración de roles"; }
                                else if (p.getNombreFuncionalidad().equalsIgnoreCase("Permisos")) { icono = "🔐"; desc = "Accesos del sistema"; }
                                else if (p.getNombreFuncionalidad().equalsIgnoreCase("Matrícula")) { icono = "📝"; desc = "Proceso de admisión"; }
                                else if (p.getNombreFuncionalidad().equalsIgnoreCase("Aulas")) { icono = "🏫"; desc = "Aulas y secciones"; }
                                else if (p.getNombreFuncionalidad().equalsIgnoreCase("Alumnos")) { icono = "👨‍🎓"; desc = "Fichas de alumnos"; }
                                else if (p.getNombreFuncionalidad().equalsIgnoreCase("Conceptos")) { icono = "💡"; desc = "Conceptos de pago"; }
                %>
                                <a href="<%= link %>" class="quick-card">
                                    <span class="quick-icon"><%= icono %></span>
                                    <span class="quick-title"><%= p.getNombreFuncionalidad() %></span>
                                    <span style="color: var(--text-muted); font-size: 0.85rem; margin-top: 0.5rem;"><%= desc %></span>
                                </a>
                <%
                            }
                        }

                    
                        for (RolFuncionalidad p : permisos) {
                            if (p.isVer() && p.getPadreFuncionalidad() == null) {
                                boolean tieneHijos = false;
                                for (RolFuncionalidad checkHijo : permisos) {
                                    if (checkHijo.getPadreFuncionalidad() != null && checkHijo.getPadreFuncionalidad() == p.getIdFuncionalidad()) {
                                        tieneHijos = true;
                                        break;
                                    }
                                }
                                
                                if (!tieneHijos) {
                                    String nombreModulo = java.text.Normalizer.normalize(p.getNombreFuncionalidad().toLowerCase(), java.text.Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                                    String link = request.getContextPath() + "/" + nombreModulo;
                                    
                                    String icono = "📁";
                                    String desc = "Gestionar " + p.getNombreFuncionalidad().toLowerCase();
                                    if (p.getNombreFuncionalidad().equalsIgnoreCase("Pagos")) { icono = "💰"; desc = "Caja y cobros"; }
                                    else if (p.getNombreFuncionalidad().equalsIgnoreCase("Reportes")) { icono = "📊"; desc = "Informes académicos"; }
                                    else if (p.getNombreFuncionalidad().equalsIgnoreCase("Auditoría")) { icono = "👁️"; desc = "Logs del sistema"; }
                %>
                                    <a href="<%= link %>" class="quick-card">
                                        <span class="quick-icon"><%= icono %></span>
                                        <span class="quick-title"><%= p.getNombreFuncionalidad() %></span>
                                        <span style="color: var(--text-muted); font-size: 0.85rem; margin-top: 0.5rem;"><%= desc %></span>
                                    </a>
                <%
                                }
                            }
                        }
                    } else {
                %>
                        <div class="card" style="grid-column: 1 / -1;">
                            <p style="color: var(--text-muted);">No tienes módulos asignados. Contacta al administrador.</p>
                        </div>
                <%
                    }
                %>
            </div>
        </main>
    </div>
</div>
</body>
</html>
