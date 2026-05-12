package com.esigelec.clubsport.dao;

import com.google.gson.Gson;
import com.esigelec.clubsport.model.EspaceClub;
import com.esigelec.clubsport.utils.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * DAO pour la table espace_club.
 * Gère les stats réelles + le contenu modifiable d'un club.
 *
 * Chemin : src/main/java/com/esigelec/clubsport/dao/EspaceClubDAO.java
 */
public class EspaceClubDAO implements DAO<EspaceClub, Integer> {

    private static final String SUM_F =
        "(ls.f_1_4+ls.f_5_9+ls.f_10_14+ls.f_15_19+ls.f_20_24+ls.f_25_29+" +
        "ls.f_30_34+ls.f_35_39+ls.f_40_44+ls.f_45_49+ls.f_50_54+ls.f_55_59+" +
        "ls.f_60_64+ls.f_65_69+ls.f_70_74+ls.f_75_79+ls.f_80_99+ls.f_nr)";

    private static final String SUM_H =
        "(ls.h_1_4+ls.h_5_9+ls.h_10_14+ls.h_15_19+ls.h_20_24+ls.h_25_29+" +
        "ls.h_30_34+ls.h_35_39+ls.h_40_44+ls.h_45_49+ls.h_50_54+ls.h_55_59+" +
        "ls.h_60_64+ls.h_65_69+ls.h_70_74+ls.h_75_79+ls.h_80_99+ls.h_nr)";

    // ── INSERT ────────────────────────────────────────────────────────
    @Override
    public EspaceClub insert(EspaceClub e) throws SQLException {
        String sql =
            "INSERT INTO espace_club " +
            "(nom_club, code_commune, code_federation, id_responsable, adresse, montant_cotisation) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNomClub());
            ps.setString(2, e.getCodeCommune());
            ps.setString(3, e.getCodeFederation());
            if (e.getIdResponsable() != null) ps.setInt(4, e.getIdResponsable());
            else ps.setNull(4, Types.INTEGER);
            ps.setString(5, e.getAdresse());
            ps.setDouble(6, e.getMontantCotisation());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setIdEspace(keys.getInt(1));
            }
        }
        return e;
    }

    // ── FIND BY ID ────────────────────────────────────────────────────
    @Override
    public EspaceClub findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM espace_club WHERE id_espace = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    // ── FIND BY CODE COMMUNE ──────────────────────────────────────────
    public EspaceClub findByCodeCommune(String codeCommune) throws SQLException {
        String sql = "SELECT * FROM espace_club WHERE code_commune = ? LIMIT 1";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codeCommune);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    /**
     * Retourne l'espace club existant, ou en crée un nouveau
     * en récupérant le vrai nom depuis la table commune.
     */
    public EspaceClub findOrCreate(String codeCommune) throws SQLException {
        EspaceClub espace = findByCodeCommune(codeCommune);
        if (espace != null) return espace;

        // Nom réel depuis la BDD
        String nomCommune = codeCommune;
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT nom_commune FROM commune WHERE code_commune = ? LIMIT 1")) {
            ps.setString(1, codeCommune);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) nomCommune = rs.getString("nom_commune");
            }
        }

        String codeFed = null;
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT code_federation FROM club_stats WHERE code_commune = ? AND annee = 2019 LIMIT 1")) {
            ps.setString(1, codeCommune);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) codeFed = rs.getString("code_federation");
            }
        }

        EspaceClub nouveau = new EspaceClub();
        nouveau.setCodeCommune(codeCommune);
        nouveau.setNomClub("Club de " + nomCommune);
        nouveau.setCodeFederation(codeFed);
        return insert(nouveau);
    }

    // ── FIND ALL ──────────────────────────────────────────────────────
    @Override
    public List<EspaceClub> findAll() throws SQLException {
        String sql = "SELECT * FROM espace_club ORDER BY nom_club";
        List<EspaceClub> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────
    @Override
    public void update(EspaceClub e) throws SQLException {
        String sql = "UPDATE espace_club SET nom_club=?, adresse=?, montant_cotisation=? " +
                     "WHERE id_espace=?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNomClub());
            ps.setString(2, e.getAdresse());
            ps.setDouble(3, e.getMontantCotisation());
            ps.setInt(4, e.getIdEspace());
            ps.executeUpdate();
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────
    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM espace_club WHERE id_espace = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── STATS RÉELLES ────────────────────────────────────────────────
    /**
     * Lit les vraies statistiques depuis commune + club_stats +
     * licence_stats + federation (données issues des CSV importés).
     */
    public Map<String, Object> getStatsReelles(String codeCommune) throws SQLException {
        String sql =
            "SELECT co.nom_commune, f.nom_federation, co.region, " +
            "COALESCE(cs.clubs, 0)         AS nb_clubs, " +
            "COALESCE(ls.total, 0)         AS total, " +
            "COALESCE(" + SUM_F + ", 0)    AS femmes, " +
            "COALESCE(" + SUM_H + ", 0)    AS hommes " +
            "FROM commune co " +
            "LEFT JOIN club_stats cs " +
            "       ON cs.code_commune = co.code_commune AND cs.annee = 2019 " +
            "LEFT JOIN federation f " +
            "       ON f.code_federation = cs.code_federation " +
            "LEFT JOIN licence_stats ls " +
            "       ON ls.code_commune    = co.code_commune " +
            "      AND ls.code_federation = cs.code_federation " +
            "      AND ls.annee = 2019 " +
            "WHERE co.code_commune = ? LIMIT 1";

        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codeCommune);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> stats = new LinkedHashMap<>();
                    stats.put("nom",        rs.getString("nom_commune"));
                    stats.put("region",     rs.getString("region"));
                    stats.put("federation", rs.getString("nom_federation"));
                    stats.put("nbClubs",    rs.getInt("nb_clubs"));
                    stats.put("total",      rs.getInt("total"));
                    stats.put("femmes",     rs.getInt("femmes"));
                    stats.put("hommes",     rs.getInt("hommes"));
                    return stats;
                }
            }
        }
        return null;
    }

    // ── CONTENU MODIFIABLE ────────────────────────────────────────────
    /**
     * Retourne le contenu modifiable depuis espace_club
     * (adresse, horaires JSON, cotisation, actualités JSON).
     */
    public Map<String, Object> getContenu(String codeCommune) throws SQLException {
        String sql =
            "SELECT nom_club, adresse, horaires, montant_cotisation, actualites " +
            "FROM espace_club WHERE code_commune = ? LIMIT 1";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codeCommune);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> contenu = new LinkedHashMap<>();
                    contenu.put("nomClub",    rs.getString("nom_club"));
                    contenu.put("adresse",    rs.getString("adresse"));
                    contenu.put("horaires",   rs.getString("horaires"));
                    contenu.put("cotisation", rs.getDouble("montant_cotisation"));
                    contenu.put("actualites", rs.getString("actualites"));
                    return contenu;
                }
            }
        }
        return null;
    }

    /**
     * Sauvegarde le contenu modifiable (INSERT ou UPDATE).
     * Les objets horaires/actualites sont re-sérialisés en JSON string.
     */
    public void saveContenu(String codeCommune, Map<String, Object> body, Gson gson)
            throws SQLException {

        String nomClub    = str(body, "nomClub");
        String adresse    = str(body, "adresse");
        String horaires   = body.get("horaires")   != null ? gson.toJson(body.get("horaires"))   : null;
        String actualites = body.get("actualites") != null ? gson.toJson(body.get("actualites")) : null;
        Double cotisation = dbl(body, "cotisation");

        // Vérifier si une ligne existe
        boolean exists = false;
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT id_espace FROM espace_club WHERE code_commune = ? LIMIT 1")) {
            ps.setString(1, codeCommune);
            exists = ps.executeQuery().next();
        }

        if (exists) {
            String upd =
                "UPDATE espace_club SET nom_club=?, adresse=?, horaires=?, " +
                "montant_cotisation=?, actualites=? WHERE code_commune=?";
            try (Connection conn = DBConnection.get();
                 PreparedStatement ps = conn.prepareStatement(upd)) {
                ps.setString(1, nomClub);
                ps.setString(2, adresse);
                ps.setString(3, horaires);
                if (cotisation != null) ps.setDouble(4, cotisation);
                else ps.setNull(4, Types.DECIMAL);
                ps.setString(5, actualites);
                ps.setString(6, codeCommune);
                ps.executeUpdate();
            }
        } else {
            String ins =
                "INSERT INTO espace_club " +
                "(nom_club, code_commune, adresse, horaires, montant_cotisation, actualites) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.get();
                 PreparedStatement ps = conn.prepareStatement(ins)) {
                ps.setString(1, nomClub != null ? nomClub : "Club " + codeCommune);
                ps.setString(2, codeCommune);
                ps.setString(3, adresse);
                ps.setString(4, horaires);
                if (cotisation != null) ps.setDouble(5, cotisation);
                else ps.setNull(5, Types.DECIMAL);
                ps.setString(6, actualites);
                ps.executeUpdate();
            }
        }
    }

    // ── Mapping ResultSet → EspaceClub ────────────────────────────────
    private EspaceClub map(ResultSet rs) throws SQLException {
        EspaceClub e = new EspaceClub();
        e.setIdEspace(rs.getInt("id_espace"));
        e.setNomClub(rs.getString("nom_club"));
        e.setCodeCommune(rs.getString("code_commune"));
        e.setCodeFederation(rs.getString("code_federation"));
        int resp = rs.getInt("id_responsable");
        e.setIdResponsable(rs.wasNull() ? null : resp);
        e.setAdresse(rs.getString("adresse"));
        e.setMontantCotisation(rs.getDouble("montant_cotisation"));
        e.setDateCreation(rs.getTimestamp("date_creation"));
        e.setDateModification(rs.getTimestamp("date_modification"));
        return e;
    }

    private String str(Map<String, Object> m, String key) {
        Object v = m.get(key); return v != null ? v.toString() : null;
    }
    private Double dbl(Map<String, Object> m, String key) {
        try { return m.get(key) != null ? Double.parseDouble(m.get(key).toString()) : null; }
        catch (NumberFormatException e) { return null; }
    }
}