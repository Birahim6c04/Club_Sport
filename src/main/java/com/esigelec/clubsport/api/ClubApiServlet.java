package com.esigelec.clubsport.api;

import com.esigelec.clubsport.dao.ClubDAO;
import com.esigelec.clubsport.model.Club;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST pour rechercher les clubs sportifs.
 *
 * Endpoints :
 *   GET /api/clubs?federation=111                              -> tous les clubs FFF
 *   GET /api/clubs?region=Bretagne                             -> tous les clubs en Bretagne
 *   GET /api/clubs?federation=111&region=Bretagne              -> les deux filtres
 *   GET /api/clubs?federation=111&commune=75056&rayon=20       -> recherche par rayon
 *   GET /api/clubs/regions                                     -> liste des régions
 *   GET /api/clubs/communes?q=Paris                            -> autocomplétion commune
 */
@WebServlet(urlPatterns = {"/api/clubs", "/api/clubs/regions", "/api/clubs/communes"})
public class ClubApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ClubDAO clubDAO = new ClubDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // En-têtes : réponse en JSON, accessible depuis le navigateur
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        PrintWriter out = resp.getWriter();
        String path = req.getServletPath();

        try {
            // ============================================================
            // ENDPOINT 1 : /api/clubs/regions
            // Retourne la liste de toutes les régions
            // ============================================================
            if (path.endsWith("/regions")) {
                List<String> regions = clubDAO.listerRegions();
                out.write(gson.toJson(regions));
                return;
            }

            // ============================================================
            // ENDPOINT 2 : /api/clubs/communes?q=Paris
            // Autocomplétion : recherche les communes par début de nom
            // ============================================================
            if (path.endsWith("/communes")) {
                String q = req.getParameter("q");
                if (q == null || q.length() < 2) {
                    envoyerErreur(resp, out, 400, "Paramètre 'q' requis (2 caractères minimum)");
                    return;
                }
                List<String[]> communes = clubDAO.rechercherCommune(q);
                out.write(gson.toJson(communes));
                return;
            }

            // ============================================================
            // ENDPOINT 3 : /api/clubs (recherche principale)
            // ============================================================
            String federation = req.getParameter("federation");
            String region     = req.getParameter("region");
            String commune    = req.getParameter("commune");
            String rayonStr   = req.getParameter("rayon");        
            String codePostal = req.getParameter("codePostal");


            // --- Cas A : recherche par RAYON ---
            // Si on a une commune et un rayon, on fait la recherche géographique
            if (commune != null && !commune.isEmpty() && rayonStr != null) {

                // Conversion du rayon en entier
                int rayon;
                try {
                    rayon = Integer.parseInt(rayonStr);
                } catch (NumberFormatException e) {
                    envoyerErreur(resp, out, 400, "Paramètre 'rayon' invalide");
                    return;
                }

                // Vérifie que le rayon est autorisé (10, 20, 50 ou 100)
                if (rayon != 10 && rayon != 20 && rayon != 50 && rayon != 100) {
                    envoyerErreur(resp, out, 400, "Rayon doit être 10, 20, 50 ou 100 km");
                    return;
                }

                List<Club> clubs = clubDAO.rechercherParRayon(federation, commune, rayon);
                out.write(gson.toJson(clubs));
                return;
            }

            // --- Cas B : recherche par FEDERATION et/ou REGION ---
            // Au moins un des deux critères doit être renseigné
            if ((federation == null || federation.isEmpty())
                && (region == null || region.isEmpty())) {
                envoyerErreur(resp, out, 400,
                    "Au moins un critère est requis : 'federation', 'region', ou ('commune' + 'rayon')");
                return;
            }

            List<Club> clubs = clubDAO.rechercher(federation, region,codePostal);
            out.write(gson.toJson(clubs));

        } catch (IllegalArgumentException e) {
            // Erreur côté utilisateur (ex : commune introuvable)
            envoyerErreur(resp, out, 400, e.getMessage());

        } catch (Exception e) {
            // Erreur serveur (ex : problème SQL)
            e.printStackTrace();
            envoyerErreur(resp, out, 500, "Erreur serveur : " + e.getMessage());
        }
    }

    /**
     * Méthode pour envoyer une erreur JSON.
     */
    private void envoyerErreur(HttpServletResponse resp, PrintWriter out, int statut, String message) {
        resp.setStatus(statut);
        Map<String, String> erreur = new HashMap<>();
        erreur.put("error", message);
        out.write(gson.toJson(erreur));
    }

    /**
     * Permet aussi les requêtes POST (en redirigeant vers doGet).
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}