package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.Club;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClubDAO {

    /** Recherche par fédération et/ou région */
    public List<Club> rechercher(String codeFederation, String region) throws Exception {
        List<Club> resultats = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT c.code_commune, c.nom_commune, c.region, c.departement, " +
            "       c.latitude, c.longitude, " +
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
                    resultats.add(mapClub(rs));
                }
            }
        }
        return resultats;
    }

    /** Recherche par fédération dans un RAYON autour d'une commune */
    public List<Club> rechercherParRayon(String codeFederation, String codeCommuneRef, int rayonKm) throws Exception {
        List<Club> resultats = new ArrayList<>();

        // 1. Coordonnées de la commune de référence
        double[] coords = getCoordonnees(codeCommuneRef);
        if (coords == null) {
            throw new IllegalArgumentException("Commune introuvable ou sans coordonnées : " + codeCommuneRef);
        }
        double latRef = coords[0];
        double lonRef = coords[1];

        // 2. Requête avec formule de Haversine (6371 = rayon de la Terre en km)
        StringBuilder sql = new StringBuilder(
            "SELECT c.code_commune, c.nom_commune, c.region, c.departement, " +
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
            "  AND c.latitude IS NOT NULL AND c.longitude IS NOT NULL "
        );

        List<Object> params = new ArrayList<>();
        params.add(latRef);
        params.add(lonRef);
        params.add(latRef);

        if (codeFederation != null && !codeFederation.isEmpty()) {
            sql.append("AND cs.code_federation = ? ");
            params.add(codeFederation);
        }

        // Pré-filtrage rapide par bounding box (utilise index)
        double deltaLat = rayonKm / 111.0;
        double deltaLon = rayonKm / (111.0 * Math.cos(Math.toRadians(latRef)));
        sql.append("AND c.latitude  BETWEEN ? AND ? ");
        sql.append("AND c.longitude BETWEEN ? AND ? ");
        params.add(latRef - deltaLat);
        params.add(latRef + deltaLat);
        params.add(lonRef - deltaLon);
        params.add(lonRef + deltaLon);

        // Filtre précis par distance Haversine
        sql.append("HAVING distance_km <= ? ");
        params.add((double) rayonKm);

        sql.append("ORDER BY distance_km ASC LIMIT 500");

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Club club = mapClub(rs);
                    club.setDistanceKm(rs.getDouble("distance_km"));
                    resultats.add(club);
                }
            }
        }
        return resultats;
    }

    private double[] getCoordonnees(String codeCommune) throws Exception {
        String sql = "SELECT latitude, longitude FROM commune WHERE code_commune = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codeCommune);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double lat = rs.getDouble("latitude");
                    double lon = rs.getDouble("longitude");
                    if (rs.wasNull()) return null;
                    return new double[]{lat, lon};
                }
            }
        }
        return null;
    }

    public List<String> listerRegions() throws Exception {
        List<String> regions = new ArrayList<>();
        String sql = "SELECT DISTINCT region FROM commune WHERE region IS NOT NULL AND region <> '' ORDER BY region";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) regions.add(rs.getString(1));
        }
        return regions;
    }

    /** Recherche de communes par nom (pour l'autocomplétion) */
    public List<String[]> rechercherCommune(String query) throws Exception {
        List<String[]> resultats = new ArrayList<>();
        String sql = "SELECT code_commune, nom_commune, code_postal, departement " +
                     "FROM commune WHERE nom_commune LIKE ? AND latitude IS NOT NULL " +
                     "ORDER BY nom_commune LIMIT 20";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(new String[]{
                        rs.getString("code_commune"),
                        rs.getString("nom_commune"),
                        rs.getString("code_postal"),
                        rs.getString("departement")
                    });
                }
            }
        }
        return resultats;
    }

    private Club mapClub(ResultSet rs) throws java.sql.SQLException {
        Club club = new Club();
        club.setCodeCommune(rs.getString("code_commune"));
        club.setNomCommune(rs.getString("nom_commune"));
        club.setRegion(rs.getString("region"));
        club.setDepartement(rs.getString("departement"));
        double lat = rs.getDouble("latitude");
        if (!rs.wasNull()) club.setLatitude(lat);
        double lon = rs.getDouble("longitude");
        if (!rs.wasNull()) club.setLongitude(lon);
        club.setCodeFederation(rs.getString("code_federation"));
        club.setNomFederation(rs.getString("nom_federation"));
        club.setClubs(rs.getInt("clubs"));
        club.setEpa(rs.getInt("epa"));
        club.setTotal(rs.getInt("total"));
        return club;
    }
}