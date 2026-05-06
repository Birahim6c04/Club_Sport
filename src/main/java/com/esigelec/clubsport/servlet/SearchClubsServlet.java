package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.utils.DBConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Recherche de clubs.
 * GET /api/search?federation=&region=&commune=&lat=&lng=&rayon=
 *
 * Tables (schéma réel) :
 *   commune       → code_commune, nom_commune, region, latitude, longitude, code_postal
 *   club_stats    → code_commune, code_federation, clubs, total
 *   licence_stats → code_commune, code_federation, total + colonnes f_* / h_*
 *   federation    → code_federation, nom_federation
 */
@WebServlet("/api/search")
public class SearchClubsServlet extends HttpServlet {

    private final Gson gson = new Gson();

    private static final String SUM_F =
        "(ls.f_1_4+ls.f_5_9+ls.f_10_14+ls.f_15_19+ls.f_20_24+ls.f_25_29+" +
        "ls.f_30_34+ls.f_35_39+ls.f_40_44+ls.f_45_49+ls.f_50_54+ls.f_55_59+" +
        "ls.f_60_64+ls.f_65_69+ls.f_70_74+ls.f_75_79+ls.f_80_99+ls.f_nr)";

    private static final String SUM_H =
        "(ls.h_1_4+ls.h_5_9+ls.h_10_14+ls.h_15_19+ls.h_20_24+ls.h_25_29+" +
        "ls.h_30_34+ls.h_35_39+ls.h_40_44+ls.h_45_49+ls.h_50_54+ls.h_55_59+" +
        "ls.h_60_64+ls.h_65_69+ls.h_70_74+ls.h_75_79+ls.h_80_99+ls.h_nr)";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String federation = clean(req.getParameter("federation"));
        String region     = clean(req.getParameter("region"));
        String commune    = clean(req.getParameter("commune"));
        Double lat   = toDouble(req.getParameter("lat"));
        Double lng   = toDouble(req.getParameter("lng"));
        Double rayon = toDouble(req.getParameter("rayon"));

        try (Connection conn = DBConnection.get()) {
            gson.toJson(search(conn, federation, region, commune, lat, lng, rayon), resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private List<Map<String, Object>> search(Connection conn,
            String federation, String region, String commune,
            Double lat, Double lng, Double rayon) throws SQLException {

        StringBuilder sql = new StringBuilder(
            "SELECT co.code_commune, co.nom_commune AS commune, co.region, f.nom_federation AS federation, " +
            "cs.clubs AS nb_clubs, " +
            "COALESCE(ls.total,0) AS total, " +
            "COALESCE(" + SUM_F + ",0) AS femmes, " +
            "COALESCE(" + SUM_H + ",0) AS hommes, " +
            "co.latitude AS lat, co.longitude AS lng " +
            "FROM club_stats cs " +
            "JOIN commune co ON cs.code_commune = co.code_commune " +
            "JOIN federation f ON cs.code_federation = f.code_federation " +
            "LEFT JOIN licence_stats ls ON ls.code_commune = cs.code_commune " +
            "  AND ls.code_federation = cs.code_federation AND ls.annee = cs.annee " +
            "WHERE cs.annee = 2019"
        );

        List<Object> params = new ArrayList<>();

        if (federation != null) { sql.append(" AND cs.code_federation = ?"); params.add(federation); }
        if (region != null)     { sql.append(" AND co.region = ?");          params.add(region); }
        if (commune != null) {
            if (commune.matches("\\d{2,5}")) {
                sql.append(" AND co.code_postal LIKE ?"); params.add(commune + "%");
            } else {
                sql.append(" AND co.nom_commune LIKE ?"); params.add("%" + commune + "%");
            }
        }
        if (lat != null && lng != null && rayon != null) {
            sql.append(" AND (6371*ACOS(COS(RADIANS(?))*COS(RADIANS(co.latitude))" +
                       "*COS(RADIANS(co.longitude)-RADIANS(?))+SIN(RADIANS(?))" +
                       "*SIN(RADIANS(co.latitude)))) <= ?");
            params.add(lat); params.add(lng); params.add(lat); params.add(rayon);
        }
        sql.append(" ORDER BY ls.total DESC LIMIT 300");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("codeCommune", rs.getString("code_commune")); // ← pour le lien club.html
                row.put("commune",    rs.getString("commune"));
                row.put("region",     rs.getString("region"));
                row.put("federation", rs.getString("federation"));
                row.put("nbClubs",    rs.getInt("nb_clubs"));
                row.put("total",      rs.getInt("total"));
                row.put("femmes",     rs.getInt("femmes"));
                row.put("hommes",     rs.getInt("hommes"));
                row.put("lat",        rs.getObject("lat"));
                row.put("lng",        rs.getObject("lng"));
                list.add(row);
            }
            return list;
        }
    }

    private String clean(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
    private Double toDouble(String s) {
        try { return (s != null && !s.isBlank()) ? Double.parseDouble(s) : null; }
        catch (NumberFormatException e) { return null; }
    }
}