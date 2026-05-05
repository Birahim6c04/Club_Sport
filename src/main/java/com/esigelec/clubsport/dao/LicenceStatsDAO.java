package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.ClassementCommune;
import com.esigelec.clubsport.model.StatDTO;

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
    public List<StatDTO> getRepartitionAge(
            String region,
            String departement,
            String federation,
            String codeCommune
    ) throws Exception {

        List<StatDTO> stats = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "COALESCE(SUM(ls.f_1_4 + ls.h_1_4), 0) AS age_1_4, " +
            "COALESCE(SUM(ls.f_5_9 + ls.h_5_9), 0) AS age_5_9, " +
            "COALESCE(SUM(ls.f_10_14 + ls.h_10_14), 0) AS age_10_14, " +
            "COALESCE(SUM(ls.f_15_19 + ls.h_15_19), 0) AS age_15_19, " +
            "COALESCE(SUM(ls.f_20_24 + ls.h_20_24), 0) AS age_20_24, " +
            "COALESCE(SUM(ls.f_25_29 + ls.h_25_29), 0) AS age_25_29, " +
            "COALESCE(SUM(ls.f_30_34 + ls.h_30_34), 0) AS age_30_34, " +
            "COALESCE(SUM(ls.f_35_39 + ls.h_35_39), 0) AS age_35_39, " +
            "COALESCE(SUM(ls.f_40_44 + ls.h_40_44), 0) AS age_40_44, " +
            "COALESCE(SUM(ls.f_45_49 + ls.h_45_49), 0) AS age_45_49, " +
            "COALESCE(SUM(ls.f_50_54 + ls.h_50_54), 0) AS age_50_54, " +
            "COALESCE(SUM(ls.f_55_59 + ls.h_55_59), 0) AS age_55_59, " +
            "COALESCE(SUM(ls.f_60_64 + ls.h_60_64), 0) AS age_60_64, " +
            "COALESCE(SUM(ls.f_65_69 + ls.h_65_69), 0) AS age_65_69, " +
            "COALESCE(SUM(ls.f_70_74 + ls.h_70_74), 0) AS age_70_74, " +
            "COALESCE(SUM(ls.f_75_79 + ls.h_75_79), 0) AS age_75_79, " +
            "COALESCE(SUM(ls.f_80_99 + ls.h_80_99), 0) AS age_80_99 " +
            "FROM licence_stats ls " +
            "JOIN commune c ON ls.code_commune = c.code_commune " +
            "JOIN federation f ON ls.code_federation = f.code_federation " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        ajouterFiltres(sql, params, region, departement, federation, codeCommune);

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            remplirParametres(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.add(new StatDTO("1-4 ans", rs.getInt("age_1_4")));
                    stats.add(new StatDTO("5-9 ans", rs.getInt("age_5_9")));
                    stats.add(new StatDTO("10-14 ans", rs.getInt("age_10_14")));
                    stats.add(new StatDTO("15-19 ans", rs.getInt("age_15_19")));
                    stats.add(new StatDTO("20-24 ans", rs.getInt("age_20_24")));
                    stats.add(new StatDTO("25-29 ans", rs.getInt("age_25_29")));
                    stats.add(new StatDTO("30-34 ans", rs.getInt("age_30_34")));
                    stats.add(new StatDTO("35-39 ans", rs.getInt("age_35_39")));
                    stats.add(new StatDTO("40-44 ans", rs.getInt("age_40_44")));
                    stats.add(new StatDTO("45-49 ans", rs.getInt("age_45_49")));
                    stats.add(new StatDTO("50-54 ans", rs.getInt("age_50_54")));
                    stats.add(new StatDTO("55-59 ans", rs.getInt("age_55_59")));
                    stats.add(new StatDTO("60-64 ans", rs.getInt("age_60_64")));
                    stats.add(new StatDTO("65-69 ans", rs.getInt("age_65_69")));
                    stats.add(new StatDTO("70-74 ans", rs.getInt("age_70_74")));
                    stats.add(new StatDTO("75-79 ans", rs.getInt("age_75_79")));
                    stats.add(new StatDTO("80-99 ans", rs.getInt("age_80_99")));
                }
            }
        }

        return stats;
    }
    
    
    public List<StatDTO> getClubsParFederation(
            String region,
            String departement,
            String codeCommune
    ) throws Exception {

        List<StatDTO> stats = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT f.nom_federation, COALESCE(SUM(cs.clubs), 0) AS total_clubs " +
            "FROM club_stats cs " +
            "JOIN commune c ON cs.code_commune = c.code_commune " +
            "JOIN federation f ON cs.code_federation = f.code_federation " +
            "WHERE f.nom_federation IS NOT NULL " +
            "AND f.nom_federation <> '' "
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

        sql.append(
            "GROUP BY f.nom_federation " +
            "ORDER BY total_clubs DESC " +
            "LIMIT 10"
        );

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            remplirParametres(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.add(new StatDTO(
                            rs.getString("nom_federation"),
                            rs.getInt("total_clubs")
                    ));
                }
            }
        }

        return stats;
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