package com.esigelec.clubsport.controller;

import java.io.File;
import java.io.IOException;

import com.esigelec.clubsport.dao.ProfilDAO;
import com.esigelec.clubsport.model.Profil;
import com.esigelec.clubsport.model.Utilisateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/profil")
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 2
)
public class ProfilServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String DOSSIER_UPLOAD = "/app/uploads";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("utilisateur") == null) {
                response.sendRedirect("connexion");
                return;
            }

            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

            ProfilDAO dao = new ProfilDAO();
            Profil profil = dao.chercherProfilParUtilisateur(utilisateur.getId());

            request.setAttribute("profil", profil);
            request.getRequestDispatcher("/WEB-INF/jsp/profilElus.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erreur", "Erreur lors du chargement du profil.");
            request.getRequestDispatcher("/WEB-INF/jsp/profilElus.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");

            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("utilisateur") == null) {
                response.sendRedirect("connexion");
                return;
            }

            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

            ProfilDAO dao = new ProfilDAO();
            Profil profil = dao.chercherProfilParUtilisateur(utilisateur.getId());

            if (profil == null) {
                profil = new Profil();
                profil.setIdUtilisateur(utilisateur.getId());
            }

            Part photoPart = request.getPart("photoProfil");

            if (photoPart != null && photoPart.getSize() > 0) {
                String typeFichier = photoPart.getContentType();

                if (typeFichier == null || !typeFichier.startsWith("image/")) {
                    request.setAttribute("erreur", "Veuillez sélectionner uniquement un fichier image.");
                    request.setAttribute("profil", profil);
                    request.getRequestDispatcher("/WEB-INF/jsp/profilElus.jsp").forward(request, response);
                    return;
                }

                String nomOriginal = photoPart.getSubmittedFileName();

                if (nomOriginal == null || nomOriginal.trim().isEmpty()) {
                    request.setAttribute("erreur", "Nom de fichier invalide.");
                    request.setAttribute("profil", profil);
                    request.getRequestDispatcher("/WEB-INF/jsp/profilElus.jsp").forward(request, response);
                    return;
                }

                nomOriginal = new File(nomOriginal).getName();

                String extension = "";
                int point = nomOriginal.lastIndexOf('.');

                if (point > 0) {
                    extension = nomOriginal.substring(point).toLowerCase();
                }

                if (!extension.equals(".jpg")
                        && !extension.equals(".jpeg")
                        && !extension.equals(".png")
                        && !extension.equals(".gif")
                        && !extension.equals(".webp")) {

                    request.setAttribute("erreur", "Formats acceptés : JPG, JPEG, PNG, GIF ou WEBP.");
                    request.setAttribute("profil", profil);
                    request.getRequestDispatcher("/WEB-INF/jsp/profilElus.jsp").forward(request, response);
                    return;
                }

                File dossier = new File(DOSSIER_UPLOAD);

                if (!dossier.exists()) {
                    dossier.mkdirs();
                }

                String nomFichier = "profil_" + utilisateur.getId() + "_" + System.currentTimeMillis() + extension;
                String cheminComplet = DOSSIER_UPLOAD + File.separator + nomFichier;

                photoPart.write(cheminComplet);
                profil.setPhotoProfil(nomFichier);
            }

            profil.setTelephone(nettoyerTexte(request.getParameter("telephone")));
            profil.setAdresse(nettoyerTexte(request.getParameter("adresse")));
            profil.setFonction(nettoyerTexte(request.getParameter("fonction")));
            profil.setCommune(nettoyerTexte(request.getParameter("commune")));
            profil.setDepartement(nettoyerTexte(request.getParameter("departement")));
            profil.setRegion(nettoyerTexte(request.getParameter("region")));

            if (profil.getIdProfil() == 0) {
                dao.ajouterProfil(profil);
            } else {
                dao.modifierProfil(profil);
            }

            response.sendRedirect(request.getContextPath() + "/profil");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erreur", "Erreur lors de l'enregistrement du profil.");
            request.getRequestDispatcher("/WEB-INF/jsp/profilElus.jsp").forward(request, response);
        }
    }

    private String nettoyerTexte(String texte) {
        if (texte == null) {
            return "";
        }

        return texte
                .trim()
                .replace("<", "")
                .replace(">", "")
                .replace("\"", "")
                .replace("'", "");
    }
}