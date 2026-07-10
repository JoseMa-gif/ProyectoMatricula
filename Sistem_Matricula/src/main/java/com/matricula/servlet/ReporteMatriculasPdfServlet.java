package com.matricula.servlet;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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

@WebServlet("/reporte_matriculas_pdf")
public class ReporteMatriculasPdfServlet extends HttpServlet {

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

          
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=Reporte_Matriculas_Activas.pdf");

            Document document = new Document();
            try (OutputStream out = response.getOutputStream()) {
                PdfWriter.getInstance(document, out);
                document.open();

                Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
                Paragraph titulo = new Paragraph("Reporte de Matrículas Activas", fontTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                titulo.setSpacingAfter(20);
                document.add(titulo);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{1f, 3f, 3f, 2f, 2f});

                Font fontCabecera = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

                String[] columns = {"ID", "Alumno", "Aula", "Año Acad.", "Fecha Matr."};
                for (String column : columns) {
                    PdfPCell cell = new PdfPCell(new Phrase(column, fontCabecera));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                Font fontDatos = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                for (Matricula m : matriculas) {
                    table.addCell(new Phrase(String.valueOf(m.getCodMatricula()), fontDatos));
                    table.addCell(new Phrase(m.getNombreAlumno() != null ? m.getNombreAlumno() : "", fontDatos));
                    table.addCell(new Phrase(m.getDescripcionAula() != null ? m.getDescripcionAula() : "", fontDatos));
                    table.addCell(new Phrase(m.getNombreAnio() != null ? m.getNombreAnio() : "", fontDatos));
                    table.addCell(new Phrase(m.getFechaMatricula() != null ? sdf.format(m.getFechaMatricula()) : "", fontDatos));
                }

                document.add(table);
                document.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error al generar el reporte PDF", e);
        }
    }
}
