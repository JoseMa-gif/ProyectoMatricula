<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Edición de Permisos</title>
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
            <span>Asignación de Permisos</span>
        </div>
        <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn btn-secondary">Volver al Dashboard</a>
    </header>
    <main class="contenido" style="display: flex; flex-wrap: wrap; gap: 30px; align-items: flex-start;">
        <div class="card" style="flex: 1 1 350px;">
            <div class="page-header-actions">
                <h2 class="page-title" style="margin:0;">Seleccionar Rol</h2>
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
                            <td><a href="permisos?idRol=${r.idRol}" class="btn btn-warning btn-sm">Editar Permisos</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            </div>
        </div>

        <c:set var="pPermisos" value="${null}" />
        <c:forEach var="p" items="${sessionScope.permisos}">
            <c:if test="${p.nombreFuncionalidad == 'Permisos'}">
                <c:set var="pPermisos" value="${p}" />
            </c:if>
        </c:forEach>

        <div class="card" style="flex: 2 1 600px;">
            <h2 class="page-title" style="margin-top:0;">Permisos 
                <c:choose>
                    <c:when test="${pPermisos.editar}"><span class="badge badge-success">Editable</span></c:when>
                    <c:otherwise><span class="badge badge-danger">Solo lectura</span></c:otherwise>
                </c:choose>
            </h2>
            
            <c:if test="${not empty sessionScope.mensajeExito}">
                <div class="alert alert-success">${sessionScope.mensajeExito}</div>
                <c:remove var="mensajeExito" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.mensajeError}">
                <div class="alert alert-error">${sessionScope.mensajeError}</div>
                <c:remove var="mensajeError" scope="session" />
            </c:if>

            <c:if test="${not empty rolSeleccionado}">
                <c:if test="${pPermisos.editar}">
                <form action="${pageContext.request.contextPath}/guardar_permisos" method="post">
                    <input type="hidden" name="idRol" value="${rolSeleccionado}">
                </c:if>
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
                                                    <label class="permiso-chk"><input type="checkbox" name="ver_${hijo.idFuncionalidad}" ${hijo.ver ? 'checked' : ''} ${!pPermisos.editar ? 'disabled' : ''}> Ver</label>
                                                    <label class="permiso-chk"><input type="checkbox" name="crear_${hijo.idFuncionalidad}" ${hijo.crear ? 'checked' : ''} ${!pPermisos.editar ? 'disabled' : ''}> Crear</label>
                                                    <label class="permiso-chk"><input type="checkbox" name="editar_${hijo.idFuncionalidad}" ${hijo.editar ? 'checked' : ''} ${!pPermisos.editar ? 'disabled' : ''}> Editar</label>
                                                    <label class="permiso-chk"><input type="checkbox" name="eliminar_${hijo.idFuncionalidad}" ${hijo.eliminar ? 'checked' : ''} ${!pPermisos.editar ? 'disabled' : ''}> Eliminar</label>
                                                </li>
                                            </c:if>
                                        </c:forEach>
                                    </ul>
                                </li>
                            </c:if>
                        </c:forEach>
                    </ul>
                <c:if test="${pPermisos.editar}">
                    <div style="margin-top: 1.5rem;">
                        <button type="submit" class="btn btn-primary">Aplicar Permisos</button>
                    </div>
                </form>
                </c:if>
            </c:if>
            <c:if test="${empty rolSeleccionado}">
                <p style="color: #6B7280; font-style: italic;">Selecciona un rol de la tabla para editar sus permisos.</p>
            </c:if>
        </div>
    </main>
</body>
</html>
