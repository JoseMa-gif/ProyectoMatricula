<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Proceso de Matrícula</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
</head>
<body>
    <header class="cabecera">
        <div class="cabecera-info">
            <span>Módulo Académico - Matrícula (Doble Factor)</span>
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

        <c:set var="pMatricula" value="${null}" />
        <c:forEach var="p" items="${sessionScope.permisos}">
            <c:if test="${p.nombreFuncionalidad == 'Matrícula'}">
                <c:set var="pMatricula" value="${p}" />
            </c:if>
        </c:forEach>
        
        <c:set var="tieneAcciones" value="${pMatricula.eliminar}" />

        <c:if test="${pMatricula.crear}">
            <div class="card">
                <h3 id="form-titulo" style="margin-bottom: 1rem;">Registrar Nueva Matrícula (Requiere Validación)</h3>
                <form action="${pageContext.request.contextPath}/matricula" method="post" id="formMatricula">
                    <input type="hidden" name="accion" id="accion" value="crear">
                    
                    <div class="form-grid" style="align-items: center;">
                        <div class="form-group">
                            <label for="codAnioAcademico">Año Académico:</label>
                            <select id="codAnioAcademico" name="codAnioAcademico" class="form-control" required>
                                <c:forEach var="anio" items="${anios}">
                                    <option value="${anio.codAnioAcademico}">${anio.anio}</option>
                                </c:forEach>
                            </select>
                        </div>
    
                        <div class="form-group">
                            <label for="nombreAlumnoStr">Alumno:</label>
                            <div style="display:flex; gap:10px;">
                                <input type="text" id="nombreAlumnoStr" class="form-control" readonly required placeholder="Seleccione un alumno...">
                                <input type="hidden" id="codAlumno" name="codAlumno" required>
                                <button type="button" class="btn btn-info" onclick="abrirModal('modalAlumno')">Buscar Alumno</button>
                            </div>
                        </div>
    
                        <div class="form-group">
                            <label for="nombreAulaStr">Aula Destino:</label>
                            <div style="display:flex; gap:10px;">
                                <input type="text" id="nombreAulaStr" class="form-control" readonly required placeholder="Seleccione un aula...">
                                <input type="hidden" id="codAula" name="codAula" required>
                                <button type="button" class="btn btn-info" onclick="abrirModal('modalAula')">Buscar Aula</button>
                            </div>
                        </div>
                    </div>
                    
                    <div class="auth-container">
                        <c:choose>
                            <c:when test="${not empty nuevoSecret2FA}">
                                <h4>🔒 Configuración Inicial de Doble Factor Obligatoria</h4>
                                <p>Por políticas de seguridad, para matricular alumnos debe usar Google Authenticator.</p>
                                <p><strong>Paso 1:</strong> Escanee este código QR con su aplicación móvil.</p>
                                <img src="${qrUrl2FA}" alt="QR de Google Authenticator"><br><br>
                                <p><strong>Paso 2:</strong> Ingrese el código de 6 dígitos que aparece en su app.</p>
                                <input type="hidden" name="nuevoSecret2FA" value="${nuevoSecret2FA}">
                            </c:when>
                            <c:otherwise>
                                <h4>🔒 Autorización de Seguridad (2FA)</h4>
                                <p>Ingrese el código de 6 dígitos de su aplicación Google Authenticator.</p>
                            </c:otherwise>
                        </c:choose>
                        
                        <label for="token2FA">Token (6 dígitos):</label>
                        <input type="text" id="token2FA" name="token2FA" class="auth-input" maxlength="6" pattern="\d{6}" required autocomplete="off">
                    </div>

                    <button type="submit" id="btn-guardar" class="btn btn-primary" onclick="return confirm('¿Está seguro de autorizar esta matrícula? Las cuotas se generarán automáticamente.');">Registrar Matrícula Segura</button>
                </form>
            </div>
        </c:if>

        <div class="card">
            <div class="page-header-actions">
                <h2 class="page-title" style="margin:0;">Listado de Matrículas</h2>
                <div>
                    <a href="${pageContext.request.contextPath}/reporte_matriculas" target="_blank" class="btn btn-success" style="margin-right: 10px;">
                        📊 Excel
                    </a>
                    <a href="${pageContext.request.contextPath}/reporte_matriculas_pdf" target="_blank" class="btn btn-danger">
                        📄 PDF
                    </a>
                </div>
            </div>
            
            <div class="table-container">
                <table>
            <thead>
                <tr>
                    <th>Cod.</th>
                    <th>Año</th>
                    <th>Alumno</th>
                    <th>Aula</th>
                    <th>Fecha Matrícula</th>
                    <th>Versión</th>
                    <th>Estado</th>
                    <c:if test="${tieneAcciones}"><th>Acciones</th></c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="m" items="${matriculas}">
                    <tr style="${!m.estado ? 'background-color: #ffe6e6; color: #999;' : ''}">
                        <td>${m.codMatricula}</td>
                        <td>${m.nombreAnio}</td>
                        <td>${m.nombreAlumno}</td>
                        <td>${m.descripcionAula}</td>
                        <td>${m.fechaMatricula}</td>
                        <td>v${m.version}</td>
                        <td>
                            <c:choose>
                                <c:when test="${m.estado}"><span style="color: green; font-weight: bold;">Activa</span></c:when>
                                <c:otherwise><span style="color: red; font-weight: bold;">Anulada</span></c:otherwise>
                            </c:choose>
                        </td>
                        <c:if test="${tieneAcciones}">
                        <td>
                            <div class="action-buttons">
                                <c:if test="${m.estado and pMatricula.eliminar}">
                                    <form action="${pageContext.request.contextPath}/matricula" method="post" style="display:inline;">
                                        <input type="hidden" name="accion" value="anular">
                                        <input type="hidden" name="codMatricula" value="${m.codMatricula}">
                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('¿Estás seguro que deseas anular esta matrícula? (Las cuotas podrían quedar huérfanas)');">Anular</button>
                                    </form>
                                </c:if>
                                <c:if test="${not m.estado and pMatricula.editar}">
                                    <form action="${pageContext.request.contextPath}/matricula" method="post" style="display:inline;">
                                        <input type="hidden" name="accion" value="restaurar">
                                        <input type="hidden" name="codMatricula" value="${m.codMatricula}">
                                        <button type="submit" class="btn btn-warning btn-sm" onclick="return confirm('¿Deseas restaurar esta matrícula a estado Activo?');">Restaurar</button>
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

 
    <div id="modalAlumno" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Seleccionar Alumno</h2>
                <span class="close" onclick="cerrarModal('modalAlumno')">&times;</span>
            </div>
            <div class="modal-body">
                <input type="text" id="filtroAlumno" class="form-control" onkeyup="filtrarTabla('filtroAlumno', 'tablaAlumno')" placeholder="Buscar por DNI o Apellidos..." style="margin-bottom: 15px;">
                <div class="table-container">
                    <table id="tablaAlumno">
                <thead>
                    <tr>
                        <th>N° Documento</th>
                        <th>Apellidos y Nombres</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="alu" items="${alumnos}">
                        <tr>
                            <td>${alu.numeroDocumento}</td>
                            <td>${alu.apellidoPaterno} ${alu.apellidoMaterno}, ${alu.nombres}</td>
                            <td>
                                <button type="button" class="btn btn-success btn-sm" onclick="seleccionarAlumno('${alu.codAlumno}', '${alu.apellidoPaterno} ${alu.apellidoMaterno}, ${alu.nombres}')">Seleccionar</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            </div>
            </div>
        </div>
    </div>

  
    <div id="modalAula" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Seleccionar Aula Destino</h2>
                <span class="close" onclick="cerrarModal('modalAula')">&times;</span>
            </div>
            <div class="modal-body">
                <input type="text" id="filtroAula" class="form-control" onkeyup="filtrarTabla('filtroAula', 'tablaAula')" placeholder="Buscar por Grado, Nivel o Sección..." style="margin-bottom: 15px;">
                <div class="table-container">
                    <table id="tablaAula">
                <thead>
                    <tr>
                        <th>Nivel</th>
                        <th>Grado</th>
                        <th>Sección</th>
                        <th>Alumnos</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="au" items="${aulas}">
                        <tr>
                            <td>${au.nombreNivel}</td>
                            <td>${au.nombreGrado}</td>
                            <td>${au.seccion}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${au.matriculados >= au.capacidadMaxima}">
                                        <span style="color:red; font-weight:bold;">${au.matriculados} / ${au.capacidadMaxima} (Llena)</span>
                                    </c:when>
                                    <c:otherwise>
                                        ${au.matriculados} / ${au.capacidadMaxima}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${au.matriculados >= au.capacidadMaxima}">
                                        <button type="button" class="btn btn-secondary btn-sm" disabled>Llena</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button" class="btn btn-success btn-sm" onclick="seleccionarAula('${au.codAula}', '${au.nombreNivel} ${au.nombreGrado} &quot;${au.seccion}&quot;')">Seleccionar</button>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            </div>
            </div>
        </div>
    </div>

    <script>
        function abrirModal(id) {
            document.getElementById(id).style.display = "block";
        }

        function cerrarModal(id) {
            document.getElementById(id).style.display = "none";
        }

        function seleccionarAlumno(cod, nombre) {
            document.getElementById('codAlumno').value = cod;
            document.getElementById('nombreAlumnoStr').value = nombre;
            cerrarModal('modalAlumno');
        }

        function seleccionarAula(cod, nombre) {
            document.getElementById('codAula').value = cod;
            document.getElementById('nombreAulaStr').value = nombre;
            cerrarModal('modalAula');
        }

        function filtrarTabla(inputId, tablaId) {
            var input, filter, table, tr, td, i, j, txtValue;
            input = document.getElementById(inputId);
            filter = input.value.toUpperCase();
            table = document.getElementById(tablaId);
            tr = table.getElementsByTagName("tr");
            
            for (i = 1; i < tr.length; i++) {
                tr[i].style.display = "none";
                td = tr[i].getElementsByTagName("td");
                for (j = 0; j < td.length; j++) {
                    if (td[j]) {
                        txtValue = td[j].textContent || td[j].innerText;
                        if (txtValue.toUpperCase().indexOf(filter) > -1) {
                            tr[i].style.display = "";
                            break;
                        }
                    }
                }
            }
        }
    </script>
</body>
</html>
