<<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Usuarios</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
    <style>
        .form-control.input-error { border-color: #EF4444; box-shadow: 0 0 0 3px rgba(239,68,68,0.15); }
        .field-error-msg { color: #EF4444; font-size: 0.78rem; margin-top: 4px; display: none; }
        /* Modal reseteo */
        .modal-reset-overlay {
            display: none;
            position: fixed; inset: 0; z-index: 1000;
            background: rgba(17,24,39,0.65);
            backdrop-filter: blur(4px);
            align-items: center; justify-content: center;
        }
        .modal-reset-overlay.active { display: flex; }
        .modal-reset-box {
            background: #fff;
            border-radius: 16px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.18);
            padding: 2rem;
            width: 100%; max-width: 420px;
            animation: modalIn 0.3s cubic-bezier(0.34,1.56,0.64,1);
        }
        @keyframes modalIn {
            from { opacity:0; transform: scale(0.88) translateY(-20px); }
            to   { opacity:1; transform: scale(1)   translateY(0); }
        }
        .modal-reset-header {
            display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1.25rem;
        }
        .modal-reset-icon {
            width: 44px; height: 44px; border-radius: 50%;
            background: linear-gradient(135deg,#EEF2FF,#C7D2FE);
            display: flex; align-items: center; justify-content: center;
            font-size: 1.3rem; flex-shrink: 0;
        }
        .modal-reset-title { font-size: 1.1rem; font-weight: 700; color: #111827; }
        .modal-reset-sub   { font-size: 0.82rem; color: #6B7280; margin-top: 2px; }
        .modal-reset-user-badge {
            display: inline-flex; align-items: center; gap: 6px;
            background: #F3F4F6; border-radius: 8px;
            padding: 0.45rem 0.9rem; font-weight: 600; font-size: 0.88rem;
            color: #374151; margin-bottom: 1.25rem; border: 1px solid #E5E7EB;
        }
    </style>
</head>
<body>
    <header class="cabecera">
        <div class="cabecera-info">
            <span>Módulo de Usuarios</span>
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

        <c:set var="pUsuarios" value="${null}" />
        <c:forEach var="p" items="${sessionScope.permisos}">
            <c:if test="${p.nombreFuncionalidad == 'Usuarios'}">
                <c:set var="pUsuarios" value="${p}" />
            </c:if>
        </c:forEach>
        
        <c:set var="tieneAcciones" value="${pUsuarios.editar or pUsuarios.eliminar}" />

        <c:if test="${pUsuarios.crear}">
            <div class="card">
                <h3 id="form-titulo" style="margin-bottom: 1rem;">Crear Nuevo Usuario</h3>
                <form id="formUsuario" action="${pageContext.request.contextPath}/usuarios" method="post" novalidate>
                <input type="hidden" name="accion" id="accion" value="crear">
                <input type="hidden" name="idUsuario" id="form-idUsuario" value="">
                
                <div class="form-grid">
                    <div class="form-group">
                        <label for="usuario">Usuario:</label>
                        <input type="text" id="usuario" name="usuario" class="form-control"
                               required maxlength="50"
                               oninput="filtrarUsuario(this); validarUsuario(this)">
                        <div class="field-error-msg" id="err-usuario">El usuario debe ser un correo válido (debe contener @).</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="password" id="lbl-password">Contraseña:</label>
                        <input type="password" id="password" name="password" class="form-control"
                               required minlength="6" oninput="validarPwUsuario(this)">
                        <div class="field-error-msg" id="err-password">La contraseña debe tener al menos 6 caracteres.</div>
                        <small id="txt-password-info" style="display:none; color:#666;">Dejar en blanco para no cambiar</small><br id="br-password-info" style="display:none;">
                    </div>
                    
                    <div class="form-group">
                        <label for="idRol">Rol:</label>
                        <select id="idRol" name="idRol" class="form-control" required>
                            <c:forEach var="rol" items="${roles}">
                                <option value="${rol.idRol}">${rol.nombreRol}</option>
                            </c:forEach>
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
                    <button type="submit" id="btn-guardar" class="btn btn-primary">Guardar Usuario</button>
                    <button type="button" id="btn-cancelar" class="btn btn-secondary" style="display:none;" onclick="cancelarEdicion()">Cancelar</button>
                </div>
            </form>
        </div>
        </c:if>

        <div class="card">
            <div class="page-header-actions">
                <h2 class="page-title" style="margin:0;">Listado de Usuarios</h2>
            </div>
            <div class="table-container">
                <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Rol</th>
                    <th>Estado</th>
                    <c:if test="${tieneAcciones}">
                        <th>Acciones</th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${usuarios}">
                    <tr style="${!u.estado ? 'background-color: #ffe6e6; color: #999;' : ''}">
                        <td>${u.idUsuario}</td>
                        <td>${u.usuario}</td>
                        <td>${u.nombreRol}</td>
                        <td>
                            <c:choose>
                                <c:when test="${u.estado}"><span class="badge badge-success">Activo</span></c:when>
                                <c:otherwise><span class="badge badge-danger">Inactivo (Eliminado)</span></c:otherwise>
                            </c:choose>
                        </td>
                        <c:if test="${tieneAcciones}">
                        <td>
                            <div class="action-buttons">
                                <c:if test="${pUsuarios.editar}">
                                    <button class="btn btn-warning btn-sm" onclick="editarUsuario('${u.idUsuario}', '${u.usuario}', '${u.idRol}', '${u.estado ? 1 : 0}')">Editar</button>
                                    <button class="btn btn-info btn-sm" onclick="abrirModalReset('${u.idUsuario}', '${u.usuario}')">🔑 Reset Clave</button>
                                </c:if>
                                <c:if test="${u.estado and pUsuarios.eliminar}">
                                    <c:choose>
                                        <c:when test="${u.idUsuario != 1 && u.usuario != 'admin'}">
                                            <form action="${pageContext.request.contextPath}/usuarios" method="post" style="display:inline;">
                                                <input type="hidden" name="accion" value="eliminar">
                                                <input type="hidden" name="idUsuario" value="${u.idUsuario}">
                                                <button type="submit" class="btn btn-danger btn-sm"
                                                        onclick="return confirm('¿Estás seguro que deseas eliminar este usuario?');">
                                                    Eliminar
                                                </button>
                                            </form>
                                        </c:when>

                                        <c:otherwise>
                                            <button type="button" class="btn btn-secondary btn-sm" disabled>
                                                Superusuario
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
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

    <div class="modal-reset-overlay" id="modalReset">
        <div class="modal-reset-box">
            <div class="modal-reset-header">
                <div class="modal-reset-icon">🔑</div>
                <div>
                    <div class="modal-reset-title">Resetear Contraseña</div>
                    <div class="modal-reset-sub">No necesitas conocer la contraseña actual</div>
                </div>
            </div>

            <div class="modal-reset-user-badge">
                <span>👤</span>
                <span id="reset-nombre-usuario">—</span>
            </div>

            <form id="formReset" action="${pageContext.request.contextPath}/usuarios" method="post" novalidate>
                <input type="hidden" name="accion" value="resetear_password">
                <input type="hidden" name="idUsuario" id="reset-idUsuario" value="">

                <div class="form-group">
                    <label for="reset-nueva-pw">Nueva contraseña</label>
                    <input type="password" id="reset-nueva-pw" name="nuevaPassword"
                           class="form-control" placeholder="Mínimo 6 caracteres"
                           oninput="validarResetPw()">
                    <div class="field-error-msg" id="err-reset-pw">Mínimo 6 caracteres requeridos.</div>
                </div>

                <div class="form-group">
                    <label for="reset-confirmar-pw">Confirmar contraseña</label>
                    <input type="password" id="reset-confirmar-pw" name="confirmarPassword"
                           class="form-control" placeholder="Repite la nueva contraseña"
                           oninput="validarResetConfirmar()">
                    <div class="field-error-msg" id="err-reset-confirmar">Las contraseñas no coinciden.</div>
                </div>

                <div style="display:flex; gap:0.75rem; margin-top:1.5rem;">
                    <button type="submit" class="btn btn-primary" style="flex:1;">Actualizar Contraseña</button>
                    <button type="button" class="btn btn-secondary" style="flex:1;" onclick="cerrarModalReset()">Cancelar</button>
                </div>
            </form>
        </div>
    </div>
    </body>
</html>
