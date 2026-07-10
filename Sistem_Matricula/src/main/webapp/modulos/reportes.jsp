<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Módulo de Reportes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
</head>
<body>
    <header class="cabecera">
        <div class="cabecera-info">
            <span>Módulo de Reportes</span>
        </div>
        <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn btn-secondary">Volver al Dashboard</a>
    </header>
    <main class="contenido">
        <div class="page-header-actions">
            <h2 class="page-title" style="margin:0;">Reportes Disponibles</h2>
        </div>
        <p style="color: #6B7280; margin-bottom: 20px;">Seleccione el reporte que desea generar y descargar.</p>

        <div class="form-grid">
            <div class="card" style="text-align: center;">
                <h3 style="margin-bottom: 15px;">Reporte de Matrículas</h3>
                <p style="color: #4B5563; font-size: 0.9em; margin-bottom: 20px;">Genera un listado de todas las matrículas activas registradas en el sistema.</p>
                <div style="display: flex; justify-content: center; gap: 15px;">
                    <a href="${pageContext.request.contextPath}/reporte_matriculas" target="_blank" class="btn btn-success">
                        📊 Excel
                    </a>
                    <a href="${pageContext.request.contextPath}/reporte_matriculas_pdf" target="_blank" class="btn btn-danger">
                        📄 PDF
                    </a>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
