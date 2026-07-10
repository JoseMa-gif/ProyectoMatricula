package com.matricula.servlet;

import com.matricula.dao.PagoDAO;
import com.matricula.model.ReciboDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/recibo")
public class ReciboServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String codCuotaStr = request.getParameter("codCuota");
        if (codCuotaStr != null && !codCuotaStr.isEmpty()) {
            try {
                int codCuota = Integer.parseInt(codCuotaStr);
                PagoDAO dao = new PagoDAO();
                ReciboDTO recibo = dao.obtenerDetalleRecibo(codCuota);
                
                if (recibo != null) {
                    request.setAttribute("recibo", recibo);
                    request.getRequestDispatcher("/modulos/recibo.jsp").forward(request, response);
                } else {
                    response.getWriter().println("Recibo no encontrado para esta cuota.");
                }
            } catch (Exception e) {
                response.getWriter().println("Error al cargar el recibo: " + e.getMessage());
            }
        } else {
            response.getWriter().println("Código de cuota no proporcionado.");
        }
    }
}
