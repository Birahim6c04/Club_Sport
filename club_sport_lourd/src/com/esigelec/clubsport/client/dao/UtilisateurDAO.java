package com.esigelec.clubsport.client.dao;

import com.esigelec.clubsport.client.model.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    public Utilisateur trouverParLogin(String login) throws Exception {
        Connection conn = DbConnection.getConnection();

        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, login);

        ResultSet rs = ps.executeQuery();

        Utilisateur u = null;
        if (rs.next()) {
            u = construireUtilisateur(rs);
        }

        rs.close();
        ps.close();
        conn.close();
        return u;
    }

    public List<Utilisateur> listerTous() throws Exception {
        List<Utilisateur> liste = new ArrayList<>();

        Connection conn = DbConnection.getConnection();
        String sql = "SELECT * FROM utilisateur ORDER BY login";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            liste.add(construireUtilisateur(rs));
        }

        rs.close();
        ps.close();
        conn.close();
        return liste;
    }

 // Liste TOUS les utilisateurs (sauf admin) - on peut valider/refuser à tout moment
    public List<Utilisateur> listerEnAttente() throws Exception {
        List<Utilisateur> liste = new ArrayList<>();

        Connection conn = DbConnection.getConnection();
        String sql = "SELECT * FROM utilisateur WHERE role != 'ADMIN' ORDER BY statut, date_creation DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            liste.add(construireUtilisateur(rs));
        }

        rs.close();
        ps.close();
        conn.close();
        return liste;
    }

    public void changerActivation(int id, boolean actif) throws Exception {
        Connection conn = DbConnection.getConnection();
        String sql = "UPDATE utilisateur SET actif = ? WHERE id_utilisateur = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setBoolean(1, actif);
        ps.setInt(2, id);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public void supprimer(int id) throws Exception {
        Connection conn = DbConnection.getConnection();
        String sql = "DELETE FROM utilisateur WHERE id_utilisateur = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    // Valider une inscription (statut = VALIDE + actif = TRUE)
    public void valider(int id) throws Exception {
        Connection conn = DbConnection.getConnection();
        String sql = "UPDATE utilisateur SET statut = 'VALIDE', actif = TRUE WHERE id_utilisateur = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    // Refuser une inscription (statut = REFUSE + actif = FALSE)
    public void refuser(int id) throws Exception {
        Connection conn = DbConnection.getConnection();
        String sql = "UPDATE utilisateur SET statut = 'REFUSE', actif = FALSE WHERE id_utilisateur = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    private Utilisateur construireUtilisateur(ResultSet rs) throws Exception {
        Utilisateur u = new Utilisateur();
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
        return u;
    }
}