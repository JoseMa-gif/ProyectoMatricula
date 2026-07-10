package com.matricula.servlet;

import com.matricula.dao.AlumnoDAO;
import com.matricula.dao.CatalogoDAO;
import com.matricula.dao.CuotaDAO;
import com.matricula.dao.MatriculaDAO;
import com.matricula.dao.PagoDAO;
import com.matricula.model.Cuota;
import com.matricula.model.Matricula;
import com.matricula.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/pagos")
public class PagosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            CatalogoDAO catDao = new CatalogoDAO();
            request.setAttribute("anios", catDao.listarAnios());
            
            AlumnoDAO alumnoDao = new AlumnoDAO();
            request.setAttribute("alumnos", alumnoDao.listarActivos());

            String codAnioStr = request.getParameter("codAnioAcademico");
            String codAlumnoStr = request.getParameter("codAlumno");
            
            if (codAnioStr != null && !codAnioStr.isEmpty() && codAlumnoStr != null && !codAlumnoStr.isEmpty()) {
                int codAnio = Integer.parseInt(codAnioStr);
                int codAlumno = Integer.parseInt(codAlumnoStr);
                
                MatriculaDAO matDao = new MatriculaDAO();
                Matricula m = matDao.buscarPorAlumnoYAnio(codAlumno, codAnio);
                
                if (m != null) {
                    CuotaDAO cuotaDao = new CuotaDAO();
                    List<Cuota> cuotas = cuotaDao.listarPorMatricula(m.getCodMatricula());
                    request.setAttribute("cuotas", cuotas);
                    request.setAttribute("matriculaEncontrada", m);
                } else {
                    request.setAttribute("mensajeError", "El alumno seleccionado no tiene una matrícula activa en el año especificado.");
                }
                
                request.setAttribute("anioSeleccionado", codAnioStr);
                request.setAttribute("alumnoSeleccionado", codAlumnoStr);
            }

            request.getRequestDispatcher("/modulos/pagos.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Error al cargar pantalla de pagos", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        String codAnio = request.getParameter("codAnioRetorno");
        String codAlumno = request.getParameter("codAlumnoRetorno");
        
        try {
            if ("cobrar".equals(accion)) {
                int codCuota = Integer.parseInt(request.getParameter("codCuota"));
                BigDecimal monto = new BigDecimal(request.getParameter("monto"));
                
                Usuario u = (Usuario) request.getSession().getAttribute("usuario");
                String ip = request.getRemoteAddr();
                
                PagoDAO pagoDao = new PagoDAO();
                String reciboGenerado = pagoDao.registrarPagoTransaccional(codCuota, monto, u.getIdUsuario(), ip);
                
                request.getSession().setAttribute("mensajeExito", "Pago registrado correctamente. N° de Recibo generado: " + reciboGenerado);
            }
            
            response.sendRedirect(request.getContextPath() + "/pagos?codAnioAcademico=" + codAnio + "&codAlumno=" + codAlumno);
        } catch (Exception e) {
            request.getSession().setAttribute("mensajeError", "Ocurrió un error en la operación: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/pagos?codAnioAcademico=" + codAnio + "&codAlumno=" + codAlumno);
        }
    }
}
