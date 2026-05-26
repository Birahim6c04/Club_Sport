package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.dao.EspaceClubDAO;
import com.esigelec.clubsport.model.EspaceClub;
import com.esigelec.clubsport.model.Utilisateur;

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
import java.util.List;

@WebServlet(urlPatterns = {"/club", "/club/editer", "/club/public", "/clubs"})
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class ClubEspaceController extends HttpServlet {

    private EspaceClubDAO dao = new EspaceClubDAO();
    private String dossierUpload = "/app/uploads";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String url = req.getServletPath();

        try {
            // Annuaire public
            if (url.equals("/clubs")) {
                List<EspaceClub> clubs = dao.listerTous();
                req.setAttribute("clubs", clubs);
                req.getRequestDispatcher("/WEB-INF/jsp/club_annuaire.jsp").forward(req, resp);
                return;
            }

            // Page publique
            if (url.equals("/club/public")) {
                String slug = req.getParameter("slug");
                EspaceClub e = dao.trouverParSlug(slug);
                if (e == null) {
                    resp.sendError(404, "Club introuvable");
                    return;
                }
                req.setAttribute("espace", e);
                req.getRequestDispatcher("/WEB-INF/jsp/club_public.jsp").forward(req, resp);
                return;
            }

            // Espace privé (CLUB connecté)
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("utilisateur") == null) {
                resp.sendRedirect(req.getContextPath() + "/connexion");
                return;
            }

            Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
            if (!u.getRole().equals("CLUB")) {
                resp.sendError(403, "Acces reserve aux clubs");
                return;
            }

            EspaceClub espace = dao.trouverParResponsable(u.getId());

            if (url.equals("/club/editer")) {
                req.setAttribute("espace", espace);
                req.getRequestDispatcher("/WEB-INF/jsp/club_editer.jsp").forward(req, resp);
                return;
            }

            // Dashboard
            req.setAttribute("espace", espace);
            req.getRequestDispatcher("/WEB-INF/jsp/club_dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("erreur", "Erreur serveur");
            req.getRequestDispatcher("/WEB-INF/jsp/club_dashboard.jsp").forward(req, resp);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            resp.sendRedirect(req.getContextPath() + "/connexion");
            return;
        }

        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (!u.getRole().equals("CLUB")) {
            resp.sendError(403);
            return;
        }

        try {
            String nomClub      = req.getParameter("nomClub");
            String description  = req.getParameter("description");
            String actualites   = req.getParameter("actualites");
            String horaires     = req.getParameter("horaires");
            String cotisationStr = req.getParameter("montantCotisation");
            String contactTel   = req.getParameter("contactTel");
            String contactEmail = req.getParameter("contactEmail");
            String adresse      = req.getParameter("adresse");

            double montantCotisation = 0;
            if (cotisationStr != null && !cotisationStr.isEmpty()) {
                montantCotisation = Double.parseDouble(cotisationStr);
            }

            EspaceClub espace = dao.trouverParResponsable(u.getId());
            boolean creation = (espace == null);

            if (creation) {
                espace = new EspaceClub();
                espace.setIdResponsable(u.getId());

                // Générer le slug
                String slug = nomClub.toLowerCase()
                                     .replaceAll("[éèêë]", "e")
                                     .replaceAll("[àâä]", "a")
                                     .replaceAll("[ùûü]", "u")
                                     .replaceAll("[ôö]", "o")
                                     .replaceAll("[îï]", "i")
                                     .replaceAll("[ç]", "c")
                                     .replaceAll("[^a-z0-9]", "");

                if (dao.slugExiste(slug)) {
                    slug = slug + System.currentTimeMillis();
                }
                espace.setSlug(slug);
            }

            espace.setNomClub(nomClub);
            espace.setDescription(description);
            espace.setActualites(actualites);
            espace.setHoraires(horaires);
            espace.setMontantCotisation(montantCotisation);
            espace.setContactTel(contactTel);
            espace.setContactEmail(contactEmail);
            espace.setAdresse(adresse);

            // Gestion de la photo
            Part photoPart = req.getPart("photo");
            if (photoPart != null && photoPart.getSize() > 0) {
                String nomOriginal = photoPart.getSubmittedFileName();
                String extension = nomOriginal.substring(nomOriginal.lastIndexOf('.')).toLowerCase();

                if (!extension.equals(".jpg") && !extension.equals(".jpeg")
                    && !extension.equals(".png")) {
                    throw new Exception("Format photo non autorise (JPG ou PNG)");
                }

                File dossier = new File(dossierUpload);
                if (!dossier.exists()) dossier.mkdirs();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String nomPhoto = "photo_" + sdf.format(new Date()) + "_" + espace.getSlug() + extension;
                photoPart.write(dossierUpload + "/" + nomPhoto);

                espace.setPhoto(nomPhoto);
            }

            if (creation) {
                dao.creer(espace);
            } else {
                dao.modifier(espace);
            }

            req.setAttribute("succes", "Espace mis a jour avec succes !");
            req.setAttribute("espace", dao.trouverParResponsable(u.getId()));
            req.getRequestDispatcher("/WEB-INF/jsp/club_dashboard.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("erreur", "Erreur : " + e.getMessage());

            req.getRequestDispatcher("/WEB-INF/jsp/club_editer.jsp").forward(req, resp);
        }
    }
}