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
@MultipartConfig
public class ProfilServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

            if (utilisateur == null) {
                response.sendRedirect("connexion");
                return;
            }

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
            HttpSession session = request.getSession();
            Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

            if (utilisateur == null) {
                response.sendRedirect("connexion");
                return;
            }

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

                String cheminUpload = getServletContext().getRealPath("/uploads");
                File dossier = new File(cheminUpload);

                if (!dossier.exists()) {
                    dossier.mkdirs();
                }

                String nomFichier = "profil_" + utilisateur.getId() + "_" + System.currentTimeMillis() + extension;
                String cheminComplet = cheminUpload + File.separator + nomFichier;

                photoPart.write(cheminComplet);
                profil.setPhotoProfil(nomFichier);
            }

            profil.setTelephone(request.getParameter("telephone"));
            profil.setAdresse(request.getParameter("adresse"));
            profil.setFonction(request.getParameter("fonction"));
            profil.setCommune(request.getParameter("commune"));
            profil.setDepartement(request.getParameter("departement"));
            profil.setRegion(request.getParameter("region"));

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
}