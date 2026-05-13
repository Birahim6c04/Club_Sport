package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.model.Club;
import com.esigelec.clubsport.service.ClubService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/accueil", "/clubs/rechercher"})
public class ClubController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ClubService service = new ClubService();

    // GET : affiche la page d'accueil avec le formulaire vide
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            List<String> regions = service.listerRegions();
            req.setAttribute("regions", regions);
        } catch (Exception e) {
            req.setAttribute("erreur", "Erreur lors du chargement des régions");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/accueil.jsp").forward(req, resp);
    }

    // POST : traite le formulaire de recherche
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String mode       = req.getParameter("mode");
        String federation = req.getParameter("federation");
        String region     = req.getParameter("region");
        String commune    = req.getParameter("commune");
        String rayonStr   = req.getParameter("rayon");
        String codePostal = req.getParameter("codePostal");

        try {
            List<Club> clubs;

            if ("rayon".equals(mode)) {
                int rayon = Integer.parseInt(rayonStr);
                clubs = service.rechercherParRayon(federation, commune, rayon);
                req.setAttribute("modeRecherche", "rayon");
            } else {
            	clubs = service.rechercher(federation, region, codePostal);
            	req.setAttribute("modeRecherche", "zone");
            }

            req.setAttribute("clubs", clubs);
            req.setAttribute("nbResultats", clubs.size());

            // Pour pré-remplir le formulaire ET pour la carte 
            req.setAttribute("federationChoisie", federation);
            req.setAttribute("regionChoisie", region);
            req.setAttribute("communeChoisie", commune);
            req.setAttribute("rayonChoisi", rayonStr);
            req.setAttribute("codePostalChoisi", codePostal);

        } catch (IllegalArgumentException e) {
            req.setAttribute("erreur", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("erreur", "Erreur serveur : " + e.getMessage());
        }

        try {
            req.setAttribute("regions", service.listerRegions());
        } catch (Exception e) {
            // ignore
        }

        req.getRequestDispatcher("/WEB-INF/jsp/accueil.jsp").forward(req, resp);
    }
}