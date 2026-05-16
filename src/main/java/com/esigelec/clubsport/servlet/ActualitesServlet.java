package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.dao.DAOFactory;
import com.esigelec.clubsport.dao.ActualiteDAO;
import com.esigelec.clubsport.dao.EspaceClubDAO;
import com.esigelec.clubsport.model.Actualite;
import com.esigelec.clubsport.model.EspaceClub;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Servlet de gestion des actualités d'un club.
 *
 * GET    /api/actualites?codeCommune=35238
 *        → liste des actualités du club (plus récentes en premier)
 *
 * POST   /api/actualites?codeCommune=35238
 *        → publier une nouvelle actualité
 *        Body JSON : { titre, contenu, categorie }
 *
 * PUT    /api/actualites/{idActualite}
 *        → modifier une actualité existante
 *        Body JSON : { titre, contenu, categorie }
 *
 * DELETE /api/actualites/{idActualite}
 *        → supprimer une actualité
 */
@WebServlet("/api/actualites/*")
public class ActualitesServlet extends HttpServlet {

    private final Gson          gson         = new Gson();
    private final ActualiteDAO  actualiteDAO = DAOFactory.getActualiteDAO();
    private final EspaceClubDAO espaceDAO    = DAOFactory.getEspaceClubDAO();

    // ── GET — liste des actualités d'un club ──────────────────────────
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
            List<Actualite> actualites = actualiteDAO.findByIdEspace(espace.getIdEspace());
            gson.toJson(actualites, resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── POST — publier une actualité ──────────────────────────────────
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
        String titre   = str(body, "titre");
        String contenu = str(body, "contenu");

        if (titre.isEmpty() || contenu.isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"titre et contenu sont obligatoires\"}");
            return;
        }

        try {
            // Récupérer ou créer l'espace club
            EspaceClub espace = espaceDAO.findByCodeCommune(codeCommune);
            if (espace == null) {
                espace = new EspaceClub();
                espace.setCodeCommune(codeCommune);
                espace.setNomClub("Club " + codeCommune);
                espace = espaceDAO.insert(espace);
            }

            Actualite a = new Actualite();
            a.setIdEspace(espace.getIdEspace());
            a.setTitre(titre);
            a.setContenu(contenu);
            a.setCategorie(str(body, "categorie").isEmpty()
                           ? "Information" : str(body, "categorie"));

            actualiteDAO.insert(a);
            resp.setStatus(201);
            gson.toJson(a, resp.getWriter());

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── PUT — modifier une actualité ──────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        Integer idActualite = extractId(req);
        if (idActualite == null) { resp.setStatus(400); return; }

        Map<?, ?> body = gson.fromJson(lireBody(req), Map.class);

        try {
            Actualite a = actualiteDAO.findById(idActualite);
            if (a == null) { resp.setStatus(404); return; }

            a.setTitre(str(body, "titre"));
            a.setContenu(str(body, "contenu"));
            a.setCategorie(str(body, "categorie"));

            actualiteDAO.update(a);
            resp.getWriter().write("{\"status\":\"ok\"}");

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── DELETE — supprimer une actualité ──────────────────────────────
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        Integer idActualite = extractId(req);
        if (idActualite == null) { resp.setStatus(400); return; }

        try {
            actualiteDAO.delete(idActualite);
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