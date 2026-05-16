package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.Actualite;
import com.esigelec.clubsport.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la table actualites.
 * Gère les publications d'un club à destination du grand public.
 */
public class ActualiteDAO implements DAO<Actualite, Integer> {

    // ── INSERT ────────────────────────────────────────────────────────
    @Override
    public Actualite insert(Actualite a) throws SQLException {
        String sql = "INSERT INTO actualites (id_espace, titre, contenu, categorie) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getIdEspace());
            ps.setString(2, a.getTitre());
            ps.setString(3, a.getContenu());
            ps.setString(4, a.getCategorie());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setIdActualite(keys.getInt(1));
            }
        }
        return a;
    }

    // ── FIND BY ID ────────────────────────────────────────────────────
    @Override
    public Actualite findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM actualites WHERE id_actualite = ?";
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
    public List<Actualite> findAll() throws SQLException {
        String sql = "SELECT * FROM actualites ORDER BY date_publication DESC";
        List<Actualite> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /**
     * Retourne toutes les actualités d'un espace club,
     * triées de la plus récente à la plus ancienne.
     */
    public List<Actualite> findByIdEspace(int idEspace) throws SQLException {
        String sql = "SELECT * FROM actualites WHERE id_espace = ? " +
                     "ORDER BY date_publication DESC";
        List<Actualite> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspace);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /**
     * Retourne les N dernières actualités d'un espace club.
     */
    public List<Actualite> findLatestByIdEspace(int idEspace, int limit) throws SQLException {
        String sql = "SELECT * FROM actualites WHERE id_espace = ? " +
                     "ORDER BY date_publication DESC LIMIT ?";
        List<Actualite> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspace);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ── UPDATE ────────────────────────────────────────────────────────
    @Override
    public void update(Actualite a) throws SQLException {
        String sql = "UPDATE actualites SET titre=?, contenu=?, categorie=? " +
                     "WHERE id_actualite=?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getTitre());
            ps.setString(2, a.getContenu());
            ps.setString(3, a.getCategorie());
            ps.setInt(4, a.getIdActualite());
            ps.executeUpdate();
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────
    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM actualites WHERE id_actualite = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── Mapping ResultSet → Actualite ─────────────────────────────────
    private Actualite map(ResultSet rs) throws SQLException {
        Actualite a = new Actualite();
        a.setIdActualite(rs.getInt("id_actualite"));
        a.setIdEspace(rs.getInt("id_espace"));
        a.setTitre(rs.getString("titre"));
        a.setContenu(rs.getString("contenu"));
        a.setCategorie(rs.getString("categorie"));
        a.setDatePublication(rs.getTimestamp("date_publication"));
        a.setDateModification(rs.getTimestamp("date_modification"));
        return a;
    }
}