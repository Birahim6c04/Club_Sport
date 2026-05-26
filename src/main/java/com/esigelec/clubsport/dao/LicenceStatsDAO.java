package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.ClassementCommune;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class LicenceStatsDAO {

    private static final String TOTAL_FEMMES =
        "f_1_4 + f_5_9 + f_10_14 + f_15_19 + f_20_24 + f_25_29 + " +
        "f_30_34 + f_35_39 + f_40_44 + f_45_49 + f_50_54 + f_55_59 + " +
        "f_60_64 + f_65_69 + f_70_74 + f_75_79 + f_80_99 + f_nr";

    private static final String TOTAL_HOMMES =
        "h_1_4 + h_5_9 + h_10_14 + h_15_19 + h_20_24 + h_25_29 + " +
        "h_30_34 + h_35_39 + h_40_44 + h_45_49 + h_50_54 + h_55_59 + " +
        "h_60_64 + h_65_69 + h_70_74 + h_75_79 + h_80_99 + h_nr";

    public List<String> listerRegions() throws Exception {
        List<String> regions = new ArrayList<>();

        String sql = "SELECT DISTINCT region FROM commune " +
                     "WHERE region IS NOT NULL AND region <> '' " +
                     "ORDER BY region";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                regions.add(rs.getString("region"));
            }
        }

        return regions;
    }

    public List<String> listerDepartementsParRegion(String region) throws Exception {
        List<String> liste = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT departement FROM commune " +
            "WHERE departement IS NOT NULL AND departement <> '' "
        );

        List<Object> params = new ArrayList<>();

        if (region != null && !region.isEmpty()) {
            sql.append("AND region = ? ");
            params.add(region);
        }

        sql.append("ORDER BY departement");

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            remplirParametres(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(rs.getString("departement"));
                }
            }
        }

        return liste;
    }

    public List<String[]> listerCommunesParFiltres(String region, String departement) throws Exception {
        List<String[]> liste = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT code_commune, nom_commune, code_postal " +
            "FROM commune " +
            "WHERE nom_commune IS NOT NULL AND nom_commune <> '' "
        );

        List<Object> params = new ArrayList<>();

        if (region != null && !region.isEmpty()) {
            sql.append("AND region = ? ");
            params.add(region);
        }

        if (departement != null && !departement.isEmpty()) {
            sql.append("AND departement = ? ");
            params.add(departement);
        }

        sql.append("ORDER BY nom_commune");

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            remplirParametres(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] commune = new String[3];
                    commune[0] = rs.getString("code_commune");
                    commune[1] = rs.getString("nom_commune");
                    commune[2] = rs.getString("code_postal");

                    liste.add(commune);
                }
            }
        }

        return liste;
    }

    public List<String> listerFederationsParFiltres(String region, String departement, String codeCommune) throws Exception {
        List<String> liste = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT DISTINCT f.nom_federation " +
            "FROM licence_stats ls " +
            "JOIN commune c ON ls.code_commune = c.code_commune " +
            "JOIN federation f ON ls.code_federation = f.code_federation " +
            "WHERE f.nom_federation IS NOT NULL AND f.nom_federation <> '' "
        );

        List<Object> params = new ArrayList<>();

        if (region != null && !region.isEmpty()) {
            sql.append("AND c.region = ? ");
            params.add(region);
        }

        if (departement != null && !departement.isEmpty()) {
            sql.append("AND c.departement = ? ");
            params.add(departement);
        }

        if (codeCommune != null && !codeCommune.isEmpty()) {
            sql.append("AND c.code_commune = ? ");
            params.add(codeCommune);
        }

        sql.append("ORDER BY f.nom_federation");

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            remplirParametres(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(rs.getString("nom_federation"));
                }
            }
        }

        return liste;
    }

    public int getTotalFiltre(String region, String departement, String federation, String codeCommune) throws Exception {
        StringBuilder sql = new StringBuilder(
            "SELECT COALESCE(SUM(ls.total), 0) AS total " +
            "FROM licence_stats ls " +
            "JOIN commune c ON ls.code_commune = c.code_commune " +
            "JOIN federation f ON ls.code_federation = f.code_federation " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        ajouterFiltres(sql, params, region, departement, federation, codeCommune);

        return executerTotal(sql.toString(), params, "total");
    }

    public int getTotalFemmesFiltre(String region, String departement, String federation, String codeCommune) throws Exception {
        StringBuilder sql = new StringBuilder(
            "SELECT COALESCE(SUM(" + TOTAL_FEMMES + "), 0) AS femmes " +
            "FROM licence_stats ls " +
            "JOIN commune c ON ls.code_commune = c.code_commune " +
            "JOIN federation f ON ls.code_federation = f.code_federation " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        ajouterFiltres(sql, params, region, departement, federation, codeCommune);

        return executerTotal(sql.toString(), params, "femmes");
    }

    public int getTotalHommesFiltre(String region, String departement, String federation, String codeCommune) throws Exception {
        StringBuilder sql = new StringBuilder(
            "SELECT COALESCE(SUM(" + TOTAL_HOMMES + "), 0) AS hommes " +
            "FROM licence_stats ls " +
            "JOIN commune c ON ls.code_commune = c.code_commune " +
            "JOIN federation f ON ls.code_federation = f.code_federation " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        ajouterFiltres(sql, params, region, departement, federation, codeCommune);

        return executerTotal(sql.toString(), params, "hommes");
    }

    public List<ClassementCommune> getClassementCommunes(
            String region,
            String departement,
            String federation
    ) throws Exception {

        List<ClassementCommune> classement = new ArrayList<>();

        int totalGeneral = getTotalFiltre(region, departement, federation, null);

        StringBuilder sql = new StringBuilder(
        	    "SELECT c.code_commune, c.nom_commune, c.code_postal, " +
        	    "COALESCE(SUM(ls.total), 0) AS total_licencies " +
        	    "FROM licence_stats ls " +
        	    "JOIN commune c ON ls.code_commune = c.code_commune " +
        	    "JOIN federation f ON ls.code_federation = f.code_federation " +
        	    "WHERE 1=1 " +
        	    "AND c.nom_commune IS NOT NULL " +
        	    "AND c.nom_commune <> '' " +
        	    "AND c.code_postal IS NOT NULL " +
        	    "AND c.code_postal <> '' "
        	);
        

        List<Object> params = new ArrayList<>();

        ajouterFiltres(sql, params, region, departement, federation, null);

        sql.append(
            "GROUP BY c.code_commune, c.nom_commune, c.code_postal " +
            "ORDER BY total_licencies DESC " +
            "LIMIT 10"
        );

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            remplirParametres(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    ClassementCommune commune = new ClassementCommune();

                    int totalLicencies = rs.getInt("total_licencies");

                    commune.setCodeCommune(rs.getString("code_commune"));
                    commune.setNomCommune(rs.getString("nom_commune"));
                    commune.setCodePostal(rs.getString("code_postal"));
                    commune.setTotalLicencies(totalLicencies);

                    double pourcentage = 0;

                    if (totalGeneral > 0) {
                        pourcentage = (totalLicencies * 100.0) / totalGeneral;
                    }

                    double arrondi = Math.round(pourcentage * 100.0) / 100.0;
                    commune.setPourcentage(arrondi);

                    classement.add(commune);
                }
            }
        }

        return classement;
    }
    
    private void ajouterFiltres(
            StringBuilder sql,
            List<Object> params,
            String region,
            String departement,
            String federation,
            String codeCommune
    ) {
        if (region != null && !region.isEmpty()) {
            sql.append("AND c.region = ? ");
            params.add(region);
        }

        if (departement != null && !departement.isEmpty()) {
            sql.append("AND c.departement = ? ");
            params.add(departement);
        }

        if (codeCommune != null && !codeCommune.isEmpty()) {
            sql.append("AND c.code_commune = ? ");
            params.add(codeCommune);
        }

        if (federation != null && !federation.isEmpty()) {
            sql.append("AND f.nom_federation = ? ");
            params.add(federation);
        }
    }

    private int executerTotal(String sql, List<Object> params, String colonne) throws Exception {
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            remplirParametres(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(colonne);
                }
            }
        }

        return 0;
    }

    private void remplirParametres(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}