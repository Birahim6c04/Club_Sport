package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.service.ExporterPdfService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/export-pdf")
public class ExporterPdfServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ExporterPdfService exporterPdfService =
            new ExporterPdfService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String type = request.getParameter("type");

        if ("dashboard".equals(type)
                || "ElusDashboard".equals(type)) {

            exporterPdfService.exporterDashboardPdf(
                    request,
                    response
            );

            return;
        }
        
        if ("classement".equals(type)) {
            exporterPdfService.exporterClassementPdf(request, response);
            return;
        }

        if ("indicateurs".equals(type)) {
            exporterPdfService.exporterIndicateursPdf(request, response);
            return;
        }

        response.sendRedirect("ElusDashboard");
    }
    
}