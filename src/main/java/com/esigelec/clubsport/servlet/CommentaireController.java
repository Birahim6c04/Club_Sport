package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.dao.CommentaireDAO;
import com.esigelec.clubsport.dao.DAOFactory;
import com.esigelec.clubsport.model.Commentaire;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Contrôleur MVC pour les commentaires.
 *
 * GET  /mvc/commentaires?idActualite=5   → JSON (liste pour le JS)
 * POST /mvc/commentaires  action=ajouter  idActualite contenu  → redirect Referer
 * POST /mvc/commentaires  action=supprimer idCommentaire        → redirect Referer
 */
@WebServlet("/mvc/commentaires/*")
public class CommentaireController extends HttpServlet {

    private final Gson           gson = new Gson();
    private final CommentaireDAO dao  = DAOFactory.getCommentaireDAO();

    // ── GET — liste JSON pour la vue JS ──────────────────────────────
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
            gson.toJson(dao.findByIdActualite(Integer.parseInt(idStr)), resp.getWriter());
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"idActualite invalide\"}");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── POST — formulaire HTML (ajouter / supprimer) ─────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        req.setCharacterEncoding("UTF-8");
        String action      = req.getParameter("action");
        String redirectUrl = referer(req);

        if ("supprimer".equals(action)) {
            supprimer(req, resp, redirectUrl);
        } else {
            ajouter(req, resp, redirectUrl);
        }
    }

    private void ajouter(HttpServletRequest req, HttpServletResponse resp,
                          String redirectUrl) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(redirectUrl);
            return;
        }
        String idStr   = req.getParameter("idActualite");
        String contenu = req.getParameter("contenu");
        if (idStr == null || contenu == null || contenu.isBlank()) {
            resp.sendRedirect(redirectUrl);
            return;
        }
        try {
            Commentaire c = new Commentaire();
            c.setIdActualite(Integer.parseInt(idStr));
            c.setIdUtilisateur((int) session.getAttribute("userId"));
            c.setContenu(contenu.trim());
            dao.insert(c);
        } catch (Exception ignored) { }
        resp.sendRedirect(redirectUrl);
    }

    private void supprimer(HttpServletRequest req, HttpServletResponse resp,
                            String redirectUrl) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(redirectUrl);
            return;
        }
        String idStr = req.getParameter("idCommentaire");
        if (idStr == null) { resp.sendRedirect(redirectUrl); return; }
        try {
            int id     = Integer.parseInt(idStr);
            int userId = (int) session.getAttribute("userId");
            Commentaire c = dao.findById(id);
            if (c != null && c.getIdUtilisateur() == userId) {
                dao.delete(id);
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
