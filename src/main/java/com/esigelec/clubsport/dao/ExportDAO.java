package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.ExportDashboardDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ExportDAO {

    private static final String TOTAL_FEMMES =
            "f_1_4 + f_5_9 + f_10_14 + f_15_19 + f_20_24 + f_25_29 + " +
            "f_30_34 + f_35_39 + f_40_44 + f_45_49 + f_50_54 + f_55_59 + " +
            "f_60_64 + f_65_69 + f_70_74 + f_75_79 + f_80_99 + f_nr";

    private static final String TOTAL_HOMMES =
            "h_1_4 + h_5_9 + h_10_14 + h_15_19 + h_20_24 + h_25_29 + " +
            "h_30_34 + h_35_39 + h_40_44 + h_45_49 + h_50_54 + h_55_59 + " +
            "h_60_64 + h_65_69 + h_70_74 + h_75_79 + h_80_99 + h_nr";

    public List<ExportDashboardDTO> getDashboardExport(
            String region,
            String departement,
            String federation,
            String codeCommune
    ) throws Exception {

        List<ExportDashboardDTO> liste = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT " +
                "c.region, " +
                "c.departement, " +
                "CONCAT(c.nom_commune, ' (', c.code_postal, ')') AS commune, " +
                "f.nom_federation, " +
                "SUM(ls.total) AS total_licencies, " +
                "SUM(" + TOTAL_HOMMES + ") AS hommes, " +
                "SUM(" + TOTAL_FEMMES + ") AS femmes " +

                "FROM licence_stats ls " +

                "JOIN commune c " +
                "ON ls.code_commune = c.code_commune " +

                "JOIN federation f " +
                "ON ls.code_federation = f.code_federation " +

                "WHERE 1=1 "
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

        if (federation != null && !federation.isEmpty()) {
            sql.append("AND f.nom_federation = ? ");
            params.add(federation);
        }

        sql.append(
                "GROUP BY " +
                "c.region, " +
                "c.departement, " +
                "c.nom_commune, " +
                "c.code_postal, " +
                "f.nom_federation " +

                "ORDER BY " +
                "c.region, " +
                "c.departement, " +
                "c.nom_commune, " +
                "f.nom_federation"
        );

        try (
                Connection conn = DbConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())
        ) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ExportDashboardDTO dto =
                            new ExportDashboardDTO();

                    dto.setRegion(
                            rs.getString("region")
                    );

                    dto.setDepartement(
                            rs.getString("departement")
                    );

                    dto.setCommune(
                            rs.getString("commune")
                    );

                    dto.setFederation(
                            rs.getString("nom_federation")
                    );

                    dto.setTotalLicencies(
                            rs.getInt("total_licencies")
                    );

                    dto.setHommes(
                            rs.getInt("hommes")
                    );

                    dto.setFemmes(
                            rs.getInt("femmes")
                    );

                    liste.add(dto);
                }
            }
        }

        return liste;
    }
}