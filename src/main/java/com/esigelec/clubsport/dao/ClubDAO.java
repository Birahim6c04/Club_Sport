package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.Club;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClubDAO {

	// ============================================================
	// 1. RECHERCHE PAR FEDERATION ET/OU REGION ET/OU CODE POSTAL
	// ============================================================
	public List<Club> rechercher(String codeFederation, String region, String codePostal) throws Exception {
	    List<Club> resultats = new ArrayList<>();

	    String sql = "SELECT c.code_commune, c.nom_commune, c.region, c.departement, " +
	                 "       c.latitude, c.longitude, " +
	                 "       f.code_federation, f.nom_federation, " +
	                 "       cs.clubs, cs.epa, cs.total " +
	                 "FROM club_stats cs " +
	                 "JOIN commune c    ON c.code_commune = cs.code_commune " +
	                 "JOIN federation f ON f.code_federation = cs.code_federation " +
	                 "WHERE cs.total > 0 ";

	    if (codeFederation != null && !codeFederation.isEmpty()) {
	        sql += "AND cs.code_federation = ? ";
	    }
	    if (region != null && !region.isEmpty()) {
	        sql += "AND c.region = ? ";
	    }
	    if (codePostal != null && !codePostal.isEmpty()) {
	        sql += "AND c.code_postal = ? ";
	    }
	    sql += "ORDER BY cs.total DESC LIMIT 500";

	    try (Connection conn = DbConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        int i = 1;
	        if (codeFederation != null && !codeFederation.isEmpty()) {
	            ps.setString(i++, codeFederation);
	        }
	        if (region != null && !region.isEmpty()) {
	            ps.setString(i++, region);
	        }
	        if (codePostal != null && !codePostal.isEmpty()) {
	            ps.setString(i++, codePostal);
	        }

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Club club = new Club();
	                club.setCodeCommune(rs.getString("code_commune"));
	                club.setNomCommune(rs.getString("nom_commune"));
	                club.setRegion(rs.getString("region"));
	                club.setDepartement(rs.getString("departement"));
	                club.setLatitude(rs.getDouble("latitude"));
	                club.setLongitude(rs.getDouble("longitude"));
	                club.setCodeFederation(rs.getString("code_federation"));
	                club.setNomFederation(rs.getString("nom_federation"));
	                club.setClubs(rs.getInt("clubs"));
	                club.setEpa(rs.getInt("epa"));
	                club.setTotal(rs.getInt("total"));
	                resultats.add(club);
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw e;
	    }

	    return resultats;
	}

    // ============================================================
    // 2. RECHERCHE PAR RAYON AUTOUR D'UNE COMMUNE
    // ============================================================
    public List<Club> rechercherParRayon(String codeFederation, String codeCommuneRef, int rayonKm) throws Exception {
        List<Club> resultats = new ArrayList<>();

        // Étape 1 : on récupère les coordonnées GPS de la commune de référence
        double[] coords = getCoordonnees(codeCommuneRef);
        if (coords == null) {
            throw new IllegalArgumentException("Commune introuvable : " + codeCommuneRef);
        }
        double latRef = coords[0];
        double lonRef = coords[1];

        // Étape 2 : on cherche tous les clubs avec calcul de distance (formule de Haversine)
        // 6371 = rayon de la Terre en km
        String sql = "SELECT c.code_commune, c.nom_commune, c.region, c.departement, " +
                     "       c.latitude, c.longitude, " +
                     "       f.code_federation, f.nom_federation, " +
                     "       cs.clubs, cs.epa, cs.total, " +
                     "       (6371 * ACOS(COS(RADIANS(?)) * COS(RADIANS(c.latitude)) " +
                     "        * COS(RADIANS(c.longitude) - RADIANS(?)) " +
                     "        + SIN(RADIANS(?)) * SIN(RADIANS(c.latitude)))) AS distance_km " +
                     "FROM club_stats cs " +
                     "JOIN commune c    ON c.code_commune = cs.code_commune " +
                     "JOIN federation f ON f.code_federation = cs.code_federation " +
                     "WHERE cs.total > 0 " +
                     "  AND c.latitude IS NOT NULL ";

        if (codeFederation != null && !codeFederation.isEmpty()) {
            sql += "AND cs.code_federation = ? ";
        }
        sql += "HAVING distance_km <= ? ORDER BY distance_km ASC LIMIT 500";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // On remplit les paramètres dans l'ordre
            ps.setDouble(1, latRef);
            ps.setDouble(2, lonRef);
            ps.setDouble(3, latRef);

            int i = 4;
            if (codeFederation != null && !codeFederation.isEmpty()) {
                ps.setString(i++, codeFederation);
            }
            ps.setInt(i, rayonKm);

            // On exécute et on lit les résultats
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Club club = new Club();
                    club.setCodeCommune(rs.getString("code_commune"));
                    club.setNomCommune(rs.getString("nom_commune"));
                    club.setRegion(rs.getString("region"));
                    club.setDepartement(rs.getString("departement"));
                    club.setLatitude(rs.getDouble("latitude"));
                    club.setLongitude(rs.getDouble("longitude"));
                    club.setCodeFederation(rs.getString("code_federation"));
                    club.setNomFederation(rs.getString("nom_federation"));
                    club.setClubs(rs.getInt("clubs"));
                    club.setEpa(rs.getInt("epa"));
                    club.setTotal(rs.getInt("total"));
                    club.setDistanceKm(rs.getDouble("distance_km"));
                    resultats.add(club);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return resultats;
    }

    // ============================================================
    // 3. LISTE DES REGIONS (pour le menu déroulant)
    // ============================================================
    public List<String> listerRegions() throws Exception {
        List<String> regions = new ArrayList<>();
        String sql = "SELECT DISTINCT region FROM commune " +
                     "WHERE region IS NOT NULL AND region <> '' " +
                     "ORDER BY region";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                regions.add(rs.getString(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return regions;
    }

    // ============================================================
    // 4. RECHERCHE DE COMMUNES PAR NOM (pour l'autocomplétion)
    // ============================================================
    public List<String[]> rechercherCommune(String query) throws Exception {
        List<String[]> resultats = new ArrayList<>();
        String sql = "SELECT code_commune, nom_commune, code_postal, departement " +
                     "FROM commune " +
                     "WHERE nom_commune LIKE ? AND latitude IS NOT NULL " +
                     "ORDER BY nom_commune LIMIT 20";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, query + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] commune = new String[4];
                    commune[0] = rs.getString("code_commune");
                    commune[1] = rs.getString("nom_commune");
                    commune[2] = rs.getString("code_postal");
                    commune[3] = rs.getString("departement");
                    resultats.add(commune);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return resultats;
    }

    // ============================================================
    // METHODE INTERNE : récupère lat/lon d'une commune
    // ============================================================
    private double[] getCoordonnees(String codeCommune) throws Exception {
        double[] coords = null;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT latitude, longitude FROM commune WHERE code_commune = ?")) {

            ps.setString(1, codeCommune);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double lat = rs.getDouble("latitude");
                    double lon = rs.getDouble("longitude");
                    if (!rs.wasNull()) {
                        coords = new double[]{ lat, lon };
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return coords;
    }
}