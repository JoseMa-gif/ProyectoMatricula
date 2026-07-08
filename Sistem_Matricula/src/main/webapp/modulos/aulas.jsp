<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Aulas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
</head>
<body>
    <header class="cabecera">
        <div class="cabecera-info">
            <span>Módulo Académico - Aulas</span>
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

        <c:set var="pAulas" value="${null}" />
        <c:forEach var="p" items="${sessionScope.permisos}">
            <c:if test="${p.nombreFuncionalidad == 'Aulas'}">
                <c:set var="pAulas" value="${p}" />
            </c:if>
        </c:forEach>
        
        <c:set var="tieneAcciones" value="${pAulas.editar or pAulas.eliminar}" />

        <c:if test="${pAulas.crear}">
            <div class="card">
                <h3 id="form-titulo" style="margin-bottom: 1rem;">Crear Nueva Aula</h3>
                <form action="${pageContext.request.contextPath}/aulas" method="post">
                    <input type="hidden" name="accion" id="accion" value="crear">
                    <input type="hidden" name="codAula" id="form-codAula" value="">
                    <input type="hidden" name="version" id="form-version" value="">
                    
                    <div class="form-grid">
                        <div class="form-group">
                            <label for="codAnioAcademico">Año Académico:</label>
                            <select id="codAnioAcademico" name="codAnioAcademico" class="form-control" required>
                                <c:forEach var="anio" items="${anios}">
                                    <option value="${anio.codAnioAcademico}">${anio.anio}</option>
                                </c:forEach>
                            </select>
                        </div>
    
                        <div class="form-group">
                            <label for="codNivel">Nivel:</label>
                            <select id="codNivel" name="codNivel" class="form-control" required>
                                <c:forEach var="nivel" items="${niveles}">
                                    <option value="${nivel.codNivel}">${nivel.nombre}</option>
                                </c:forEach>
                            </select>
                        </div>
    
                        <div class="form-group">
                            <label for="codGrado">Grado:</label>
                            <select id="codGrado" name="codGrado" class="form-control" required>
                                <c:forEach var="grado" items="${grados}">
                                    <option value="${grado.codGrado}" data-nivel="${grado.codNivel}">${grado.nombre}</option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <div class="form-group">
                            <label for="seccion">Sección:</label>
                            <input type="text" id="seccion" name="seccion" maxlength="2" class="form-control" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="capacidadMaxima">Capacidad Máxima:</label>
                            <input type="number" id="capacidadMaxima" name="capacidadMaxima" class="form-control" required>
                        </div>
    
                        <div class="form-group">
                            <label for="estado">Estado:</label>
                            <select id="estado" name="estado" class="form-control">
                                <option value="1">Activa</option>
                                <option value="0">Inactiva</option>
                            </select>
                        </div>
                    </div>
                    
                    <div style="margin-top: 1.5rem;">
                        <button type="submit" id="btn-guardar" class="btn btn-primary">Guardar Aula</button>
                        <button type="button" id="btn-cancelar" class="btn btn-secondary" style="display:none;" onclick="cancelarEdicion()">Cancelar</button>
                    </div>
                </form>
            </div>
        </c:if>

        <div class="card">
            <div class="page-header-actions">
                <h2 class="page-title" style="margin:0;">Listado de Aulas</h2>
            </div>
            <div class="table-container">
                <table>
            <thead>
                <tr>
                    <th>Año</th>
                    <th>Nivel</th>
                    <th>Grado</th>
                    <th>Sección</th>
                    <th>Capacidad</th>
                    <th>Versión</th>
                    <th>Estado</th>
                    <c:if test="${tieneAcciones}"><th>Acciones</th></c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="a" items="${aulas}">
                    <tr style="${!a.estado ? 'background-color: #ffe6e6; color: #999;' : ''}">
                        <td>${a.nombreAnio}</td>
                        <td>${a.nombreNivel}</td>
                        <td>${a.nombreGrado}</td>
                        <td>${a.seccion}</td>
                        <td>${a.capacidadMaxima}</td>
                        <td>v${a.version}</td>
                        <td>
                            <c:choose>
                                <c:when test="${a.estado}"><span class="badge badge-success">Activa</span></c:when>
                                <c:otherwise><span class="badge badge-danger">Inactiva</span></c:otherwise>
                            </c:choose>
                        </td>
                        <c:if test="${tieneAcciones}">
                        <td>
                            <div class="action-buttons">
                                <c:if test="${pAulas.editar}">
                                    <button class="btn btn-warning btn-sm" onclick="editarAula('${a.codAula}', '${a.codAnioAcademico}', '${a.codNivel}', '${a.codGrado}', '${a.seccion}', '${a.capacidadMaxima}', '${a.estado ? 1 : 0}', '${a.version}')">Editar</button>
                                </c:if>
                                <c:if test="${a.estado and pAulas.eliminar}">
                                    <form action="${pageContext.request.contextPath}/aulas" method="post" style="display:inline;">
                                        <input type="hidden" name="accion" value="eliminar">
                                        <input type="hidden" name="codAula" value="${a.codAula}">
                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('¿Estás seguro que deseas eliminar esta aula?');">Eliminar</button>
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
        function editarAula(codAula, codAnio, codNivel, codGrado, seccion, capacidad, estado, version) {
            document.getElementById('form-titulo').innerText = 'Editar Aula';
            document.getElementById('accion').value = 'editar';
            document.getElementById('form-codAula').value = codAula;
            document.getElementById('codAnioAcademico').value = codAnio;
            document.getElementById('codNivel').value = codNivel;
            document.getElementById('codGrado').value = codGrado;
            document.getElementById('seccion').value = seccion;
            document.getElementById('capacidadMaxima').value = capacidad;
            document.getElementById('estado').value = estado;
            document.getElementById('form-version').value = version;
            
            document.getElementById('btn-guardar').innerText = 'Actualizar Aula';
            document.getElementById('btn-cancelar').style.display = 'inline-block';
            window.scrollTo(0, 0);
        }

        function cancelarEdicion() {
            document.getElementById('form-titulo').innerText = 'Crear Nueva Aula';
            document.getElementById('accion').value = 'crear';
            document.getElementById('form-codAula').value = '';
            document.getElementById('seccion').value = '';
            document.getElementById('capacidadMaxima').value = '';
            document.getElementById('estado').value = '1';
            document.getElementById('form-version').value = '';
            
            document.getElementById('btn-guardar').innerText = 'Guardar Aula';
            document.getElementById('btn-cancelar').style.display = 'none';
        }

        // Lógica simple para filtrar grados por nivel
        document.getElementById('codNivel').addEventListener('change', function() {
            var nivelSeleccionado = this.value;
            var grados = document.getElementById('codGrado').options;
            for(var i=0; i < grados.length; i++) {
                if(grados[i].getAttribute('data-nivel') === nivelSeleccionado) {
                    grados[i].style.display = 'block';
                } else {
                    grados[i].style.display = 'none';
                }
            }
        });
    </script>
</body>
</html>
