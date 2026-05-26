package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.Commentaire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InteractionDAO {

    // ---------- LIKES ----------

    // Compte les likes d'un espace
    public int compterLikes(int idEspace) throws Exception {
        String sql = "SELECT COUNT(*) FROM pub_like WHERE id_espace = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspace);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // Verifie si l'utilisateur a deja like
    public boolean aLike(int idEspace, int idUtilisateur) throws Exception {
        String sql = "SELECT 1 FROM pub_like WHERE id_espace = ? AND id_utilisateur = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspace);
            ps.setInt(2, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Ajoute ou retire un like (toggle)
    public boolean toggleLike(int idEspace, int idUtilisateur) throws Exception {
        if (aLike(idEspace, idUtilisateur)) {
            String sql = "DELETE FROM pub_like WHERE id_espace = ? AND id_utilisateur = ?";
            try (Connection conn = DbConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEspace);
                ps.setInt(2, idUtilisateur);
                ps.executeUpdate();
            }
            return false; // n'aime plus
        } else {
            String sql = "INSERT INTO pub_like (id_espace, id_utilisateur) VALUES (?, ?)";
            try (Connection conn = DbConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEspace);
                ps.setInt(2, idUtilisateur);
                ps.executeUpdate();
            }
            return true; // aime
        }
    }

    // ---------- COMMENTAIRES ----------

    public void ajouterCommentaire(int idEspace, int idUtilisateur, String auteur, String contenu) throws Exception {
        String sql = "INSERT INTO pub_commentaire (id_espace, id_utilisateur, auteur, contenu) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspace);
            ps.setInt(2, idUtilisateur);
            ps.setString(3, auteur);
            ps.setString(4, contenu);
            ps.executeUpdate();
        }
    }

    public List<Commentaire> listerCommentaires(int idEspace) throws Exception {
        List<Commentaire> liste = new ArrayList<>();
        String sql = "SELECT * FROM pub_commentaire WHERE id_espace = ? ORDER BY date_com DESC";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspace);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Commentaire c = new Commentaire();
                    c.setId(rs.getLong("id_com"));
                    c.setIdEspace(rs.getInt("id_espace"));
                    c.setIdUtilisateur(rs.getInt("id_utilisateur"));
                    c.setAuteur(rs.getString("auteur"));
                    c.setContenu(rs.getString("contenu"));
                    c.setDateCom(rs.getTimestamp("date_com"));
                    liste.add(c);
                }
            }
        }
        return liste;
    }
}
