package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.utils.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * DAO pour les statistiques de la page stats.html.
 * Deux requêtes : top N communes + totaux globaux H/F.
 *
 * Chemin : src/main/java/fr/esigelec/clubs/dao/StatsDAO.java
 */
public class StatsDAO {

    private static final String SUM_F =
        "(ls.f_1_4+ls.f_5_9+ls.f_10_14+ls.f_15_19+ls.f_20_24+ls.f_25_29+" +
        "ls.f_30_34+ls.f_35_39+ls.f_40_44+ls.f_45_49+ls.f_50_54+ls.f_55_59+" +
        "ls.f_60_64+ls.f_65_69+ls.f_70_74+ls.f_75_79+ls.f_80_99+ls.f_nr)";

    private static final String SUM_H =
        "(ls.h_1_4+ls.h_5_9+ls.h_10_14+ls.h_15_19+ls.h_20_24+ls.h_25_29+" +
        "ls.h_30_34+ls.h_35_39+ls.h_40_44+ls.h_45_49+ls.h_50_54+ls.h_55_59+" +
        "ls.h_60_64+ls.h_65_69+ls.h_70_74+ls.h_75_79+ls.h_80_99+ls.h_nr)";

    /**
     * Construit les données JSON pour stats.html :
     *   - communes : top N par total licenciés (histogramme)
     *   - totalFemmes / totalHommes : agrégats globaux (camembert + KPIs)
     */
    public Map<String, Object> buildStats(String federation, String region, int top)
            throws SQLException {

        // ── 1. Top N communes ─────────────────────────────────────────
        StringBuilder sqlTop = new StringBuilder(
            "SELECT co.nom_commune AS nom, " +
            "  COALESCE(SUM(ls.total), 0)      AS total, " +
            "  COALESCE(SUM(" + SUM_F + "), 0) AS femmes, " +
            "  COALESCE(SUM(" + SUM_H + "), 0) AS hommes " +
            "FROM licence_stats ls " +
            "JOIN commune co ON ls.code_commune = co.code_commune " +
            "WHERE ls.annee = 2019"
        );
        List<Object> p1 = new ArrayList<>();
        if (federation != null) { sqlTop.append(" AND ls.code_federation = ?"); p1.add(federation); }
        if (region     != null) { sqlTop.append(" AND co.region = ?");          p1.add(region); }
        sqlTop.append(" GROUP BY co.nom_commune ORDER BY total DESC LIMIT ?");
        p1.add(top);

        List<Map<String, Object>> communes = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sqlTop.toString())) {
            for (int i = 0; i < p1.size(); i++) ps.setObject(i + 1, p1.get(i));
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

        // ── 2. Totaux globaux H/F ─────────────────────────────────────
        StringBuilder sqlTot = new StringBuilder(
            "SELECT " +
            "  COALESCE(SUM(" + SUM_F + "), 0) AS totalFemmes, " +
            "  COALESCE(SUM(" + SUM_H + "), 0) AS totalHommes " +
            "FROM licence_stats ls " +
            "JOIN commune co ON ls.code_commune = co.code_commune " +
            "WHERE ls.annee = 2019"
        );
        List<Object> p2 = new ArrayList<>();
        if (federation != null) { sqlTot.append(" AND ls.code_federation = ?"); p2.add(federation); }
        if (region     != null) { sqlTot.append(" AND co.region = ?");          p2.add(region); }

        long totalFemmes = 0, totalHommes = 0;
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sqlTot.toString())) {
            for (int i = 0; i < p2.size(); i++) ps.setObject(i + 1, p2.get(i));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalFemmes = rs.getLong("totalFemmes");
                totalHommes = rs.getLong("totalHommes");
            }
        }

        // ── 3. Assemblage ─────────────────────────────────────────────
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("communes",    communes);
        result.put("totalFemmes", totalFemmes);
        result.put("totalHommes", totalHommes);
        return result;
    }
}