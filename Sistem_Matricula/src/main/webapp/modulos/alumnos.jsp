<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Alumnos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
</head>
<body>
    <header class="cabecera">
        <div class="cabecera-info">
            <span>Módulo Académico - Alumnos</span>
        </div>
        <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn btn-secondary">Volver al Dashboard</a>
    </header>
    <main class="contenido">
        <c:if test="${not empty sessionScope.mensajeExito}">
            <div class="alert alert-success">${sessionScope.mensajeExito}</div>
            <c:remove var="mensajeExito" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.mensajeError}">
            <div class="alert alert-error">${sessionScope.mensajeError}</div>
            <c:remove var="mensajeError" scope="session" />
        </c:if>

        <c:set var="pAlumnos" value="${null}" />
        <c:forEach var="p" items="${sessionScope.permisos}">
            <c:if test="${p.nombreFuncionalidad == 'Alumnos'}">
                <c:set var="pAlumnos" value="${p}" />
            </c:if>
        </c:forEach>
        
        <c:set var="tieneAcciones" value="${pAlumnos.editar or pAlumnos.eliminar}" />

        <c:if test="${pAlumnos.crear}">
            <div class="card">
                <h3 id="form-titulo" style="margin-bottom: 1rem;">Registrar Nuevo Alumno</h3>
                <form action="${pageContext.request.contextPath}/alumnos" method="post">
                    <input type="hidden" name="accion" id="accion" value="crear">
                    <input type="hidden" name="codAlumno" id="form-codAlumno" value="">
                    
                    <div class="form-grid">
                        <div class="form-group">
                            <label for="codTipoDocumento">Tipo Doc:</label>
                            <select id="codTipoDocumento" name="codTipoDocumento" class="form-control" required>
                                <c:forEach var="td" items="${tiposDocumento}">
                                    <option value="${td.codTipoDocumento}">${td.nombre}</option>
                                </c:forEach>
                            </select>
                        </div>
    
                        <div class="form-group">
                            <label for="numeroDocumento">Número Documento:</label>
                            <input type="text" id="numeroDocumento" name="numeroDocumento" class="form-control" required> 
                            <small style="color:gray;">(Se guardará cifrado AES)</small>
                        </div>
                        
                        <div class="form-group">
                            <label for="nombres">Nombres:</label>
                            <input type="text" id="nombres" name="nombres" class="form-control" required>
                        </div>
    
                        <div class="form-group">
                            <label for="apellidoPaterno">Apellido Paterno:</label>
                            <input type="text" id="apellidoPaterno" name="apellidoPaterno" class="form-control" required>
                        </div>
    
                        <div class="form-group">
                            <label for="apellidoMaterno">Apellido Materno:</label>
                            <input type="text" id="apellidoMaterno" name="apellidoMaterno" class="form-control" required>
                        </div>
    
                        <div class="form-group">
                            <label for="fechaNacimiento">F. Nacimiento:</label>
                            <input type="date" id="fechaNacimiento" name="fechaNacimiento" class="form-control" required>
                            <small style="color:gray;">(Se guardará cifrada AES)</small>
                        </div>
    
                        <div class="form-group">
                            <label for="estado">Estado:</label>
                            <select id="estado" name="estado" class="form-control">
                                <option value="1">Activo</option>
                                <option value="0">Inactivo</option>
                            </select>
                        </div>
                    </div>
                    <div style="margin-top: 1.5rem;">
                        <button type="submit" id="btn-guardar" class="btn btn-primary">Guardar Alumno</button>
                        <button type="button" id="btn-cancelar" class="btn btn-secondary" style="display:none;" onclick="cancelarEdicion()">Cancelar</button>
                    </div>
                </form>
            </div>
        </c:if>

        <div class="card">
            <div class="page-header-actions">
                <h2 class="page-title" style="margin:0;">Listado de Alumnos</h2>
            </div>
            <div class="table-container">
                <table>
            <thead>
                <tr>
                    <th>Apellidos y Nombres</th>
                    <th>Tipo Doc</th>
                    <th>N° Documento</th>
                    <th>F. Nacimiento</th>
                    <th>Estado</th>
                    <c:if test="${tieneAcciones}"><th>Acciones</th></c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="a" items="${alumnos}">
                    <tr style="${!a.estado ? 'background-color: #ffe6e6; color: #999;' : ''}">
                        <td>${a.apellidoPaterno} ${a.apellidoMaterno}, ${a.nombres}</td>
                        <td>${a.nombreTipoDocumento}</td>
                        <td>${a.numeroDocumento}</td>
                        <td>${a.fechaNacimiento}</td>
                        <td>
                            <c:choose>
                                <c:when test="${a.estado}"><span class="badge badge-success">Activo</span></c:when>
                                <c:otherwise><span class="badge badge-danger">Inactivo</span></c:otherwise>
                            </c:choose>
                        </td>
                        <c:if test="${tieneAcciones}">
                        <td>
                            <div class="action-buttons">
                                <c:if test="${pAlumnos.editar}">
                                    <button class="btn btn-warning btn-sm" onclick="editarAlumno('${a.codAlumno}', '${a.codTipoDocumento}', '${a.numeroDocumento}', '${a.nombres}', '${a.apellidoPaterno}', '${a.apellidoMaterno}', '${a.fechaNacimiento}', '${a.estado ? 1 : 0}')">Editar</button>
                                </c:if>
                                <c:if test="${a.estado and pAlumnos.eliminar}">
                                    <form action="${pageContext.request.contextPath}/alumnos" method="post" style="display:inline;">
                                        <input type="hidden" name="accion" value="eliminar">
                                        <input type="hidden" name="codAlumno" value="${a.codAlumno}">
                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('¿Estás seguro que deseas eliminar este alumno?');">Eliminar</button>
                                    </form>
                                </c:if>
                            </div>
                        </td>
                        </c:if>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        </div>
        </div>
    </main>

    <script>
        function editarAlumno(codAlumno, codTipoDocumento, numeroDocumento, nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, estado) {
            document.getElementById('form-titulo').innerText = 'Editar Alumno';
            document.getElementById('accion').value = 'editar';
            document.getElementById('form-codAlumno').value = codAlumno;
            document.getElementById('codTipoDocumento').value = codTipoDocumento;
            document.getElementById('numeroDocumento').value = numeroDocumento;
            document.getElementById('nombres').value = nombres;
            document.getElementById('apellidoPaterno').value = apellidoPaterno;
            document.getElementById('apellidoMaterno').value = apellidoMaterno;
            document.getElementById('fechaNacimiento').value = fechaNacimiento;
            document.getElementById('estado').value = estado;
            
            document.getElementById('btn-guardar').innerText = 'Actualizar Alumno';
            document.getElementById('btn-cancelar').style.display = 'inline-block';
            window.scrollTo(0, 0);
        }

        function cancelarEdicion() {
            document.getElementById('form-titulo').innerText = 'Registrar Nuevo Alumno';
            document.getElementById('accion').value = 'crear';
            document.getElementById('form-codAlumno').value = '';
            document.getElementById('numeroDocumento').value = '';
            document.getElementById('nombres').value = '';
            document.getElementById('apellidoPaterno').value = '';
            document.getElementById('apellidoMaterno').value = '';
            document.getElementById('fechaNacimiento').value = '';
            document.getElementById('estado').value = '1';
            
            document.getElementById('btn-guardar').innerText = 'Guardar Alumno';
            document.getElementById('btn-cancelar').style.display = 'none';
        }
    </script>
</body>
</html>
