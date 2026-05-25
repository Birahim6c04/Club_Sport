package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.Like;
import com.esigelec.clubsport.utils.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * DAO pour la table likes.
 * Gère le système de "j'aime" sur les actualités.
 * La contrainte UNIQUE (id_actualite, id_utilisateur) empêche les doublons en BDD.
 *
 * Chemin : src/main/java/com/esigelec/clubsport/dao/LikeDAO.java
 */
public class LikeDAO implements DAO<Like, Integer> {

    // ── INSERT ────────────────────────────────────────────────────────
    @Override
    public Like insert(Like l) throws SQLException {
        String sql = "INSERT INTO likes (id_actualite, id_utilisateur) VALUES (?, ?)";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, l.getIdActualite());
            ps.setInt(2, l.getIdUtilisateur());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) l.setIdLike(keys.getInt(1));
            }
        }
        return l;
    }

    // ── FIND BY ID ────────────────────────────────────────────────────
    @Override
    public Like findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM likes WHERE id_like = ?";
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
    public List<Like> findAll() throws SQLException {
        String sql = "SELECT * FROM likes ORDER BY date_creation DESC";
        List<Like> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /**
     * Vérifie si un utilisateur a déjà liké une actualité.
     * Retourne true si le like existe.
     */
    public boolean existsByActualiteAndUser(int idActualite, int idUtilisateur)
            throws SQLException {
        String sql = "SELECT 1 FROM likes WHERE id_actualite = ? AND id_utilisateur = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idActualite);
            ps.setInt(2, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Retourne le nombre total de likes d'une actualité.
     */
    public int countByIdActualite(int idActualite) throws SQLException {
        String sql = "SELECT COUNT(*) FROM likes WHERE id_actualite = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idActualite);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Supprime le like d'un utilisateur sur une actualité (toggle unlike).
     */
    public void deleteByActualiteAndUser(int idActualite, int idUtilisateur)
            throws SQLException {
        String sql = "DELETE FROM likes WHERE id_actualite = ? AND id_utilisateur = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idActualite);
            ps.setInt(2, idUtilisateur);
            ps.executeUpdate();
        }
    }

    // ── UPDATE — non applicable pour les likes ────────────────────────
    @Override
    public void update(Like l) throws SQLException {
        // Un like n'est pas modifiable, uniquement créé ou supprimé
    }

    // ── DELETE ────────────────────────────────────────────────────────
    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM likes WHERE id_like = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── Mapping ResultSet → Like ──────────────────────────────────────
    private Like map(ResultSet rs) throws SQLException {
        Like l = new Like();
        l.setIdLike(rs.getInt("id_like"));
        l.setIdActualite(rs.getInt("id_actualite"));
        l.setIdUtilisateur(rs.getInt("id_utilisateur"));
        l.setDateCreation(rs.getTimestamp("date_creation"));
        return l;
    }
}