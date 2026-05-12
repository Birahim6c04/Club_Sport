package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.utils.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * DAO pour les données de référence.
 * Fédérations et régions utilisées pour les listes déroulantes.
 *
 * Chemin : src/main/java/fr/esigelec/clubs/dao/ReferentielDAO.java
 */
public class ReferentielDAO {

    /** Retourne toutes les fédérations triées par nom. */
    public List<Map<String, String>> findAllFederations() throws SQLException {
        String sql = "SELECT code_federation, nom_federation " +
                     "FROM federation ORDER BY nom_federation";
        List<Map<String, String>> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> row = new LinkedHashMap<>();
                row.put("code", rs.getString("code_federation"));
                row.put("nom",  rs.getString("nom_federation"));
                list.add(row);
            }
        }
        return list;
    }

    /** Retourne toutes les régions distinctes triées alphabétiquement. */
    public List<String> findAllRegions() throws SQLException {
        String sql = "SELECT DISTINCT region FROM commune " +
                     "WHERE region IS NOT NULL ORDER BY region";
        List<String> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString("region"));
        }
        return list;
    }
}