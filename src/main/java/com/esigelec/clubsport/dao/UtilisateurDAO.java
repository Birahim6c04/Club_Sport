package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.Utilisateur;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UtilisateurDAO {

    // Trouver un utilisateur par login
    public Utilisateur trouverParLogin(String login) throws Exception {
        Utilisateur u = null;
        String sql = "SELECT * FROM utilisateur WHERE login = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = new Utilisateur();
                    u.setId(rs.getInt("id_utilisateur"));
                    u.setLogin(rs.getString("login"));
                    u.setMotDePasse(rs.getString("mot_de_passe"));
                    u.setEmail(rs.getString("email"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setRole(rs.getString("role"));
                    u.setActif(rs.getBoolean("actif"));
                    u.setPieceJointe(rs.getString("piece_jointe"));
                    u.setStatut(rs.getString("statut"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return u;
    }

    // Vérifier si le login existe déjà
    public boolean loginExiste(String login) throws Exception {
        String sql = "SELECT 1 FROM utilisateur WHERE login = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean emailExiste(String email) throws Exception {
        String sql = "SELECT 1 FROM utilisateur WHERE email = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // Créer un nouvel utilisateur
    public void creer(Utilisateur u) throws Exception {
        String sql = "INSERT INTO utilisateur (login, mot_de_passe, email, nom, prenom, role, actif, piece_jointe, statut) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getLogin());
            ps.setString(2, u.getMotDePasse());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getNom());
            ps.setString(5, u.getPrenom());
            ps.setString(6, u.getRole());
            ps.setBoolean(7, u.isActif());
            ps.setString(8, u.getPieceJointe());
            ps.setString(9, u.getStatut());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}