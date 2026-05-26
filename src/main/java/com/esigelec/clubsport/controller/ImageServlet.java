package com.esigelec.clubsport.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/uploads/*")
public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String DOSSIER_UPLOAD = "/app/uploads";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nomFichier = request.getPathInfo();

        if (nomFichier == null || nomFichier.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        nomFichier = nomFichier.substring(1);

        File fichier = new File(DOSSIER_UPLOAD, nomFichier);

        if (!fichier.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String type = getServletContext().getMimeType(fichier.getName());

        if (type == null) {
            type = "application/octet-stream";
        }

        response.setContentType(type);
        response.setContentLengthLong(fichier.length());

        try (
            FileInputStream fis = new FileInputStream(fichier);
            OutputStream os = response.getOutputStream()
        ) {

            byte[] buffer = new byte[1024];
            int bytesLu;

            while ((bytesLu = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesLu);
            }
        }
    }
}