package com.matricula.servlet;

import com.matricula.dao.MatriculaDAO;
import com.matricula.model.Matricula;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@WebServlet("/reporte_matriculas")
public class ReporteMatriculasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
  
        if (request.getSession().getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            MatriculaDAO dao = new MatriculaDAO();
            List<Matricula> matriculas = dao.listarActivas();

 
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=Reporte_Matriculas_Activas.xlsx");

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Matriculas Activas");

    
                Row headerRow = sheet.createRow(0);
                String[] columns = {"ID", "Alumno", "Aula (Nivel/Grado/Sección)", "Año Académico", "Fecha de Matrícula"};
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                }

   
                int rowNum = 1;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                
                for (Matricula m : matriculas) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(m.getCodMatricula());
                    row.createCell(1).setCellValue(m.getNombreAlumno() != null ? m.getNombreAlumno() : "");
                    row.createCell(2).setCellValue(m.getDescripcionAula() != null ? m.getDescripcionAula() : "");
                    row.createCell(3).setCellValue(m.getNombreAnio() != null ? m.getNombreAnio() : "");
                    
                    if (m.getFechaMatricula() != null) {
                        row.createCell(4).setCellValue(sdf.format(m.getFechaMatricula()));
                    } else {
                        row.createCell(4).setCellValue("");
                    }
                }

     
                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                 
                try (OutputStream out = response.getOutputStream()) {
                    workbook.write(out);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error al generar el reporte Excel", e);
        }
    }
}
