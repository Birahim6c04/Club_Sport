package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.model.Utilisateur;
import com.esigelec.clubsport.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/connexion", "/inscription", "/deconnexion"})
public class AuthController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // Déconnexion
        if (path.equals("/deconnexion")) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/accueil");
            return;
        }

        // Page d'inscription
        if (path.equals("/inscription")) {
            req.getRequestDispatcher("/WEB-INF/jsp/inscription.jsp").forward(req, resp);
            return;
        }

        // Par défaut : page de connexion
        req.getRequestDispatcher("/WEB-INF/jsp/connexion.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if (path.equals("/connexion")) {
            traiterConnexion(req, resp);
        } else if (path.equals("/inscription")) {
            traiterInscription(req, resp);
        }
    }

    // Traite le formulaire de connexion
    private void traiterConnexion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String login = req.getParameter("login");
        String motDePasse = req.getParameter("motDePasse");

        try {
            Utilisateur u = authService.connecter(login, motDePasse);

            // Création de la session
            HttpSession session = req.getSession(true);
            session.setAttribute("utilisateur", u);
            session.setAttribute("role", u.getRole());

            // Redirection selon le rôle
            String redirect = req.getContextPath() + "/accueil";
            if (u.getRole().equals("ADMIN")) {
                redirect = req.getContextPath() + "/admin";
            } else if (u.getRole().equals("ELU")) {
                redirect = req.getContextPath() + "/elu";
            } else if (u.getRole().equals("CLUB")) {
                redirect = req.getContextPath() + "/club";
            }
            resp.sendRedirect(redirect);

        } catch (IllegalArgumentException e) {
            req.setAttribute("erreur", e.getMessage());
            req.setAttribute("login", login);
            req.getRequestDispatcher("/WEB-INF/jsp/connexion.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("erreur", "Erreur serveur");
            req.getRequestDispatcher("/WEB-INF/jsp/connexion.jsp").forward(req, resp);
        }
    }

    // Traite le formulaire d'inscription
    private void traiterInscription(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String login = req.getParameter("login");
        String motDePasse = req.getParameter("motDePasse");
        String email = req.getParameter("email");
        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String role = req.getParameter("role");

        try {
            authService.inscrire(login, motDePasse, email, nom, prenom, role);

            req.setAttribute("succes", "Inscription reussie ! Vous pouvez vous connecter.");
            req.getRequestDispatcher("/WEB-INF/jsp/connexion.jsp").forward(req, resp);

        } catch (IllegalArgumentException e) {
            req.setAttribute("erreur", e.getMessage());
            req.setAttribute("login", login);
            req.setAttribute("email", email);
            req.setAttribute("nom", nom);
            req.setAttribute("prenom", prenom);
            req.setAttribute("role", role);
            req.getRequestDispatcher("/WEB-INF/jsp/inscription.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("erreur", "Erreur serveur");
            req.getRequestDispatcher("/WEB-INF/jsp/inscription.jsp").forward(req, resp);
        }
    }
}