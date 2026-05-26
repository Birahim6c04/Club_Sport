package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.dao.InteractionDAO;
import com.esigelec.clubsport.model.Utilisateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/club/like", "/club/commenter"})
public class InteractionController extends HttpServlet {

    private InteractionDAO dao = new InteractionDAO();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Il faut etre connecte
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            resp.sendRedirect(req.getContextPath() + "/connexion");
            return;
        }

        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        String url = req.getServletPath();
     // Seul le grand public peut liker / commenter
        if (!u.getRole().equals("PUBLIC")) {
            resp.sendError(403, "Action reservee au grand public");
            return;
        }
        String slug = req.getParameter("slug");
        int idEspace = Integer.parseInt(req.getParameter("idEspace"));

        try {
            if (url.equals("/club/like")) {
                dao.toggleLike(idEspace, u.getId());
            } else if (url.equals("/club/commenter")) {
                String contenu = req.getParameter("contenu");
                if (contenu != null && !contenu.trim().isEmpty()) {
                    dao.ajouterCommentaire(idEspace, u.getId(),
                            u.getPrenom() + " " + u.getNom(), contenu.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retour a la page publique
        resp.sendRedirect(req.getContextPath() + "/club/public?slug=" + slug);
    }
}
