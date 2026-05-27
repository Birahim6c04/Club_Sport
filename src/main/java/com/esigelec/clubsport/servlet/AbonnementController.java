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
 * Contrôleur MVC pour les abonnements.
 *
 * GET  /mvc/abonnements?codeCommune=35238   → JSON { count, abonne }
 * GET  /mvc/abonnements/mes-clubs           → JSON liste des clubs suivis
 * POST /mvc/abonnements  codeCommune        → toggle abo/désabo, redirect Referer
 */
@WebServlet("/mvc/abonnements/*")
public class AbonnementController extends HttpServlet {

    private final Gson          gson      = new Gson();
    private final AbonnementDAO abonnDAO  = DAOFactory.getAbonnementDAO();
    private final EspaceClubDAO espaceDAO = DAOFactory.getEspaceClubDAO();

    // ── GET ───────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String pathInfo = req.getPathInfo();
        if ("/mes-clubs".equals(pathInfo)) {
            getMesClubs(req, resp);
        } else {
            getStatut(req, resp);
        }
    }

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
                Map<String, Object> r = new LinkedHashMap<>();
                r.put("count",  0);
                r.put("abonne", false);
                gson.toJson(r, resp.getWriter());
                return;
            }
            int count   = abonnDAO.countByIdEspace(espace.getIdEspace());
            boolean abo = false;
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                abo = abonnDAO.existsByUserAndEspace(
                        (int) session.getAttribute("userId"), espace.getIdEspace());
            }
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("count",  count);
            result.put("abonne", abo);
            gson.toJson(result, resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void getMesClubs(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Connexion requise\"}");
            return;
        }
        try {
            gson.toJson(abonnDAO.findByIdUtilisateur(
                    (int) session.getAttribute("userId")), resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── POST — formulaire HTML (toggle) ──────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");
        String redirectUrl = referer(req);

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(redirectUrl);
            return;
        }

        String codeCommune = req.getParameter("codeCommune");
        if (codeCommune == null || codeCommune.isBlank()) {
            resp.sendRedirect(redirectUrl);
            return;
        }

        try {
            int idUtilisateur = (int) session.getAttribute("userId");
            EspaceClub espace = espaceDAO.findOrCreate(codeCommune);
            int idEspace      = espace.getIdEspace();

            if (abonnDAO.existsByUserAndEspace(idUtilisateur, idEspace)) {
                abonnDAO.deleteByUserAndEspace(idUtilisateur, idEspace);
            } else {
                Abonnement a = new Abonnement();
                a.setIdUtilisateur(idUtilisateur);
                a.setIdEspace(idEspace);
                abonnDAO.insert(a);
            }
        } catch (Exception ignored) { }

        resp.sendRedirect(redirectUrl);
    }

    private String referer(HttpServletRequest req) {
        String ref = req.getHeader("Referer");
        return (ref != null && !ref.isBlank()) ? ref
                : req.getContextPath() + "/club.html";
    }
}
