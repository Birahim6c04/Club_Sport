package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.dao.AbonnementDAO;
import com.esigelec.clubsport.dao.DAOFactory;
import com.esigelec.clubsport.dao.EspaceClubDAO;
import com.esigelec.clubsport.model.Abonnement;
import com.esigelec.clubsport.model.EspaceClub;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API abonnements à un espace club.
 * L'utilisateur doit être connecté (session requise).
 *
 * GET  /api/abonnements?codeCommune=35238
 *      → { count: 42, abonne: true/false }
 *      abonne = true si l'utilisateur connecté est abonné
 *
 * POST /api/abonnements?codeCommune=35238
 *      → toggle : s'abonner si pas abonné, se désabonner sinon
 *      → { count: 43, abonne: true }  ou  { count: 41, abonne: false }
 *
 * GET  /api/abonnements/mes-clubs
 *      → liste des clubs auxquels l'utilisateur est abonné
 *
 * Chemin : src/main/java/com/esigelec/clubs/servlet/AbonnementsServlet.java
 */
@WebServlet("/api/abonnements/*")
public class AbonnementsServlet extends HttpServlet {

    private final Gson           gson        = new Gson();
    private final AbonnementDAO  abonnDAO    = DAOFactory.getAbonnementDAO();
    private final EspaceClubDAO  espaceDAO   = DAOFactory.getEspaceClubDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String pathInfo = req.getPathInfo();

        // GET /api/abonnements/mes-clubs → liste des abonnements de l'utilisateur
        if (pathInfo != null && pathInfo.equals("/mes-clubs")) {
            getMesClubs(req, resp);
            return;
        }

        // GET /api/abonnements?codeCommune=35238 → statut + compteur
        getStatut(req, resp);
    }

    // ── GET statut abonnement + nb abonnés ────────────────────────────
    private void getStatut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String codeCommune = req.getParameter("codeCommune");
        if (codeCommune == null || codeCommune.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"codeCommune requis\"}");
            return;
        }

        try {
            EspaceClub espace = espaceDAO.findByCodeCommune(codeCommune);
            if (espace == null) {
                // Club sans espace créé = 0 abonnés, non abonné
                Map<String, Object> r = new LinkedHashMap<>();
                r.put("count",  0);
                r.put("abonne", false);
                gson.toJson(r, resp.getWriter());
                return;
            }

            int count  = abonnDAO.countByIdEspace(espace.getIdEspace());
            boolean abonne = false;
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                int userId = (int) session.getAttribute("userId");
                abonne = abonnDAO.existsByUserAndEspace(userId, espace.getIdEspace());
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("count",  count);
            result.put("abonne", abonne);
            gson.toJson(result, resp.getWriter());

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── GET mes-clubs — liste des clubs suivis ────────────────────────
    private void getMesClubs(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Connexion requise\"}");
            return;
        }

        try {
            int userId = (int) session.getAttribute("userId");
            gson.toJson(abonnDAO.findByIdUtilisateur(userId), resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── POST — toggle abonnement / désabonnement ──────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // Session obligatoire
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Connexion requise pour s'abonner\"}");
            return;
        }

        String codeCommune = req.getParameter("codeCommune");
        if (codeCommune == null || codeCommune.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"codeCommune requis\"}");
            return;
        }

        try {
            int idUtilisateur = (int) session.getAttribute("userId");

            // Créer l'espace club si premier abonnement
            EspaceClub espace = espaceDAO.findOrCreate(codeCommune);
            int idEspace      = espace.getIdEspace();

            boolean dejaAbonne = abonnDAO.existsByUserAndEspace(idUtilisateur, idEspace);

            if (dejaAbonne) {
                // Se désabonner
                abonnDAO.deleteByUserAndEspace(idUtilisateur, idEspace);
            } else {
                // S'abonner
                Abonnement a = new Abonnement();
                a.setIdUtilisateur(idUtilisateur);
                a.setIdEspace(idEspace);
                abonnDAO.insert(a);
            }

            int newCount = abonnDAO.countByIdEspace(idEspace);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("count",  newCount);
            result.put("abonne", !dejaAbonne);
            gson.toJson(result, resp.getWriter());

        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}