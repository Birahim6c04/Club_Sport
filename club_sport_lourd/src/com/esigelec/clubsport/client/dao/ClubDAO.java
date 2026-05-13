package com.esigelec.clubsport.client.dao;

import com.esigelec.clubsport.client.model.Club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClubDAO {

    // Sous-requête pour calculer les licenciés H/F
    private static final String SOUS_REQUETE_LICENCES =
        "LEFT JOIN (SELECT code_commune, code_federation, " +
        "  (h_1_4+h_5_9+h_10_14+h_15_19+h_20_24+h_25_29+h_30_34+h_35_39+h_40_44+h_45_49+h_50_54+h_55_59+h_nr) AS licences_h, " +
        "  (f_1_4+f_5_9+f_10_14+f_15_19+f_20_24+f_25_29+f_30_34+f_35_39+f_40_44+f_45_49+f_50_54+f_55_59+f_60_64+f_65_69+f_70_74+f_75_79+f_80_99+f_nr) AS licences_f " +
        "  FROM licence_stats) ls " +
        "  ON ls.code_commune = cs.code_commune AND ls.code_federation = cs.code_federation ";

    // Recherche admin avec critères avancés
    public List<Club> rechercheAdmin(String region, String departement,
                                     int clubsMin, int licenciesMin,
                                     double tauxFeminisationMin,
                                     String triPar) throws Exception {

        List<Club> resultats = new ArrayList<>();

        String sql = "SELECT c.nom_commune, c.region, c.departement, " +
                     "       f.nom_federation, " +
                     "       cs.clubs, cs.epa, cs.total, " +
                     "       COALESCE(ls.licences_h, 0) AS licences_h, " +
                     "       COALESCE(ls.licences_f, 0) AS licences_f " +
                     "FROM club_stats cs " +
                     "JOIN commune c    ON c.code_commune = cs.code_commune " +
                     "JOIN federation f ON f.code_federation = cs.code_federation " +
                     SOUS_REQUETE_LICENCES +
                     "WHERE 1=1 ";

        if (region != null && !region.isEmpty())           sql += "AND c.region = ? ";
        if (departement != null && !departement.isEmpty()) sql += "AND c.departement = ? ";
        if (clubsMin > 0)                                  sql += "AND cs.clubs >= ? ";
        if (licenciesMin > 0)                              sql += "AND cs.total >= ? ";
        if (tauxFeminisationMin > 0) {
            sql += "AND (COALESCE(ls.licences_f, 0) / NULLIF((COALESCE(ls.licences_h, 0) + COALESCE(ls.licences_f, 0)), 0)) * 100 >= ? ";
        }

        // Tri
        if ("clubs".equals(triPar))    sql += "ORDER BY cs.clubs DESC ";
        else if ("nom".equals(triPar)) sql += "ORDER BY c.nom_commune ASC ";
        else                           sql += "ORDER BY cs.total DESC ";

        sql += "LIMIT 1000";

        Connection conn = DbConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        int i = 1;
        if (region != null && !region.isEmpty())           ps.setString(i++, region);
        if (departement != null && !departement.isEmpty()) ps.setString(i++, departement);
        if (clubsMin > 0)                                  ps.setInt(i++, clubsMin);
        if (licenciesMin > 0)                              ps.setInt(i++, licenciesMin);
        if (tauxFeminisationMin > 0)                       ps.setDouble(i++, tauxFeminisationMin);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Club club = new Club();
            club.setNomCommune(rs.getString("nom_commune"));
            club.setDepartement(rs.getString("departement"));
            club.setRegion(rs.getString("region"));
            club.setNomFederation(rs.getString("nom_federation"));
            club.setClubs(rs.getInt("clubs"));
            club.setEpa(rs.getInt("epa"));
            club.setTotal(rs.getInt("total"));
            club.setLicencesH(rs.getInt("licences_h"));
            club.setLicencesF(rs.getInt("licences_f"));
            resultats.add(club);
        }

        rs.close();
        ps.close();
        conn.close();
        return resultats;
    }

    // Liste des régions
    public List<String> listerRegions() throws Exception {
        List<String> liste = new ArrayList<>();

        Connection conn = DbConnection.getConnection();
        String sql = "SELECT DISTINCT region FROM commune WHERE region IS NOT NULL AND region <> '' ORDER BY region";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) liste.add(rs.getString(1));
        rs.close();
        ps.close();
        conn.close();
        return liste;
    }

    // Liste des départements
    public List<String> listerDepartements() throws Exception {
        List<String> liste = new ArrayList<>();

        Connection conn = DbConnection.getConnection();
        String sql = "SELECT DISTINCT departement FROM commune WHERE departement IS NOT NULL AND departement <> '' ORDER BY departement";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) liste.add(rs.getString(1));
        rs.close();
        ps.close();
        conn.close();
        return liste;
    }
}