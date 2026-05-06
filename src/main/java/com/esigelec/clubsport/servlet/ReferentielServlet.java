package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.utils.DBConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * GET /api/federations → [{code, nom}, ...]
 * GET /api/regions     → ["Bretagne", ...]
 *
 * Tables (schéma réel) :
 *   federation → code_federation, nom_federation
 *   commune    → region
 */
@WebServlet(urlPatterns = {"/api/federations", "/api/regions"})
public class ReferentielServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        try (Connection conn = DBConnection.get()) {
            if ("/api/federations".equals(req.getServletPath())) {
                gson.toJson(getFederations(conn), resp.getWriter());
            } else {
                gson.toJson(getRegions(conn), resp.getWriter());
            }
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private List<Map<String, String>> getFederations(Connection conn) throws SQLException {
        String sql = "SELECT code_federation, nom_federation FROM federation ORDER BY nom_federation";
        List<Map<String, String>> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("code", rs.getString("code_federation"));
                row.put("nom",  rs.getString("nom_federation"));
                list.add(row);
            }
        }
        return list;
    }

    private List<String> getRegions(Connection conn) throws SQLException {
        String sql = "SELECT DISTINCT region FROM commune WHERE region IS NOT NULL ORDER BY region";
        List<String> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString("region"));
        }
        return list;
    }
}