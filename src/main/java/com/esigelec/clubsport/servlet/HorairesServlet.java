package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.dao.DAOFactory;
import com.esigelec.clubsport.dao.EspaceClubDAO;
import com.esigelec.clubsport.dao.HorairesDAO;
import com.esigelec.clubsport.model.EspaceClub;
import com.esigelec.clubsport.model.Horaire;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Servlet de gestion des horaires d'un club.
 *
 * GET    /api/horaires?codeCommune=35238
 *        → liste des séances de la semaine du club
 *
 * POST   /api/horaires?codeCommune=35238
 *        → ajouter une séance
 *        Body JSON : { jour, heureDebut, heureFin, activite, niveau }
 *
 * PUT    /api/horaires/{idHoraire}
 *        → modifier une séance existante
 *        Body JSON : { jour, heureDebut, heureFin, activite, niveau }
 *
 * DELETE /api/horaires/{idHoraire}
 *        → supprimer une séance
 */
@WebServlet("/api/horaires/*")
public class HorairesServlet extends HttpServlet {

    private final Gson         gson         = new Gson();
    private final HorairesDAO  horairesDAO  = DAOFactory.getHorairesDAO();
    private final EspaceClubDAO espaceDAO   = DAOFactory.getEspaceClubDAO();

    // ── GET — liste des horaires d'un club ────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String codeCommune = req.getParameter("codeCommune");
        if (codeCommune == null || codeCommune.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"codeCommune requis\"}");
            return;
        }

        try {
            EspaceClub espace = espaceDAO.findByCodeCommune(codeCommune);
            if (espace == null) {
                resp.getWriter().write("[]");
                return;
            }
            List<Horaire> horaires = horairesDAO.findByIdEspace(espace.getIdEspace());
            gson.toJson(horaires, resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── POST — ajouter une séance ─────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String codeCommune = req.getParameter("codeCommune");
        if (codeCommune == null || codeCommune.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"codeCommune requis\"}");
            return;
        }

        Map<?, ?> body = gson.fromJson(lireBody(req), Map.class);

        try {
            // Récupérer ou créer l'espace club
            EspaceClub espace = espaceDAO.findByCodeCommune(codeCommune);
            if (espace == null) {
                espace = new EspaceClub();
                espace.setCodeCommune(codeCommune);
                espace.setNomClub("Club " + codeCommune);
                espace = espaceDAO.insert(espace);
            }

            Horaire h = new Horaire();
            h.setIdEspace(espace.getIdEspace());
            h.setJour(str(body, "jour"));
            h.setHeureDebut(Time.valueOf(str(body, "heureDebut") + ":00"));
            h.setHeureFin(Time.valueOf(str(body, "heureFin") + ":00"));
            h.setActivite(str(body, "activite"));
            h.setNiveau(str(body, "niveau"));

            horairesDAO.insert(h);
            resp.setStatus(201);
            gson.toJson(h, resp.getWriter());

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── PUT — modifier une séance ─────────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        Integer idHoraire = extractId(req);
        if (idHoraire == null) { resp.setStatus(400); return; }

        Map<?, ?> body = gson.fromJson(lireBody(req), Map.class);

        try {
            Horaire h = horairesDAO.findById(idHoraire);
            if (h == null) { resp.setStatus(404); return; }

            h.setJour(str(body, "jour"));
            h.setHeureDebut(Time.valueOf(str(body, "heureDebut") + ":00"));
            h.setHeureFin(Time.valueOf(str(body, "heureFin") + ":00"));
            h.setActivite(str(body, "activite"));
            h.setNiveau(str(body, "niveau"));

            horairesDAO.update(h);
            resp.getWriter().write("{\"status\":\"ok\"}");

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── DELETE — supprimer une séance ─────────────────────────────────
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        Integer idHoraire = extractId(req);
        if (idHoraire == null) { resp.setStatus(400); return; }

        try {
            horairesDAO.delete(idHoraire);
            resp.getWriter().write("{\"status\":\"ok\"}");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── Utilitaires ───────────────────────────────────────────────────
    private String lireBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private Integer extractId(HttpServletRequest req) {
        String p = req.getPathInfo();
        if (p == null || p.length() < 2) return null;
        try { return Integer.parseInt(p.split("/")[1]); }
        catch (NumberFormatException e) { return null; }
    }

    private String str(Map<?, ?> m, String key) {
        Object v = m.get(key);
        return v != null ? v.toString().trim() : "";
    }
}