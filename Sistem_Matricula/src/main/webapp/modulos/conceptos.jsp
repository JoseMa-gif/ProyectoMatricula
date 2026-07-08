<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Conceptos (Tarifario)</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
</head>
<body>
    <header class="cabecera">
        <div class="cabecera-info">
            <span>Módulo Académico - Tarifario y Conceptos</span>
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

        <c:set var="pConceptos" value="${null}" />
        <c:forEach var="p" items="${sessionScope.permisos}">
            <c:if test="${p.nombreFuncionalidad == 'Conceptos'}">
                <c:set var="pConceptos" value="${p}" />
            </c:if>
        </c:forEach>
        
        <c:set var="tieneAcciones" value="${pConceptos.editar or pConceptos.eliminar}" />

        <c:if test="${pConceptos.crear}">
            <div class="card">
                <h3 id="form-titulo" style="margin-bottom: 1rem;">Crear Nuevo Concepto</h3>
                <form action="${pageContext.request.contextPath}/conceptos" method="post">
                    <input type="hidden" name="accion" id="accion" value="crear">
                    <input type="hidden" name="codConcepto" id="form-codConcepto" value="">
                    <!-- Este campo hidden maneja el Optimistic Lock -->
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
                            <label for="codTipoConcepto">Tipo de Pago:</label>
                            <select id="codTipoConcepto" name="codTipoConcepto" class="form-control" required>
                                <c:forEach var="tc" items="${tiposConcepto}">
                                    <option value="${tc.codTipoConcepto}">${tc.nombre}</option>
                                </c:forEach>
                            </select>
                        </div>
    
                        <div class="form-group">
                            <label for="nombreConcepto">Nombre Concepto:</label>
                            <input type="text" id="nombreConcepto" name="nombreConcepto" class="form-control" required>
                        </div>
    
                        <div class="form-group">
                            <label for="monto">Monto (S/):</label>
                            <input type="number" step="0.01" id="monto" name="monto" class="form-control" required>
                        </div>
    
                        <div class="form-group">
                            <label for="ordenPago">Orden de Pago:</label>
                            <input type="number" id="ordenPago" name="ordenPago" class="form-control" required placeholder="Ej: 1=Matricula, 2=Marzo...">
                            <small style="color:gray;">(1 a 12)</small>
                        </div>
                        
                        <div class="form-group">
                            <label for="obligatorio">¿Es Obligatorio?</label>
                            <select id="obligatorio" name="obligatorio" class="form-control">
                                <option value="1">Sí</option>
                                <option value="0">No</option>
                            </select>
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
                        <button type="submit" id="btn-guardar" class="btn btn-primary">Guardar Concepto</button>
                        <button type="button" id="btn-cancelar" class="btn btn-secondary" style="display:none;" onclick="cancelarEdicion()">Cancelar</button>
                    </div>
                </form>
            </div>
        </c:if>

        <c:if test="${pConceptos.crear}">
            <div class="card">
                <h3 style="margin-bottom: 1rem;">📋 Clonar Conceptos a Otro Año</h3>
                <form action="${pageContext.request.contextPath}/conceptos" method="post">
                    <input type="hidden" name="accion" value="clonar">
                    <div class="form-grid" style="align-items: end;">
                        <div class="form-group">
                            <label for="codAnioOrigen">Año Origen:</label>
                            <select id="codAnioOrigen" name="codAnioOrigen" class="form-control" required>
                                <option value="">-- Seleccione --</option>
                                <c:forEach var="anio" items="${anios}">
                                    <option value="${anio.codAnioAcademico}">${anio.anio}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="codAnioDestino">Año Destino:</label>
                            <select id="codAnioDestino" name="codAnioDestino" class="form-control" required>
                                <option value="">-- Seleccione --</option>
                                <c:forEach var="anio" items="${anios}">
                                    <option value="${anio.codAnioAcademico}">${anio.anio}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <button type="submit" class="btn btn-info" onclick="return confirm('¿Está seguro de clonar los conceptos del año origen al año destino? Los conceptos que ya existan no se duplicarán.');">
                                📋 Clonar Conceptos
                            </button>
                        </div>
                    </div>
                    <small style="color:gray;">Copia todos los conceptos activos (nombre, monto, orden, tipo) del año origen al destino. No duplica si ya existen.</small>
                </form>
            </div>
        </c:if>

        <div class="card">
            <div class="page-header-actions">
                <h2 class="page-title" style="margin:0;">Listado de Conceptos</h2>
            </div>
            <div class="table-container">
                <table>
            <thead>
                <tr>
                    <th>Año</th>
                    <th>Tipo</th>
                    <th>Concepto</th>
                    <th>Monto (S/)</th>
                    <th>Orden</th>
                    <th>Obligatorio</th>
                    <th>Estado</th>
                    <th>Versión (Lock)</th>
                    <c:if test="${tieneAcciones}"><th>Acciones</th></c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${conceptos}">
                    <tr style="${!c.estado ? 'background-color: #ffe6e6; color: #999;' : ''}">
                        <td>${c.nombreAnio}</td>
                        <td>${c.nombreTipoConcepto}</td>
                        <td>${c.nombreConcepto}</td>
                        <td>${c.monto}</td>
                        <td>${c.ordenPago}</td>
                        <td>${c.obligatorio ? 'Sí' : 'No'}</td>
                        <td>
                            <c:choose>
                                <c:when test="${c.estado}"><span class="badge badge-success">Activo</span></c:when>
                                <c:otherwise><span class="badge badge-danger">Inactivo</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>v${c.version}</td>
                        <c:if test="${tieneAcciones}">
                        <td>
                            <div class="action-buttons">
                                <c:if test="${pConceptos.editar}">
                                    <button class="btn btn-warning btn-sm" onclick="editarConcepto('${c.codConcepto}', '${c.codAnioAcademico}', '${c.codTipoConcepto}', '${c.nombreConcepto}', '${c.monto}', '${c.ordenPago}', '${c.obligatorio ? 1 : 0}', '${c.estado ? 1 : 0}', '${c.version}')">Editar</button>
                                </c:if>
                                <c:if test="${c.estado and pConceptos.eliminar}">
                                    <form action="${pageContext.request.contextPath}/conceptos" method="post" style="display:inline;">
                                        <input type="hidden" name="accion" value="eliminar">
                                        <input type="hidden" name="codConcepto" value="${c.codConcepto}">
                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('¿Estás seguro que deseas eliminar este concepto?');">Eliminar</button>
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
        function editarConcepto(codConcepto, codAnio, codTipo, nombre, monto, orden, obligatorio, estado, version) {
            document.getElementById('form-titulo').innerText = 'Editar Concepto';
            document.getElementById('accion').value = 'editar';
            document.getElementById('form-codConcepto').value = codConcepto;
            document.getElementById('codAnioAcademico').value = codAnio;
            document.getElementById('codTipoConcepto').value = codTipo;
            document.getElementById('nombreConcepto').value = nombre;
            document.getElementById('monto').value = monto;
            document.getElementById('ordenPago').value = orden;
            document.getElementById('obligatorio').value = obligatorio;
            document.getElementById('estado').value = estado;

            document.getElementById('form-version').value = version;
            
            document.getElementById('btn-guardar').innerText = 'Actualizar Concepto';
            document.getElementById('btn-cancelar').style.display = 'inline-block';
            window.scrollTo(0, 0);
        }

        function cancelarEdicion() {
            document.getElementById('form-titulo').innerText = 'Crear Nuevo Concepto';
            document.getElementById('accion').value = 'crear';
            document.getElementById('form-codConcepto').value = '';
            document.getElementById('nombreConcepto').value = '';
            document.getElementById('monto').value = '';
            document.getElementById('ordenPago').value = '';
            document.getElementById('obligatorio').value = '1';
            document.getElementById('estado').value = '1';
            document.getElementById('form-version').value = '';
            
            document.getElementById('btn-guardar').innerText = 'Guardar Concepto';
            document.getElementById('btn-cancelar').style.display = 'none';
        }
    </script>
</body>
</html>
