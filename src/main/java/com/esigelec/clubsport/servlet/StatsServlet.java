package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.dao.DAOFactory;
import com.esigelec.clubsport.dao.StatsDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * GET /api/stats?federation=&region=&top=10
 *
 * Chemin : src/main/java/com/esigelec/clubsport/servlet/StatsServlet.java
 */
@WebServlet("/api/stats")
public class StatsServlet extends HttpServlet {

    private final Gson     gson = new Gson();
    private final StatsDAO dao  = DAOFactory.getStatsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String federation = clean(req.getParameter("federation"));
        String region     = clean(req.getParameter("region"));
        int    top        = toInt(req.getParameter("top"), 10);
        if (top < 1 || top > 100) top = 10;

        try {
            gson.toJson(dao.buildStats(federation, region, top), resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String clean(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private int toInt(String s, int def) {
        try { return (s != null && !s.isBlank()) ? Integer.parseInt(s) : def; }
        catch (NumberFormatException e) { return def; }
    }
}