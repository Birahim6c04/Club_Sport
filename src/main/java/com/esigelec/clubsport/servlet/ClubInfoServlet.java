package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.dao.DAOFactory;
import com.esigelec.clubsport.dao.EspaceClubDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.SQLException;
import java.util.Map;

/**
 * GET  /api/club/{codeCommune}        → stats réelles + contenu espace_club
 * POST /api/club/{codeCommune}/update → sauvegarde le contenu modifiable
 *
 * Chemin : src/main/java/com/esigelec/clubsport/servlet/ClubInfoServlet.java
 */
@WebServlet("/api/club/*")
public class ClubInfoServlet extends HttpServlet {

    private final Gson          gson      = new Gson();
    private final EspaceClubDAO espaceDAO = DAOFactory.getEspaceClubDAO();

    // ── GET ────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String clubId = extractId(req);
        if (clubId == null) { resp.setStatus(400); return; }

        try {
            // Vraies stats depuis commune + club_stats + licence_stats
            Map<String, Object> stats = espaceDAO.getStatsReelles(clubId);
            if (stats == null) {
                resp.setStatus(404);
                resp.getWriter().write("{\"error\":\"Commune introuvable\"}");
                return;
            }

            // Contenu modifiable depuis espace_club
            Map<String, Object> contenu = espaceDAO.getContenu(clubId);
            if (contenu != null) stats.put("contenu", contenu);

            gson.toJson(stats, resp.getWriter());

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── POST ───────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String clubId = extractId(req);
        if (clubId == null) { resp.setStatus(400); return; }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> body = gson.fromJson(sb.toString(), Map.class);

        try {
            espaceDAO.saveContenu(clubId, body, gson);
            resp.getWriter().write("{\"status\":\"ok\"}");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String extractId(HttpServletRequest req) {
        String p = req.getPathInfo();
        if (p == null || p.length() < 2) return null;
        return p.split("/")[1];
    }
}