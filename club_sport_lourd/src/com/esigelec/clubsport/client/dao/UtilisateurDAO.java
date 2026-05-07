package com.esigelec.clubsport.client.dao;
 
import com.esigelec.clubsport.client.model.Utilisateur;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
 
public class UtilisateurDAO {
 
    // Trouver un utilisateur par son login
    public Utilisateur trouverParLogin(String login) throws Exception {
        Connection conn = DbConnection.getConnection();
 
        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, login);
 
        ResultSet rs = ps.executeQuery();
 
        Utilisateur u = null;
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
        }
 
        rs.close();
        ps.close();
        conn.close();
 
        return u;
    }
 
    // Lister tous les utilisateurs
    public List<Utilisateur> listerTous() throws Exception {
        List<Utilisateur> liste = new ArrayList<>();
 
        Connection conn = DbConnection.getConnection();
 
        String sql = "SELECT * FROM utilisateur ORDER BY login";
        PreparedStatement ps = conn.prepareStatement(sql);
 
        ResultSet rs = ps.executeQuery();
 
        while (rs.next()) {
            Utilisateur u = new Utilisateur();
            u.setId(rs.getInt("id_utilisateur"));
            u.setLogin(rs.getString("login"));
            u.setEmail(rs.getString("email"));
            u.setNom(rs.getString("nom"));
            u.setPrenom(rs.getString("prenom"));
            u.setRole(rs.getString("role"));
            u.setActif(rs.getBoolean("actif"));
            liste.add(u);
        }
 
        rs.close();
        ps.close();
        conn.close();
 
        return liste;
    }
 
    // Activer ou désactiver un utilisateur
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
 
    // Supprimer un utilisateur
    public void supprimer(int id) throws Exception {
        Connection conn = DbConnection.getConnection();
 
        String sql = "DELETE FROM utilisateur WHERE id_utilisateur = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
 
        ps.executeUpdate();
 
        ps.close();
        conn.close();
    }
}