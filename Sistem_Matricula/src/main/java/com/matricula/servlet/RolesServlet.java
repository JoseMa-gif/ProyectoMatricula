package com.matricula.servlet;

import com.matricula.dao.RolDAO;
import com.matricula.dao.RolFuncionalidadDAO;
import com.matricula.model.Rol;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/roles")
public class RolesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            RolDAO dao = new RolDAO();
            List<Rol> lista = dao.listarTodos();
            request.setAttribute("roles", lista);
            
            String idRolStr = request.getParameter("idRol");
            if (idRolStr != null && !idRolStr.isEmpty()) {
                int idRol = Integer.parseInt(idRolStr);
                RolFuncionalidadDAO rfDao = new RolFuncionalidadDAO();
                request.setAttribute("permisosRol", rfDao.listarPorRol(idRol));
                request.setAttribute("rolSeleccionado", idRol);
            }

            request.getRequestDispatcher("/modulos/roles.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error al listar roles", e);
        }
    }
}
