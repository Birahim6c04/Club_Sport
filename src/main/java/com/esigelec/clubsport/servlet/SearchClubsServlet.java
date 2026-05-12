package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.dao.DAOFactory;
import com.esigelec.clubsport.dao.SearchClubsDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * GET /api/search?federation=&region=&commune=&lat=&lng=&rayon=
 *
 * Chemin : src/main/java/com/esigelec/clubsport/servlet/SearchClubsServlet.java
 */
@WebServlet("/api/search")
public class SearchClubsServlet extends HttpServlet {

    private final Gson           gson = new Gson();
    private final SearchClubsDAO dao  = DAOFactory.getSearchClubsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String federation = clean(req.getParameter("federation"));
        String region     = clean(req.getParameter("region"));
        String commune    = clean(req.getParameter("commune"));
        Double lat        = toDouble(req.getParameter("lat"));
        Double lng        = toDouble(req.getParameter("lng"));
        Double rayon      = toDouble(req.getParameter("rayon"));

        try {
            gson.toJson(
                dao.search(federation, region, commune, lat, lng, rayon),
                resp.getWriter()
            );
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String clean(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private Double toDouble(String s) {
        try { return (s != null && !s.isBlank()) ? Double.parseDouble(s) : null; }
        catch (NumberFormatException e) { return null; }
    }
}