<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Roles y Permisos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
    <style>
        .tree-js { list-style-type: none; padding-left: 0; }
        .tree-js li { margin: 12px 0; }
        .tree-js ul { margin-left: 20px; border-left: 1px dashed #ccc; padding-left: 15px; margin-top: 10px; }
        .permiso-chk { margin-left: 12px; font-size: 0.95em; color: #4B5563; }
    </style>
</head>
<body>
    <header class="cabecera">
        <div class="cabecera-info">
            <span>Gestión de Roles y Permisos</span>
        </div>
        <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn btn-secondary">Volver al Dashboard</a>
    </header>
    <main class="contenido" style="display: flex; flex-wrap: wrap; gap: 30px; align-items: flex-start;">
        <div class="card" style="flex: 1 1 350px;">
            <div class="page-header-actions">
                <h2 class="page-title" style="margin:0;">Roles</h2>
            </div>
            <div class="table-container">
                <table>
                <thead>
                    <tr><th>ID</th><th>Nombre Rol</th><th>Acción</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${roles}">
                        <tr>
                            <td>${r.idRol}</td>
                            <td>${r.nombreRol}</td>
                            <td><a href="roles?idRol=${r.idRol}" class="btn btn-info btn-sm">Ver Permisos</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            </div>
        </div>

        <div class="card" style="flex: 2 1 600px;">
            <h2 class="page-title" style="margin-top:0;">Permisos (Tree JavaScript)</h2>
            <c:if test="${not empty rolSeleccionado}">
                <ul class="tree-js">
                    <c:forEach var="p" items="${permisosRol}">
                        <c:if test="${empty p.padreFuncionalidad}">
                            <li>
                                <strong>${p.nombreFuncionalidad}</strong>
                                <ul>
                                    <c:forEach var="hijo" items="${permisosRol}">
                                        <c:if test="${hijo.padreFuncionalidad == p.idFuncionalidad}">
                                            <li>
                                                ${hijo.nombreFuncionalidad}
                                                <label class="permiso-chk"><input type="checkbox" disabled ${hijo.ver ? 'checked' : ''}> Ver</label>
                                                <label class="permiso-chk"><input type="checkbox" disabled ${hijo.crear ? 'checked' : ''}> Crear</label>
                                                <label class="permiso-chk"><input type="checkbox" disabled ${hijo.editar ? 'checked' : ''}> Editar</label>
                                                <label class="permiso-chk"><input type="checkbox" disabled ${hijo.eliminar ? 'checked' : ''}> Eliminar</label>
                                            </li>
                                        </c:if>
                                    </c:forEach>
                                </ul>
                            </li>
                        </c:if>
                    </c:forEach>
                </ul>
            </c:if>
            <c:if test="${empty rolSeleccionado}">
                <p style="color: #6B7280; font-style: italic;">Selecciona un rol de la tabla para ver sus permisos.</p>
            </c:if>
        </div>
    </main>
</body>
</html>
