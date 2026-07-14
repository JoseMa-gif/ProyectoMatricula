<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Sistema de Matrícula - Iniciar Sesión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/recursos/css/estilos.css">
    <style>

        .toast-error {
            position: fixed;
            top: 1.5rem;
            right: 1.5rem;
            z-index: 9999;
            display: flex;
            align-items: flex-start;
            gap: 0.9rem;
            background: #fff;
            border-radius: 14px;
            box-shadow: 0 8px 30px rgba(0,0,0,0.14), 0 0 0 1px rgba(239,68,68,0.15);
            padding: 1rem 1.25rem 0.6rem;
            min-width: 300px;
            max-width: 380px;
            animation: toastIn 0.4s cubic-bezier(0.34,1.56,0.64,1) both;
            overflow: hidden;
        }

 
        .toast-error::after {
            content: '';
            position: absolute;
            bottom: 0; left: 0;
            height: 3px;
            width: 100%;
            background: linear-gradient(90deg, #DC2626, #F87171);
            border-radius: 0 0 14px 14px;
            animation: progressBar 4s linear forwards;
        }

        @keyframes toastIn {
            from { opacity: 0; transform: translateX(60px) scale(0.92); }
            to   { opacity: 1; transform: translateX(0)  scale(1); }
        }

        @keyframes toastOut {
            to { opacity: 0; transform: translateX(60px) scale(0.92); }
        }

        @keyframes progressBar {
            from { width: 100%; }
            to   { width: 0%; }
        }

        .toast-icon-wrap {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 38px;
            height: 38px;
            background: linear-gradient(135deg, #FEE2E2, #FECACA);
            border-radius: 50%;
            flex-shrink: 0;
            font-size: 1.1rem;
        }

        /* Texto */
        .toast-body {
            flex: 1;
            padding-bottom: 0.5rem;
        }
        .toast-title {
            font-size: 0.78rem;
            font-weight: 700;
            color: #DC2626;
            text-transform: uppercase;
            letter-spacing: 0.06em;
            margin-bottom: 2px;
        }
        .toast-msg {
            font-size: 0.88rem;
            color: #374151;
            font-weight: 500;
            line-height: 1.4;
        }

        /* Botón cerrar */
        .toast-close {
            background: none;
            border: none;
            cursor: pointer;
            color: #9CA3AF;
            font-size: 1rem;
            padding: 0;
            line-height: 1;
            flex-shrink: 0;
            transition: color 0.2s;
        }
        .toast-close:hover { color: #374151; }
    </style>
</head>
<body>

    <% if (request.getAttribute("error") != null) { %>
        <div class="toast-error" id="toastError">
            <div class="toast-icon-wrap">🔐</div>
            <div class="toast-body">
                <div class="toast-title">Acceso denegado</div>
                <div class="toast-msg"><%= request.getAttribute("error") %></div>
            </div>
            <button class="toast-close" onclick="cerrarToast()" title="Cerrar">✕</button>
        </div>
        <script>
 
            setTimeout(function() { cerrarToast(); }, 4000);
            function cerrarToast() {
                var t = document.getElementById('toastError');
                if (t) {
                    t.style.animation = 'toastOut 0.3s ease forwards';
                    setTimeout(function(){ t.remove(); }, 300);
                }
            }
        </script>
    <% } %>

    <div class="login-split">
        
        <div class="login-left">
            <div class="login-branding">
                <h1>App Matrícula</h1>
                <p>Gestión académica y financiera moderna, rápida y segura.</p>
            </div>
        </div>

     
        <div class="login-right">
            <div class="contenedor-login">
                <h2>Bienvenido de nuevo</h2>
                <p class="login-subtitle">Ingresa tus credenciales para acceder al sistema</p>

                <form action="${pageContext.request.contextPath}/login" method="post">
                    <label for="usuario">👤 Usuario</label>
                    <input type="text" id="usuario" name="usuario" placeholder="Ej: admin" required autofocus>

                    <label for="password">🔒 Contraseña</label>
                    <input type="password" id="password" name="password" placeholder="••••••••" required>

                    <button type="submit">Iniciar Sesión</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>

