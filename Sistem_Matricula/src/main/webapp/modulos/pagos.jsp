<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Módulo de Pagos y Caja</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
</head>
<body>
    <header class="cabecera">
        <div class="cabecera-info">
            <span>Módulo Financiero - Caja</span>
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

        <c:set var="pPagos" value="${null}" />
        <c:forEach var="p" items="${sessionScope.permisos}">
            <c:if test="${p.nombreFuncionalidad == 'Pagos'}">
                <c:set var="pPagos" value="${p}" />
            </c:if>
        </c:forEach>
        
        <c:if test="${pPagos.ver}">
            <div class="card">
                <h3 style="margin-bottom: 1rem;">Buscar Alumno por Año (Estado de Cuenta)</h3>
                <form action="${pageContext.request.contextPath}/pagos" method="get">
                    
                    <div class="form-grid" style="align-items: center;">
                        <div class="form-group">
                            <label>Año Académico:</label>
                            <select name="codAnioAcademico" class="form-control" required>
                                <option value="">-- Seleccione Año --</option>
                                <c:forEach var="anio" items="${anios}">
                                    <option value="${anio.codAnioAcademico}" ${anioSeleccionado == anio.codAnioAcademico ? 'selected' : ''}>${anio.anio}</option>
                                </c:forEach>
                            </select>
                        </div>
    
                        <div class="form-group">
                            <label>Alumno:</label>
                            <div style="display:flex; gap:10px;">
                                <input type="text" id="nombreAlumnoStr" class="form-control" readonly required placeholder="Seleccione un alumno..." 
                                       value="${matriculaEncontrada != null ? matriculaEncontrada.nombreAlumno : ''}">
                                <input type="hidden" id="codAlumno" name="codAlumno" value="${alumnoSeleccionado}" required>
                                <button type="button" class="btn btn-info" onclick="abrirModal('modalAlumno')">Buscar Alumno</button>
                            </div>
                        </div>
                    </div>
                    <div style="margin-top: 1.5rem;">
                        <button type="submit" class="btn btn-primary" style="padding: 10px 20px; font-size: 16px;">Ver Estado de Cuenta</button>
                    </div>
                </form>
            </div>
        </c:if>

        <c:if test="${not empty cuotas}">
            <div class="card">
                <div class="page-header-actions">
                    <h2 class="page-title" style="margin:0;">Estado de Cuenta: ${matriculaEncontrada.nombreAlumno} (${matriculaEncontrada.nombreAnio})</h2>
                    <p style="margin:0; font-weight:bold; color: #4F46E5;">Aula: ${matriculaEncontrada.descripcionAula}</p>
                </div>
                <div class="table-container">
                    <table>
                <thead>
                    <tr>
                        <th>N° Orden</th>
                        <th>Concepto</th>
                        <th>Monto (S/)</th>
                        <th>Estado</th>
                        <th>Fecha Pago</th>
                        <c:if test="${pPagos.crear}"><th>Acción (Caja)</th></c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:set var="deudaEncontrada" value="false" />
                    <c:forEach var="cuota" items="${cuotas}">
                        <tr>
                            <td>${cuota.ordenPago}</td>
                            <td>${cuota.nombreConcepto}</td>
                            <td>${cuota.monto}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${cuota.estado == 'PAGADO'}">
                                        <span class="badge badge-success">PAGADO</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-danger">${cuota.estado}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${cuota.fechaPago != null ? cuota.fechaPago : '-'}</td>
                            
                            <c:if test="${pPagos.crear}">
                            <td>
                                <div class="action-buttons">
                                    <c:if test="${cuota.estado == 'PENDIENTE'}">
                                        <c:choose>
                                            <c:when test="${not deudaEncontrada}">
                                                <!-- Es la primera deuda encontrada, SE PUEDE PAGAR -->
                                                <form action="${pageContext.request.contextPath}/pagos" method="post" style="display:inline;">
                                                    <input type="hidden" name="accion" value="cobrar">
                                                    <input type="hidden" name="codAnioRetorno" value="${anioSeleccionado}">
                                                    <input type="hidden" name="codAlumnoRetorno" value="${alumnoSeleccionado}">
                                                    <input type="hidden" name="codCuota" value="${cuota.codCuota}">
                                                    <input type="hidden" name="monto" value="${cuota.monto}">
                                                    <button type="submit" class="btn btn-success btn-sm" onclick="return confirm('¿Confirmar el cobro de S/ ${cuota.monto} por el concepto de ${cuota.nombreConcepto}? Se emitirá un recibo automático.');">
                                                        Cobrar S/ ${cuota.monto}
                                                    </button>
                                                </form>
 
                                                <c:set var="deudaEncontrada" value="true" />
                                            </c:when>
                                            <c:otherwise>
                                                 
                                                <button type="button" class="btn btn-secondary btn-sm" title="No puede pagar esta cuota hasta cancelar las anteriores." disabled>
                                                    Pago Bloqueado
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                    <c:if test="${cuota.estado == 'PAGADO' and pPagos.imprimir}">
                                        <a href="${pageContext.request.contextPath}/recibo?codCuota=${cuota.codCuota}" target="_blank" style="text-decoration:none;">
                                            <button type="button" class="btn btn-info btn-sm">🖨️ Imprimir Recibo</button>
                                        </a>
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
        </c:if>
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

    <script>
        function abrirModal(id) { document.getElementById(id).style.display = "block"; }
        function cerrarModal(id) { document.getElementById(id).style.display = "none"; }
        function seleccionarAlumno(cod, nombre) {
            document.getElementById('codAlumno').value = cod;
            document.getElementById('nombreAlumnoStr').value = nombre;
            cerrarModal('modalAlumno');
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
