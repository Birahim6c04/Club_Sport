package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.model.Club;
import com.esigelec.clubsport.service.ClubService;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/carte/donnees", "/carte/communes"})
public class MapDataController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ClubService service = new ClubService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        String path = req.getServletPath();

        try {
            // Autocomplétion des communes
            if (path.endsWith("/communes")) {
                String q = req.getParameter("q");
                List<String[]> communes = service.rechercherCommune(q);
                out.write(gson.toJson(communes));
                return;
            }

            // Coordonnées des clubs pour les marqueurs
            String federation = req.getParameter("federation");
            String region     = req.getParameter("region");
            String commune    = req.getParameter("commune");
            String rayonStr   = req.getParameter("rayon");
            String codePostal = req.getParameter("codePostal");

            List<Club> clubs;

            if (commune != null && !commune.isEmpty() && rayonStr != null) {
                int rayon = Integer.parseInt(rayonStr);
                clubs = service.rechercherParRayon(federation, commune, rayon);
            } else {
                clubs = service.rechercher(federation, region, codePostal);
            }

            out.write(gson.toJson(clubs));

        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            out.write("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            out.write("{\"error\":\"Erreur serveur\"}");
        }
    }
}