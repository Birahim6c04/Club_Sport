package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.dao.DAOFactory;
import com.esigelec.clubsport.dao.ReferentielDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * GET /api/federations → [{code, nom}, ...]
 * GET /api/regions     → ["Bretagne", ...]
 *
 * Chemin : src/main/java/com/esigelec/clubsport/servlet/ReferentielServlet.java
 */
@WebServlet(urlPatterns = {"/api/federations", "/api/regions"})
public class ReferentielServlet extends HttpServlet {

    private final Gson            gson  = new Gson();
    private final ReferentielDAO  dao   = DAOFactory.getReferentielDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        try {
            if ("/api/federations".equals(req.getServletPath())) {
                gson.toJson(dao.findAllFederations(), resp.getWriter());
            } else {
                gson.toJson(dao.findAllRegions(), resp.getWriter());
            }
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}