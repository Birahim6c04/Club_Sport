package com.esigelec.clubsport.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.esigelec.clubsport.model.Profil;

public class ProfilDAO {

    public void ajouterProfil(Profil profil) throws Exception {

        String sql =
            "INSERT INTO profil " +
            "(telephone, adresse, photo_profil, description, " +
            "fonction, commune, departement, region, " +
            "club_associe, fonction_club, id_utilisateur) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, profil.getTelephone());
            ps.setString(2, profil.getAdresse());
            ps.setString(3, profil.getPhotoProfil());
            ps.setString(4, profil.getDescription());

            ps.setString(5, profil.getFonction());
            ps.setString(6, profil.getCommune());
            ps.setString(7, profil.getDepartement());
            ps.setString(8, profil.getRegion());

            ps.setString(9, profil.getClubAssocie());
            ps.setString(10, profil.getFonctionClub());

            ps.setInt(11, profil.getIdUtilisateur());

            ps.executeUpdate();
        }
    }
    public Profil chercherProfilParUtilisateur(int idUtilisateur) throws Exception {

        String sql = "SELECT * FROM profil WHERE id_utilisateur = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUtilisateur);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Profil profil = new Profil();

                    profil.setIdProfil(rs.getInt("id_profil"));
                    profil.setTelephone(rs.getString("telephone"));
                    profil.setAdresse(rs.getString("adresse"));
                    profil.setPhotoProfil(rs.getString("photo_profil"));
                    profil.setDescription(rs.getString("description"));

                    profil.setFonction(rs.getString("fonction"));
                    profil.setCommune(rs.getString("commune"));
                    profil.setDepartement(rs.getString("departement"));
                    profil.setRegion(rs.getString("region"));

                    profil.setClubAssocie(rs.getString("club_associe"));
                    profil.setFonctionClub(rs.getString("fonction_club"));

                    profil.setIdUtilisateur(rs.getInt("id_utilisateur"));

                    return profil;
                }
            }
        }

        return null;
    }
    public void modifierProfil(Profil profil) throws Exception {

        String sql =
            "UPDATE profil SET " +
            "telephone = ?, adresse = ?, photo_profil = ?, description = ?, " +
            "fonction = ?, commune = ?, departement = ?, region = ?, " +
            "club_associe = ?, fonction_club = ? " +
            "WHERE id_utilisateur = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, profil.getTelephone());
            ps.setString(2, profil.getAdresse());
            ps.setString(3, profil.getPhotoProfil());
            ps.setString(4, profil.getDescription());

            ps.setString(5, profil.getFonction());
            ps.setString(6, profil.getCommune());
            ps.setString(7, profil.getDepartement());
            ps.setString(8, profil.getRegion());

            ps.setString(9, profil.getClubAssocie());
            ps.setString(10, profil.getFonctionClub());

            ps.setInt(11, profil.getIdUtilisateur());

            ps.executeUpdate();
        }
    }
}