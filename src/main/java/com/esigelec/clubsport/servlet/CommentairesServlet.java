package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.dao.CommentaireDAO;
import com.esigelec.clubsport.dao.DAOFactory;
import com.esigelec.clubsport.model.Commentaire;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.SQLException;
import java.util.Map;

/**
 * API commentaires sur les actualités.
 * L'utilisateur doit être connecté (session requise).
 *
 * GET    /api/commentaires?idActualite=5   → liste des commentaires
 * POST   /api/commentaires?idActualite=5   → ajouter un commentaire
 *        Body : { contenu }
 * DELETE /api/commentaires/{idCommentaire} → supprimer son commentaire
 *
 * Chemin : src/main/java/com/esigelec/clubsport/servlet/CommentairesServlet.java
 */
@WebServlet("/api/commentaires/*")
public class CommentairesServlet extends HttpServlet {

    private final Gson           gson = new Gson();
    private final CommentaireDAO dao  = DAOFactory.getCommentaireDAO();

    // ── GET — liste des commentaires d'une actualité ──────────────────
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
            gson.toJson(dao.findByIdActualite(idActualite), resp.getWriter());
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"idActualite invalide\"}");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── POST — ajouter un commentaire ────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // Vérification session utilisateur
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Connexion requise pour commenter\"}");
            return;
        }

        String idStr = req.getParameter("idActualite");
        if (idStr == null || idStr.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"idActualite requis\"}");
            return;
        }

        Map<?, ?> body   = gson.fromJson(lireBody(req), Map.class);
        String contenu   = str(body, "contenu");

        if (contenu.isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"Le contenu est obligatoire\"}");
            return;
        }

        try {
            int idActualite    = Integer.parseInt(idStr);
            int idUtilisateur  = (int) session.getAttribute("userId");

            Commentaire c = new Commentaire();
            c.setIdActualite(idActualite);
            c.setIdUtilisateur(idUtilisateur);
            c.setContenu(contenu);

            dao.insert(c);

            // Recharger avec le login pour l'affichage immédiat
            Commentaire complet = dao.findById(c.getIdCommentaire());
            resp.setStatus(201);
            gson.toJson(complet, resp.getWriter());

        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"idActualite invalide\"}");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── DELETE — supprimer son propre commentaire ─────────────────────
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        // Vérification session
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"Connexion requise\"}");
            return;
        }

        Integer id = extractId(req);
        if (id == null) { resp.setStatus(400); return; }

        try {
            Commentaire c = dao.findById(id);
            if (c == null) { resp.setStatus(404); return; }

            // Vérifier que l'utilisateur supprime son propre commentaire
            int userId = (int) session.getAttribute("userId");
            if (c.getIdUtilisateur() != userId) {
                resp.setStatus(403);
                resp.getWriter().write("{\"error\":\"Action non autorisée\"}");
                return;
            }

            dao.delete(id);
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