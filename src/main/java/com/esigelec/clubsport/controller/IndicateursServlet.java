package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.dao.LicenceStatsDAO;
import com.esigelec.clubsport.model.StatDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/indicateurs")
public class IndicateursServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String region = request.getParameter("region");
            String departement = request.getParameter("departement");
            String federation = request.getParameter("federation");
            String codeCommune = request.getParameter("codeCommune");

            LicenceStatsDAO dao = new LicenceStatsDAO();

            List<StatDTO> repartitionAge =
                    dao.getRepartitionAge(region, departement, federation, codeCommune);

            List<StatDTO> clubsFederations =
                    dao.getClubsParFederation(region, departement, codeCommune);

            request.setAttribute("repartitionAge", repartitionAge);
            request.setAttribute("clubsFederations", clubsFederations);

            request.getRequestDispatcher("/indicateurs.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}