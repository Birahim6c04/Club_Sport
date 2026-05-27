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
 * Contrôleur MVC pour les likes.
 *
 * GET  /mvc/likes?idActualite=5   → JSON { count, liked }
 * POST /mvc/likes  idActualite    → toggle like/unlike, redirect Referer
 */
@WebServlet("/mvc/likes/*")
public class LikeController extends HttpServlet {

    private final Gson    gson = new Gson();
    private final LikeDAO dao  = DAOFactory.getLikeDAO();

    // ── GET — compteur + statut (pour le JS) ─────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        String idStr = req.getParameter("idActualite");
        if (idStr == null || idStr.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"idActualite requis\"}");
            return;
        }
        try {
            int idActualite = Integer.parseInt(idStr);
            int count       = dao.countByIdActualite(idActualite);
            boolean liked   = false;
            HttpSession session = req.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                liked = dao.existsByActualiteAndUser(idActualite,
                        (int) session.getAttribute("userId"));
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

    // ── POST — toggle via formulaire HTML ────────────────────────────
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

        String idStr = req.getParameter("idActualite");
        if (idStr == null || idStr.isBlank()) {
            resp.sendRedirect(redirectUrl);
            return;
        }

        try {
            int idActualite   = Integer.parseInt(idStr);
            int idUtilisateur = (int) session.getAttribute("userId");

            if (dao.existsByActualiteAndUser(idActualite, idUtilisateur)) {
                dao.deleteByActualiteAndUser(idActualite, idUtilisateur);
            } else {
                Like l = new Like();
                l.setIdActualite(idActualite);
                l.setIdUtilisateur(idUtilisateur);
                dao.insert(l);
            }
        } catch (Exception ignored) { }

        resp.sendRedirect(redirectUrl);
    }

    private String referer(HttpServletRequest req) {
        String ref = req.getHeader("Referer");
        return (ref != null && !ref.isBlank()) ? ref
                : req.getContextPath() + "/actualites.html";
    }
}
