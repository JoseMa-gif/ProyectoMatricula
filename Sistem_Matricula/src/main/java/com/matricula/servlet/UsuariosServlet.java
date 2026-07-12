/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.matricula.servlet;

import com.matricula.dao.RolDAO;
import com.matricula.dao.UsuarioDAO;
import com.matricula.model.Rol;
import com.matricula.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author JOSE
 */

@WebServlet("/usuarios")
public class UsuariosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            UsuarioDAO dao = new UsuarioDAO();
            List<Usuario> lista = dao.listarTodos();
            request.setAttribute("usuarios", lista);
            
            
            RolDAO rolDao = new RolDAO();
            List<Rol> roles = rolDao.listarTodos();
            request.setAttribute("roles", roles);

            request.getRequestDispatcher("/modulos/usuarios.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error al listar usuarios", e);
        }
    } 
}
