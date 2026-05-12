package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.Horaire;
import com.esigelec.clubsport.utils.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * DAO pour la table horaires.
 */
public class HorairesDAO implements DAO<Horaire, Integer> {

    @Override
    public Horaire insert(Horaire h) throws SQLException {
        String sql = "INSERT INTO horaires " +
                     "(id_espace, jour, heure_debut, heure_fin, activite, niveau) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, h.getIdEspace());
            ps.setString(2, h.getJour());
            ps.setTime(3, h.getHeureDebut());
            ps.setTime(4, h.getHeureFin());
            ps.setString(5, h.getActivite());
            ps.setString(6, h.getNiveau());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) h.setIdHoraire(keys.getInt(1));
            }
        }
        return h;
    }

    @Override
    public Horaire findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM horaires WHERE id_horaire = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    @Override
    public List<Horaire> findAll() throws SQLException {
        String sql = "SELECT * FROM horaires ORDER BY " +
                     "FIELD(jour,'Lundi','Mardi','Mercredi','Jeudi','Vendredi','Samedi','Dimanche'), " +
                     "heure_debut";
        List<Horaire> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    /** Retourne tous les horaires d'un espace club triés par jour et heure. */
    public List<Horaire> findByIdEspace(int idEspace) throws SQLException {
        String sql = "SELECT * FROM horaires WHERE id_espace = ? ORDER BY " +
                     "FIELD(jour,'Lundi','Mardi','Mercredi','Jeudi','Vendredi','Samedi','Dimanche'), " +
                     "heure_debut";
        List<Horaire> list = new ArrayList<>();
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspace);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public void update(Horaire h) throws SQLException {
        String sql = "UPDATE horaires SET jour=?, heure_debut=?, heure_fin=?, " +
                     "activite=?, niveau=? WHERE id_horaire=?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, h.getJour());
            ps.setTime(2, h.getHeureDebut());
            ps.setTime(3, h.getHeureFin());
            ps.setString(4, h.getActivite());
            ps.setString(5, h.getNiveau());
            ps.setInt(6, h.getIdHoraire());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM horaires WHERE id_horaire = ?";
        try (Connection conn = DBConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Horaire map(ResultSet rs) throws SQLException {
        Horaire h = new Horaire();
        h.setIdHoraire(rs.getInt("id_horaire"));
        h.setIdEspace(rs.getInt("id_espace"));
        h.setJour(rs.getString("jour"));
        h.setHeureDebut(rs.getTime("heure_debut"));
        h.setHeureFin(rs.getTime("heure_fin"));
        h.setActivite(rs.getString("activite"));
        h.setNiveau(rs.getString("niveau"));
        h.setDateCreation(rs.getTimestamp("date_creation"));
        h.setDateModification(rs.getTimestamp("date_modification"));
        return h;
    }
}