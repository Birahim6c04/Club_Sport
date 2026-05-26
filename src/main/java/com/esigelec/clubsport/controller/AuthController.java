package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.model.Utilisateur;
import com.esigelec.clubsport.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(urlPatterns = {"/connexion", "/inscription", "/deconnexion"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize       = 10 * 1024 * 1024,
    maxRequestSize    = 15 * 1024 * 1024
)
public class AuthController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AuthService authService = new AuthService();
    private static final String DOSSIER_UPLOAD = "/app/uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if (path.equals("/deconnexion")) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/accueil");
            return;
        }

        if (path.equals("/inscription")) {
            req.getRequestDispatcher("/WEB-INF/jsp/inscription.jsp").forward(req, resp);
            return;
        }

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

    private void traiterConnexion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String login = req.getParameter("login");
        String motDePasse = req.getParameter("motDePasse");
        String ip = req.getRemoteAddr();  // Recupere l'IP du client

        try {
            Utilisateur u = authService.connecter(login, motDePasse);

            // LOG : connexion reussie
            authService.loggerConnexion(login, ip, true);

            HttpSession session = req.getSession(true);
            session.setAttribute("utilisateur", u);
            session.setAttribute("role", u.getRole());

            if (u.getRole().equals("ELU")) {
                req.getRequestDispatcher("/WEB-INF/jsp/elus.jsp").forward(req, resp);
            } else if (u.getRole().equals("ADMIN")) {
                resp.sendRedirect(req.getContextPath() + "/admin");
            } else if (u.getRole().equals("CLUB")) {
                resp.sendRedirect(req.getContextPath() + "/club");
            } else {
                resp.sendRedirect(req.getContextPath() + "/accueil");
            }

        } catch (IllegalArgumentException e) {
            // LOG : connexion echouee
            authService.loggerConnexion(login, ip, false);

            req.setAttribute("erreur", e.getMessage());
            req.setAttribute("login", login);
            req.getRequestDispatcher("/WEB-INF/jsp/connexion.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            authService.loggerConnexion(login, ip, false);

            req.setAttribute("erreur", "Erreur serveur");
            req.getRequestDispatcher("/WEB-INF/jsp/connexion.jsp").forward(req, resp);
        }
    }

    private void traiterInscription(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String login = req.getParameter("login");
        String motDePasse = req.getParameter("motDePasse");
        String email = req.getParameter("email");
        String nom = req.getParameter("nom");
        String prenom = req.getParameter("prenom");
        String role = req.getParameter("role");

        try {
            Part filePart = req.getPart("pieceJointe");

            if (filePart == null || filePart.getSize() == 0) {
                throw new IllegalArgumentException("Veuillez joindre un fichier justificatif");
            }

            String nomOriginal = filePart.getSubmittedFileName();
            String extension = "";
            int point = nomOriginal.lastIndexOf('.');
            if (point > 0) {
                extension = nomOriginal.substring(point).toLowerCase();
            }

            if (!extension.equals(".pdf") && !extension.equals(".jpg")
                && !extension.equals(".jpeg") && !extension.equals(".png")) {
                throw new IllegalArgumentException("Format de fichier non autorise (PDF, JPG, PNG uniquement)");
            }

            File dossier = new File(DOSSIER_UPLOAD);
            if (!dossier.exists()) {
                dossier.mkdirs();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String nomFichier = sdf.format(new Date()) + "_" + login + "_" + nomOriginal;
            nomFichier = nomFichier.replaceAll("[^a-zA-Z0-9._-]", "_");

            String cheminComplet = DOSSIER_UPLOAD + "/" + nomFichier;
            filePart.write(cheminComplet);

            authService.inscrire(login, motDePasse, email, nom, prenom, role, nomFichier);

            req.setAttribute("succes", "Inscription envoyee ! En attente de validation par un administrateur.");
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
            req.setAttribute("erreur", "Erreur serveur : " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/inscription.jsp").forward(req, resp);
        }
    }
}