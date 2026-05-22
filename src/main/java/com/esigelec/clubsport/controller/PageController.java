package com.esigelec.clubsport.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/mentions", "/cookies"})
public class PageController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if (path.equals("/mentions")) {
            req.getRequestDispatcher("/WEB-INF/jsp/mentions.jsp").forward(req, resp);
        } else if (path.equals("/cookies")) {
            req.getRequestDispatcher("/WEB-INF/jsp/cookies.jsp").forward(req, resp);
        }
    }
}
