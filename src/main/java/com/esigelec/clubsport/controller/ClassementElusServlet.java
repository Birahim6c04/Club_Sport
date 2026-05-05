package com.esigelec.clubsport.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esigelec.clubsport.dao.LicenceStatsDAO;
import com.esigelec.clubsport.model.ClassementCommune;

@WebServlet("/LeClassement")
public class ClassementElusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            LicenceStatsDAO dao = new LicenceStatsDAO();

            String region = request.getParameter("region");
            String departement = request.getParameter("departement");
            String federation = request.getParameter("federation");

            List<String> regions = dao.listerRegions();
            List<String> departements = dao.listerDepartementsParRegion(region);
            List<String> federations = dao.listerFederationsParFiltres(region, departement, null);

            if (federation != null && !federation.isEmpty() && !federations.contains(federation)) {
                federation = "";
            }

            List<ClassementCommune> classementCommunes =
                    dao.getClassementCommunes(region, departement, federation);

            StringBuilder labelsCommunes = new StringBuilder("[");
            StringBuilder dataPourcentages = new StringBuilder("[");

            int limite = Math.min(classementCommunes.size(), 10);

            for (int i = 0; i < limite; i++) {
                ClassementCommune commune = classementCommunes.get(i);

                String nomCommune = commune.getNomCommune();

                if (nomCommune == null || nomCommune.isEmpty()) {
                    nomCommune = "Commune inconnue";
                }

                labelsCommunes.append("\"")
                        .append(nomCommune.replace("\\", "\\\\").replace("\"", "\\\""))
                        .append("\"");

                dataPourcentages.append(commune.getPourcentage());

                if (i < limite - 1) {
                    labelsCommunes.append(",");
                    dataPourcentages.append(",");
                }
            }

            labelsCommunes.append("]");
            dataPourcentages.append("]");

            request.setAttribute("regions", regions);
            request.setAttribute("departements", departements);
            request.setAttribute("federations", federations);

            request.setAttribute("region", region);
            request.setAttribute("departement", departement);
            request.setAttribute("federation", federation);

            request.setAttribute("classementCommunes", classementCommunes);

            request.setAttribute("labelsCommunes", labelsCommunes.toString());
            request.setAttribute("dataPourcentages", dataPourcentages.toString());

            request.getRequestDispatcher("/ClassementElus.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute("erreur", "Erreur lors du chargement du classement élus.");
            request.getRequestDispatcher("/ClassementElus.jsp").forward(request, response);
        }
    }
}