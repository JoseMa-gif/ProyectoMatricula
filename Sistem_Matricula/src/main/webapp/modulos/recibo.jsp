<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Recibo Electrónico</title>
    <style>
        body { font-family: "Courier New", Courier, monospace; background-color: #f0f0f0; padding: 20px; }
        .ticket { background-color: white; width: 300px; margin: 0 auto; padding: 20px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
        .centrado { text-align: center; }
        .linea { border-bottom: 1px dashed #000; margin: 10px 0; }
        h2 { margin: 0 0 10px 0; font-size: 18px; }
        p { margin: 5px 0; font-size: 14px; }
        .btn-imprimir { display: block; width: 100%; padding: 10px; margin-top: 20px; background-color: #007bff; color: white; border: none; cursor: pointer; font-weight: bold; }
        @media print {
            body { background-color: white; padding: 0; }
            .ticket { box-shadow: none; width: 100%; margin: 0; padding: 0; }
            .btn-imprimir { display: none; } /* Ocultar botón al imprimir */
        }
    </style>
</head>
<body>
    <div class="ticket">
        <div class="centrado">
            <h2>COLEGIO NACIONAL</h2>
            <p>RUC: 20123456789</p>
            <p>Av. Principal 123, Lima</p>
        </div>
        <div class="linea"></div>
        <div class="centrado">
            <p><strong>RECIBO ELECTRÓNICO</strong></p>
            <p><strong>${recibo.correlativo}</strong></p>
        </div>
        <div class="linea"></div>
        <p><strong>Fecha:</strong> ${recibo.fecha}</p>
        <p><strong>Alumno:</strong> ${recibo.alumno}</p>
        <div class="linea"></div>
        <p><strong>Concepto:</strong></p>
        <p>${recibo.concepto}</p>
        <p style="text-align: right; font-size: 18px;"><strong>Total: S/ ${recibo.monto}</strong></p>
        <div class="linea"></div>
        <div class="centrado">
            <p>¡Gracias por su puntualidad!</p>
            <p>Documento sin valor fiscal</p>
        </div>
        
        <button class="btn-imprimir" onclick="window.print()">🖨️ IMPRIMIR RECIBO</button>
    </div>
</body>
</html>
