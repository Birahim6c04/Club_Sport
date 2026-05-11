package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.dao.LicenceStatsDAO;
import com.esigelec.clubsport.dao.IndicateursDAO;
import com.esigelec.clubsport.model.StatDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/indicateurs")
public class IndicateursServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String region = request.getParameter("region");
            String departement = request.getParameter("departement");
            String federation = request.getParameter("federation");
            String codeCommune = request.getParameter("codeCommune");

            LicenceStatsDAO licenceDao = new LicenceStatsDAO();
            IndicateursDAO indicateursDao = new IndicateursDAO();

            // Listes pour les filtres
            List<String> regions = licenceDao.listerRegions();
            List<String> departements = licenceDao.listerDepartementsParRegion(region);
            List<String[]> communes = licenceDao.listerCommunesParFiltres(region, departement);
            List<String> federations = licenceDao.listerFederationsParFiltres(region, departement, codeCommune);
            
            if (federation != null && !federation.isEmpty() && !federations.contains(federation)) {
                federation = "";
            }

            // Données des graphiques
            List<StatDTO> repartitionAge =
                    indicateursDao.getRepartitionAge(region, departement, federation, codeCommune);

            List<StatDTO> clubsFederations =
                    indicateursDao.getClubsParFederation(region, departement, codeCommune);

            List<StatDTO> rapportAgeClubs =
                    indicateursDao.getRapportAgeClubs(region, departement, federation, codeCommune);

            // Envoi des filtres vers la JSP
            request.setAttribute("regions", regions);
            request.setAttribute("departements", departements);
            request.setAttribute("communes", communes);
            request.setAttribute("federations", federations);

            // Garder les valeurs sélectionnées
            request.setAttribute("region", region);
            request.setAttribute("departement", departement);
            request.setAttribute("codeCommune", codeCommune);
            request.setAttribute("federation", federation);

            // Envoi des statistiques
            request.setAttribute("repartitionAge", repartitionAge);
            request.setAttribute("clubsFederations", clubsFederations);
            request.setAttribute("rapportAgeClubs", rapportAgeClubs);

            request.getRequestDispatcher("/indicateurs.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("erreur", "Erreur lors du chargement des indicateurs.");
            request.getRequestDispatcher("/indicateurs.jsp")
                    .forward(request, response);
        }
    }
}