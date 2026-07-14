<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
</head>
<body>
    <h1>Ha ocurrido un error en el servidor (HTTP 500)</h1>
    <p>Mensaje: <%= exception != null ? exception.getMessage() : "Error desconocido" %></p>
    <%
        if(exception != null) {
            out.println("<pre>");
            exception.printStackTrace(new java.io.PrintWriter(out));
            out.println("</pre>");
        }
    %>
</body>
</html>
