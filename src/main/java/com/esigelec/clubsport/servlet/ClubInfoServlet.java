package com.esigelec.clubsport.servlet;

import com.google.gson.Gson;
import com.esigelec.clubsport.utils.DBConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Gestion de l'espace club (tâche 3).
 * Table réelle : espace_club
 * Colonnes : nom_club, adresse, horaires (TEXT), montant_cotisation, actualites (TEXT)
 *
 * GET  /api/club/{code_commune}        → données stats + contenu espace_club
 * POST /api/club/{code_commune}/update → sauvegarde adresse, horaires, cotisation, actualites
 */
@WebServlet("/api/club/*")
public class ClubInfoServlet extends HttpServlet {

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

        String clubId = extractId(req);
        if (clubId == null) { resp.setStatus(400); return; }

        try (Connection conn = DBConnection.get()) {
            Map<String, Object> data = getClubData(conn, clubId);
            if (data == null) { resp.setStatus(404); resp.getWriter().write("{}"); return; }
            gson.toJson(data, resp.getWriter());
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String clubId = extractId(req);
        if (clubId == null) { resp.setStatus(400); return; }

        // Lire le body JSON
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }

        // Parser le JSON reçu
        @SuppressWarnings("unchecked")
        Map<String, Object> body = gson.fromJson(sb.toString(), Map.class);

        try (Connection conn = DBConnection.get()) {
            saveEspaceClub(conn, clubId, body);
            resp.getWriter().write("{\"status\":\"ok\"}");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ------------------------------------------------------------------
    // Lecture : stats licenciés + contenu espace_club
    // ------------------------------------------------------------------
    private Map<String, Object> getClubData(Connection conn, String clubId) throws SQLException {

        // 1. Stats licenciés
        String sqlStats =
            "SELECT co.nom_commune, f.nom_federation, co.region, " +
            "COALESCE(ls.total,0) AS total, " +
            "COALESCE(" + SUM_F + ",0) AS femmes, " +
            "COALESCE(" + SUM_H + ",0) AS hommes " +
            "FROM commune co " +
            "LEFT JOIN club_stats cs ON cs.code_commune = co.code_commune AND cs.annee = 2019 " +
            "LEFT JOIN federation f  ON f.code_federation = cs.code_federation " +
            "LEFT JOIN licence_stats ls ON ls.code_commune = co.code_commune " +
            "  AND ls.code_federation = cs.code_federation AND ls.annee = 2019 " +
            "WHERE co.code_commune = ? LIMIT 1";

        Map<String, Object> result = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sqlStats)) {
            ps.setString(1, clubId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            result.put("nom",        rs.getString("nom_commune"));
            result.put("federation", rs.getString("nom_federation"));
            result.put("region",     rs.getString("region"));
            result.put("total",      rs.getInt("total"));
            result.put("femmes",     rs.getInt("femmes"));
            result.put("hommes",     rs.getInt("hommes"));
        }

        // 2. Contenu espace_club (adresse, horaires, cotisation, actualités)
        String sqlEspace =
            "SELECT nom_club, adresse, horaires, montant_cotisation, actualites " +
            "FROM espace_club WHERE code_commune = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sqlEspace)) {
            ps.setString(1, clubId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, Object> contenu = new LinkedHashMap<>();
                contenu.put("nomClub",    rs.getString("nom_club"));
                contenu.put("adresse",    rs.getString("adresse"));
                contenu.put("horaires",   rs.getString("horaires"));    // JSON stocké en TEXT
                contenu.put("cotisation", rs.getDouble("montant_cotisation"));
                contenu.put("actualites", rs.getString("actualites"));  // JSON stocké en TEXT
                result.put("contenu", contenu);
            }
        }

        return result;
    }

    // ------------------------------------------------------------------
    // Sauvegarde : INSERT ou UPDATE dans espace_club
    // ------------------------------------------------------------------
    private void saveEspaceClub(Connection conn, String clubId, Map<String, Object> body)
            throws SQLException {

        // Extraire les champs du body JSON
        String nomClub    = getString(body, "nomClub");
        String adresse    = getString(body, "adresse");
        // horaires et actualites sont des objets JS → on les resérialise en JSON string
        String horaires   = body.get("horaires")   != null ? gson.toJson(body.get("horaires"))   : null;
        String actualites = body.get("actualites") != null ? gson.toJson(body.get("actualites")) : null;
        Double cotisation = getDouble(body, "cotisation");

        String sql =
            "INSERT INTO espace_club " +
            "  (nom_club, code_commune, adresse, horaires, montant_cotisation, actualites) " +
            "VALUES (?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "  nom_club = VALUES(nom_club), " +
            "  adresse = VALUES(adresse), " +
            "  horaires = VALUES(horaires), " +
            "  montant_cotisation = VALUES(montant_cotisation), " +
            "  actualites = VALUES(actualites)";

        // Ajouter un index UNIQUE sur code_commune si pas déjà là (ou gérer autrement)
        // Pour l'instant on suppose 1 espace_club par commune
        String sqlAlt =
            "INSERT INTO espace_club " +
            "  (nom_club, code_commune, adresse, horaires, montant_cotisation, actualites) " +
            "VALUES (?, ?, ?, ?, ?, ?) ";

        // Vérifie si une ligne existe déjà
        boolean exists = false;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_espace FROM espace_club WHERE code_commune = ? LIMIT 1")) {
            ps.setString(1, clubId);
            exists = ps.executeQuery().next();
        }

        if (exists) {
            // UPDATE
            String upd =
                "UPDATE espace_club SET nom_club=?, adresse=?, horaires=?, " +
                "montant_cotisation=?, actualites=? WHERE code_commune=?";
            try (PreparedStatement ps = conn.prepareStatement(upd)) {
                ps.setString(1, nomClub);
                ps.setString(2, adresse);
                ps.setString(3, horaires);
                if (cotisation != null) ps.setDouble(4, cotisation); else ps.setNull(4, Types.DECIMAL);
                ps.setString(5, actualites);
                ps.setString(6, clubId);
                ps.executeUpdate();
            }
        } else {
            // INSERT
            try (PreparedStatement ps = conn.prepareStatement(sqlAlt)) {
                ps.setString(1, nomClub != null ? nomClub : "Club " + clubId);
                ps.setString(2, clubId);
                ps.setString(3, adresse);
                ps.setString(4, horaires);
                if (cotisation != null) ps.setDouble(5, cotisation); else ps.setNull(5, Types.DECIMAL);
                ps.setString(6, actualites);
                ps.executeUpdate();
            }
        }
    }

    // ---------- utilitaires ----------
    private String extractId(HttpServletRequest req) {
        String p = req.getPathInfo();
        if (p == null || p.length() < 2) return null;
        return p.split("/")[1];
    }
    private String getString(Map<String, Object> m, String key) {
        Object v = m.get(key);
        return v != null ? v.toString() : null;
    }
    private Double getDouble(Map<String, Object> m, String key) {
        try { return m.get(key) != null ? Double.parseDouble(m.get(key).toString()) : null; }
        catch (NumberFormatException e) { return null; }
    }
}