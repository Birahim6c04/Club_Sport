package com.esigelec.clubsport.service;

import com.esigelec.clubsport.dao.UtilisateurDAO;
import com.esigelec.clubsport.dao.DbConnection;
import com.esigelec.clubsport.model.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AuthService {

    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Utilisateur connecter(String login, String motDePasseClair) throws Exception {
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("Le login est obligatoire");
        }
        if (motDePasseClair == null || motDePasseClair.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }

        Utilisateur u = utilisateurDAO.trouverParLogin(login);
        if (u == null) {
            throw new IllegalArgumentException("Login ou mot de passe incorrect");
        }

        if (u.getStatut().equals("EN_ATTENTE")) {
            throw new IllegalArgumentException("Votre compte est en attente de validation par un administrateur");
        }
        if (u.getStatut().equals("REFUSE")) {
            throw new IllegalArgumentException("Votre inscription a ete refusee");
        }
        if (!u.isActif()) {
            throw new IllegalArgumentException("Votre compte est desactive");
        }
        if (!BCrypt.checkpw(motDePasseClair, u.getMotDePasse())) {
            throw new IllegalArgumentException("Login ou mot de passe incorrect");
        }

        return u;
    }

    public void inscrire(String login, String motDePasseClair, String email,
                         String nom, String prenom, String role,
                         String cheminPieceJointe) throws Exception {
        if (login == null || login.length() < 3) {
            throw new IllegalArgumentException("Le login doit faire au moins 3 caracteres");
        }
        if (motDePasseClair == null || motDePasseClair.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit faire au moins 6 caracteres");
        }
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("L'email est invalide (exemple : jean@example.fr)");
        }
        if (nom == null || nom.isEmpty() || prenom == null || prenom.isEmpty()) {
            throw new IllegalArgumentException("Le nom et le prenom sont obligatoires");
        }
        if (!role.equals("ADMIN") && !role.equals("ELU") && !role.equals("CLUB")) {
            throw new IllegalArgumentException("Role invalide");
        }
        if (cheminPieceJointe == null || cheminPieceJointe.isEmpty()) {
            throw new IllegalArgumentException("La piece jointe est obligatoire");
        }
        if (utilisateurDAO.loginExiste(login)) {
            throw new IllegalArgumentException("Ce login est deja pris");
        }
        if (utilisateurDAO.emailExiste(email)) {
            throw new IllegalArgumentException("Cet email est deja utilise");
        }

        String hash = BCrypt.hashpw(motDePasseClair, BCrypt.gensalt(10));

        Utilisateur u = new Utilisateur();
        u.setLogin(login);
        u.setMotDePasse(hash);
        u.setEmail(email);
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setRole(role);
        u.setActif(false);
        u.setPieceJointe(cheminPieceJointe);
        u.setStatut("EN_ATTENTE");

        utilisateurDAO.creer(u);
    }

    // ============================================================
    // Enregistrement d'un log de connexion
    // ============================================================
    public void loggerConnexion(String login, String ip, boolean succes) {
        String sql = "INSERT INTO log_connexion (login_tente, adresse_ip, succes, date_tentative) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, ip);
            ps.setBoolean(3, succes);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}