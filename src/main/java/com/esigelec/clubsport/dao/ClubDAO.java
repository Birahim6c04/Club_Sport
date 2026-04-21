package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.Club;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la recherche de clubs sportifs.
 */
public class ClubDAO {

    /**
     * Recherche les clubs selon les critères (fédération et/ou région).
     * Au moins un critère doit être non null.
     */
    public List<Club> rechercher(String codeFederation, String region) throws Exception {
        List<Club> resultats = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT c.code_commune, c.nom_commune, c.region, c.departement, " +
            "       f.code_federation, f.nom_federation, " +
            "       cs.clubs, cs.epa, cs.total " +
            "FROM club_stats cs " +
            "JOIN commune c    ON c.code_commune = cs.code_commune " +
            "JOIN federation f ON f.code_federation = cs.code_federation " +
            "WHERE cs.total > 0 "
        );

        List<Object> params = new ArrayList<>();
        if (codeFederation != null && !codeFederation.isEmpty()) {
            sql.append("AND cs.code_federation = ? ");
            params.add(codeFederation);
        }
        if (region != null && !region.isEmpty()) {
            sql.append("AND c.region = ? ");
            params.add(region);
        }
        sql.append("ORDER BY cs.total DESC LIMIT 500");

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Club club = new Club();
                    club.setCodeCommune(rs.getString("code_commune"));
                    club.setNomCommune(rs.getString("nom_commune"));
                    club.setRegion(rs.getString("region"));
                    club.setDepartement(rs.getString("departement"));
                    club.setCodeFederation(rs.getString("code_federation"));
                    club.setNomFederation(rs.getString("nom_federation"));
                    club.setClubs(rs.getInt("clubs"));
                    club.setEpa(rs.getInt("epa"));
                    club.setTotal(rs.getInt("total"));
                    resultats.add(club);
                }
            }
        }
        return resultats;
    }

    /**
     * Retourne la liste des régions distinctes (pour alimenter un dropdown)
     */
    public List<String> listerRegions() throws Exception {
        List<String> regions = new ArrayList<>();
        String sql = "SELECT DISTINCT region FROM commune WHERE region IS NOT NULL AND region <> '' ORDER BY region";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                regions.add(rs.getString(1));
            }
        }
        return regions;
    }
}