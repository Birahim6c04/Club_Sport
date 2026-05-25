package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.Abonnement;
import com.esigelec.clubsport.utils.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * DAO pour la table abonnements.
 * Un utilisateur connecté s'abonne à un espace club pour suivre ses actualités.
 *
 * Chemin : src/main/java/com/esigelec/clubsport/dao/AbonnementDAO.java
 */
public class AbonnementDAO implements DAO<Abonnement, Integer> {

    // ── INSERT ────────────────────────────────────────────────────────
    @Override
    public Abonnement insert(Abonnement a) throws SQLException {
        String sql = "INSERT INTO abonnements (id_utilisateur, id_espace) VALUES (?, ?)";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getIdUtilisateur());
            ps.setInt(2, a.getIdEspace());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setIdAbonnement(keys.getInt(1));
            }
        }
        return a;
    }

    // ── FIND BY ID ────────────────────────────────────────────────────
    @Override
    public Abonnement findById(Integer id) throws SQLException {
        String sql =
            "SELECT a.*, ec.nom_club, co.nom_commune " +
            "FROM abonnements a " +
            "JOIN espace_club ec ON ec.id_espace = a.id_espace " +
            "JOIN commune co     ON co.code_commune = ec.code_commune " +
            "WHERE a.id_abonnement = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    // ── FIND ALL ──────────────────────────────────────────────────────
    @Override
    public List<Abonnement> findAll() throws SQLException {
        return new ArrayList<>();
    }

    /**
     * Retourne tous les clubs auxquels un utilisateur est abonné.
     * Jointure avec espace_club et commune pour afficher le nom du club.
     */
    public List<Abonnement> findByIdUtilisateur(int idUtilisateur) throws SQLException {
        String sql =
            "SELECT a.*, ec.nom_club, co.nom_commune " +
            "FROM abonnements a " +
            "JOIN espace_club ec ON ec.id_espace    = a.id_espace " +
            "JOIN commune co     ON co.code_commune = ec.code_commune " +
            "WHERE a.id_utilisateur = ? " +
            "ORDER BY a.date_abonnement DESC";
        List<Abonnement> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /**
     * Vérifie si un utilisateur est déjà abonné à un espace club.
     */
    public boolean existsByUserAndEspace(int idUtilisateur, int idEspace)
            throws SQLException {
        String sql = "SELECT 1 FROM abonnements WHERE id_utilisateur = ? AND id_espace = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ps.setInt(2, idEspace);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Retourne le nombre d'abonnés d'un espace club.
     */
    public int countByIdEspace(int idEspace) throws SQLException {
        String sql = "SELECT COUNT(*) FROM abonnements WHERE id_espace = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspace);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Se désabonner d'un club (toggle).
     */
    public void deleteByUserAndEspace(int idUtilisateur, int idEspace)
            throws SQLException {
        String sql = "DELETE FROM abonnements WHERE id_utilisateur = ? AND id_espace = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ps.setInt(2, idEspace);
            ps.executeUpdate();
        }
    }

    // ── UPDATE — non applicable ────────────────────────────────────────
    @Override
    public void update(Abonnement a) throws SQLException {}

    // ── DELETE ────────────────────────────────────────────────────────
    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM abonnements WHERE id_abonnement = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── Mapping ResultSet → Abonnement ────────────────────────────────
    private Abonnement map(ResultSet rs) throws SQLException {
        Abonnement a = new Abonnement();
        a.setIdAbonnement(rs.getInt("id_abonnement"));
        a.setIdUtilisateur(rs.getInt("id_utilisateur"));
        a.setIdEspace(rs.getInt("id_espace"));
        a.setDateAbonnement(rs.getTimestamp("date_abonnement"));
        try { a.setNomClub(rs.getString("nom_club"));       } catch (SQLException ignored) {}
        try { a.setNomCommune(rs.getString("nom_commune")); } catch (SQLException ignored) {}
        return a;
    }
}