package com.esigelec.clubsport.dao;

import com.esigelec.clubsport.model.EspaceClub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EspaceClubDAO {

    // Trouver l'espace d'un utilisateur (par id_responsable)
    public EspaceClub trouverParResponsable(int idResponsable) throws Exception {
        Connection conn = DbConnection.getConnection();
        String sql = "SELECT * FROM espace_club WHERE id_responsable = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, idResponsable);
        ResultSet rs = ps.executeQuery();

        EspaceClub e = null;
        if (rs.next()) {
            e = construire(rs);
        }

        rs.close();
        ps.close();
        conn.close();
        return e;
    }

    // Trouver l'espace par son slug
    public EspaceClub trouverParSlug(String slug) throws Exception {
        Connection conn = DbConnection.getConnection();
        String sql = "SELECT * FROM espace_club WHERE slug = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, slug);
        ResultSet rs = ps.executeQuery();

        EspaceClub e = null;
        if (rs.next()) {
            e = construire(rs);
        }

        rs.close();
        ps.close();
        conn.close();
        return e;
    }

    // Lister tous les clubs (annuaire)
    public List<EspaceClub> listerTous() throws Exception {
        List<EspaceClub> liste = new ArrayList<>();
        Connection conn = DbConnection.getConnection();
        String sql = "SELECT * FROM espace_club WHERE slug IS NOT NULL ORDER BY nom_club ASC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            liste.add(construire(rs));
        }

        rs.close();
        ps.close();
        conn.close();
        return liste;
    }

    // Créer un espace
    public void creer(EspaceClub e) throws Exception {
        Connection conn = DbConnection.getConnection();
        String sql = "INSERT INTO espace_club (id_responsable, slug, nom_club, description, actualites, horaires, montant_cotisation, contact_tel, contact_email, adresse, photo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, e.getIdResponsable());
        ps.setString(2, e.getSlug());
        ps.setString(3, e.getNomClub());
        ps.setString(4, e.getDescription());
        ps.setString(5, e.getActualites());
        ps.setString(6, e.getHoraires());
        ps.setDouble(7, e.getMontantCotisation());
        ps.setString(8, e.getContactTel());
        ps.setString(9, e.getContactEmail());
        ps.setString(10, e.getAdresse());
        ps.setString(11, e.getPhoto());
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    // Modifier un espace
    public void modifier(EspaceClub e) throws Exception {
        Connection conn = DbConnection.getConnection();
        String sql = "UPDATE espace_club SET nom_club = ?, description = ?, actualites = ?, horaires = ?, montant_cotisation = ?, contact_tel = ?, contact_email = ?, adresse = ?, photo = ? " +
                     "WHERE id_responsable = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, e.getNomClub());
        ps.setString(2, e.getDescription());
        ps.setString(3, e.getActualites());
        ps.setString(4, e.getHoraires());
        ps.setDouble(5, e.getMontantCotisation());
        ps.setString(6, e.getContactTel());
        ps.setString(7, e.getContactEmail());
        ps.setString(8, e.getAdresse());
        ps.setString(9, e.getPhoto());
        ps.setInt(10, e.getIdResponsable());
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public boolean slugExiste(String slug) throws Exception {
        Connection conn = DbConnection.getConnection();
        String sql = "SELECT 1 FROM espace_club WHERE slug = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, slug);
        ResultSet rs = ps.executeQuery();
        boolean existe = rs.next();
        rs.close();
        ps.close();
        conn.close();
        return existe;
    }

    private EspaceClub construire(ResultSet rs) throws Exception {
        EspaceClub e = new EspaceClub();
        e.setIdEspace(rs.getInt("id_espace"));
        e.setIdResponsable(rs.getInt("id_responsable"));
        e.setSlug(rs.getString("slug"));
        e.setNomClub(rs.getString("nom_club"));
        e.setDescription(rs.getString("description"));
        e.setActualites(rs.getString("actualites"));
        e.setHoraires(rs.getString("horaires"));
        e.setMontantCotisation(rs.getDouble("montant_cotisation"));
        e.setContactTel(rs.getString("contact_tel"));
        e.setContactEmail(rs.getString("contact_email"));
        e.setAdresse(rs.getString("adresse"));
        e.setCodeCommune(rs.getString("code_commune"));
        e.setCodeFederation(rs.getString("code_federation"));
        e.setPhoto(rs.getString("photo"));
        return e;
    }
}