package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.utils.DBConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Données pour la page de statistiques (tâche 1 — version JS/Chart.js).
 *
 * GET /api/stats?federation=&region=&top=10
 *
 * Retourne :
 * {
 *   "communes": [{ nom, total, femmes, hommes }, ...],  ← top N
 *   "totalFemmes": 123456,
 *   "totalHommes":  98765
 * }
 *
 * Tables utilisées (schéma réel) :
 *   commune       → nom_commune, region
 *   licence_stats → total + colonnes f_* / h_*
 *   federation    → code_federation
 */
@WebServlet("/api/stats")
public class StatsServlet extends HttpServlet {

    private final Gson gson = new Gson();

    // Somme de toutes les colonnes femmes
    private static final String SUM_F =
        "(ls.f_1_4+ls.f_5_9+ls.f_10_14+ls.f_15_19+ls.f_20_24+ls.f_25_29+" +
        "ls.f_30_34+ls.f_35_39+ls.f_40_44+ls.f_45_49+ls.f_50_54+ls.f_55_59+" +
        "ls.f_60_64+ls.f_65_69+ls.f_70_74+ls.f_75_79+ls.f_80_99+ls.f_nr)";

    // Somme de toutes les colonnes hommes
    private static final String SUM_H =
        "(ls.h_1_4+ls.h_5_9+ls.h_10_14+ls.h_15_19+ls.h_20_24+ls.h_25_29+" +
        "ls.h_30_34+ls.h_35_39+ls.h_40_44+ls.h_45_49+ls.h_50_54+ls.h_55_59+" +
        "ls.h_60_64+ls.h_65_69+ls.h_70_74+ls.h_75_79+ls.h_80_99+ls.h_nr)";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String federation = clean(req.getParameter("federation"));
        String region     = clean(req.getParameter("region"));
        int    top        = toInt(req.getParameter("top"), 10);
        if (top < 1 || top > 100) top = 10;

        try (Connection conn = DBConnection.get()) {
            Map<String, Object> result = buildStats(conn, federation, region, top);
            gson.toJson(result, resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private Map<String, Object> buildStats(Connection conn,
            String federation, String region, int top) throws SQLException {

        // ----------------------------------------------------------------
        // 1. Top N communes par nb licenciés (pour l'histogramme)
        // ----------------------------------------------------------------
        StringBuilder sqlTop = new StringBuilder(
            "SELECT co.nom_commune AS nom, " +
            "  COALESCE(SUM(ls.total), 0)       AS total, " +
            "  COALESCE(SUM(" + SUM_F + "), 0)  AS femmes, " +
            "  COALESCE(SUM(" + SUM_H + "), 0)  AS hommes " +
            "FROM licence_stats ls " +
            "JOIN commune co ON ls.code_commune = co.code_commune " +
            "WHERE ls.annee = 2019"
        );

        List<Object> params = new ArrayList<>();
        if (federation != null) { sqlTop.append(" AND ls.code_federation = ?"); params.add(federation); }
        if (region != null)     { sqlTop.append(" AND co.region = ?");          params.add(region); }
        sqlTop.append(" GROUP BY co.nom_commune ORDER BY total DESC LIMIT ?");
        params.add(top);

        List<Map<String, Object>> communes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sqlTop.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("nom",    rs.getString("nom"));
                row.put("total",  rs.getLong("total"));
                row.put("femmes", rs.getLong("femmes"));
                row.put("hommes", rs.getLong("hommes"));
                communes.add(row);
            }
        }

        // ----------------------------------------------------------------
        // 2. Totaux globaux H/F (pour le camembert et les KPIs)
        // ----------------------------------------------------------------
        StringBuilder sqlTotaux = new StringBuilder(
            "SELECT " +
            "  COALESCE(SUM(" + SUM_F + "), 0) AS totalFemmes, " +
            "  COALESCE(SUM(" + SUM_H + "), 0) AS totalHommes " +
            "FROM licence_stats ls " +
            "JOIN commune co ON ls.code_commune = co.code_commune " +
            "WHERE ls.annee = 2019"
        );

        List<Object> params2 = new ArrayList<>();
        if (federation != null) { sqlTotaux.append(" AND ls.code_federation = ?"); params2.add(federation); }
        if (region != null)     { sqlTotaux.append(" AND co.region = ?");          params2.add(region); }

        long totalFemmes = 0, totalHommes = 0;
        try (PreparedStatement ps = conn.prepareStatement(sqlTotaux.toString())) {
            for (int i = 0; i < params2.size(); i++) ps.setObject(i + 1, params2.get(i));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalFemmes = rs.getLong("totalFemmes");
                totalHommes = rs.getLong("totalHommes");
            }
        }

        // ----------------------------------------------------------------
        // 3. Assemblage du résultat
        // ----------------------------------------------------------------
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("communes",    communes);
        result.put("totalFemmes", totalFemmes);
        result.put("totalHommes", totalHommes);
        return result;
    }

    private String clean(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
    private int toInt(String s, int def) {
        try { return (s != null && !s.isBlank()) ? Integer.parseInt(s) : def; }
        catch (NumberFormatException e) { return def; }
    }
}