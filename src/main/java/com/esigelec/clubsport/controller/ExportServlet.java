package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.service.ExporterExcelService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/export")
public class ExportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ExporterExcelService exportExcelService = new ExporterExcelService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");

        if ("dashboard".equals(type) || "ElusDashboard".equals(type)) {
            exportExcelService.exporterDashboard(request, response);
            return;
        }

        if ("classement".equals(type)) {
            exportExcelService.exporterClassement(request, response);
            return;
        }

        if ("indicateurs".equals(type)) {
            exportExcelService.exporterIndicateurs(request, response);
            return;
        }

        response.sendRedirect("ElusDashboard");
    }
}