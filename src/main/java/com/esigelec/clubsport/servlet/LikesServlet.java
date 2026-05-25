package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.dao.DAOFactory;
import com.esigelec.clubsport.dao.LikeDAO;
import com.esigelec.clubsport.model.Like;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API likes sur les actualités — système toggle (like/unlike).
 * L'utilisateur doit être connecté (session requise).
 *
 * GET  /api/likes?idActualite=5
 *      → { count: 12, liked: true/false }
 *      liked = true si l'utilisateur connecté a déjà liké
 *
 * POST /api/likes?idActualite=5
 *      → toggle : like si pas encore liké, unlike sinon
 *      → { count: 13, liked: true }  ou  { count: 11, liked: false }
 *
 * Chemin : src/main/java/fr/esigelec/clubsport/servlet/LikesServlet.java
 */
@WebServlet("/api/likes/*")
public class LikesServlet extends HttpServlet {

    private final Gson    gson = new Gson();
    private final LikeDAO dao  = DAOFactory.getLikeDAO();

    // ── GET — nb likes + statut pour l'utilisateur connecté ──────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String idStr = req.getParameter("idActualite");
        if (idStr == null || idStr.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"idActualite requis\"}");
            return;
        }

        try {
            int idActualite = Integer.parseInt(idStr);
            int count       = dao.countByIdActualite(idActualite);

            // Vérifier si l'utilisateur connecté a liké
            boolean liked = false;
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                int userId = (int) session.getAttribute("userId");
                liked = dao.existsByActualiteAndUser(idActualite, userId);
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("count", count);
            result.put("liked", liked);
            gson.toJson(result, resp.getWriter());

        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"idActualite invalide\"}");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── POST — toggle like/unlike ─────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // Session obligatoire
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Connexion requise pour liker\"}");
            return;
        }

        String idStr = req.getParameter("idActualite");
        if (idStr == null || idStr.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"idActualite requis\"}");
            return;
        }

        try {
            int idActualite   = Integer.parseInt(idStr);
            int idUtilisateur = (int) session.getAttribute("userId");

            boolean dejaLike = dao.existsByActualiteAndUser(idActualite, idUtilisateur);

            if (dejaLike) {
                // Unlike — supprimer le like existant
                dao.deleteByActualiteAndUser(idActualite, idUtilisateur);
            } else {
                // Like — insérer un nouveau like
                Like l = new Like();
                l.setIdActualite(idActualite);
                l.setIdUtilisateur(idUtilisateur);
                dao.insert(l);
            }

            // Retourner le nouveau compteur et le statut
            int newCount = dao.countByIdActualite(idActualite);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("count", newCount);
            result.put("liked", !dejaLike);
            gson.toJson(result, resp.getWriter());

        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"idActualite invalide\"}");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}