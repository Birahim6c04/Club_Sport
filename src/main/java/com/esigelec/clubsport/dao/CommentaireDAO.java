package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.Commentaire;
import com.esigelec.clubsport.utils.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * DAO pour la table commentaires.
 * Les commentaires sont liés à une actualité et à un utilisateur connecté.
 *
 * Chemin : src/main/java/com/esigelec/clubsport/dao/CommentaireDAO.java
 */
public class CommentaireDAO implements DAO<Commentaire, Integer> {

    // ── INSERT ────────────────────────────────────────────────────────
    @Override
    public Commentaire insert(Commentaire c) throws SQLException {
        String sql = "INSERT INTO commentaires (id_actualite, id_utilisateur, contenu) " +
                     "VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getIdActualite());
            ps.setInt(2, c.getIdUtilisateur());
            ps.setString(3, c.getContenu());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setIdCommentaire(keys.getInt(1));
            }
        }
        return c;
    }

    // ── FIND BY ID ────────────────────────────────────────────────────
    @Override
    public Commentaire findById(Integer id) throws SQLException {
        String sql =
            "SELECT c.*, u.login, u.prenom, u.nom " +
            "FROM commentaires c " +
            "JOIN utilisateur u ON u.id_utilisateur = c.id_utilisateur " +
            "WHERE c.id_commentaire = ?";
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
    public List<Commentaire> findAll() throws SQLException {
        String sql =
            "SELECT c.*, u.login, u.prenom, u.nom FROM commentaires c " +
            "JOIN utilisateur u ON u.id_utilisateur = c.id_utilisateur " +
            "ORDER BY c.date_creation ASC";
        List<Commentaire> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /**
     * Retourne tous les commentaires d'une actualité,
     * triés du plus ancien au plus récent, avec login + nom de l'auteur.
     */
    public List<Commentaire> findByIdActualite(int idActualite) throws SQLException {
        String sql =
            "SELECT c.*, u.login, u.prenom, u.nom " +
            "FROM commentaires c " +
            "JOIN utilisateur u ON u.id_utilisateur = c.id_utilisateur " +
            "WHERE c.id_actualite = ? " +
            "ORDER BY c.date_creation ASC";
        List<Commentaire> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idActualite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /**
     * Retourne le nombre de commentaires d'une actualité.
     */
    public int countByIdActualite(int idActualite) throws SQLException {
        String sql = "SELECT COUNT(*) FROM commentaires WHERE id_actualite = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idActualite);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────
    @Override
    public void update(Commentaire c) throws SQLException {
        String sql = "UPDATE commentaires SET contenu = ? WHERE id_commentaire = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getContenu());
            ps.setInt(2, c.getIdCommentaire());
            ps.executeUpdate();
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────
    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM commentaires WHERE id_commentaire = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── Mapping ResultSet → Commentaire ──────────────────────────────
    private Commentaire map(ResultSet rs) throws SQLException {
        Commentaire c = new Commentaire();
        c.setIdCommentaire(rs.getInt("id_commentaire"));
        c.setIdActualite(rs.getInt("id_actualite"));
        c.setIdUtilisateur(rs.getInt("id_utilisateur"));
        c.setContenu(rs.getString("contenu"));
        c.setDateCreation(rs.getTimestamp("date_creation"));
        c.setDateModification(rs.getTimestamp("date_modification"));
        // Colonnes issues de la jointure avec utilisateur
        try { c.setLoginUtilisateur(rs.getString("login")); } catch (SQLException ignored) {}
        try {
            String prenom = rs.getString("prenom");
            String nom    = rs.getString("nom");
            c.setNomUtilisateur(prenom + " " + nom);
        } catch (SQLException ignored) {}
        return c;
    }
}