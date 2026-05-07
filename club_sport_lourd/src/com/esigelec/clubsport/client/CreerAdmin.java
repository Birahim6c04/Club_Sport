package com.esigelec.clubsport.client;
 
import com.esigelec.clubsport.client.dao.DbConnection;
 
import org.mindrot.jbcrypt.BCrypt;
 
import java.sql.Connection;

import java.sql.PreparedStatement;
 
/**

* Petit script pour créer un compte ADMIN dans la base.

* À lancer une seule fois.

*/

public class CreerAdmin {
 
    public static void main(String[] args) throws Exception {

        // Modifie ces valeurs comme tu veux

        String login = "admin";

        String motDePasseClair = "admin123";

        String email = "admin@test.fr";

        String nom = "Admin";

        String prenom = "Test";
 
        // Hash du mot de passe avec BCrypt

        String motDePasseHash = BCrypt.hashpw(motDePasseClair, BCrypt.gensalt(10));
 
        System.out.println("Mot de passe en clair  : " + motDePasseClair);

        System.out.println("Mot de passe hashe     : " + motDePasseHash);
 
        // Insertion dans la base

        Connection conn = DbConnection.getConnection();
 
        String sql = "INSERT INTO utilisateur (login, mot_de_passe, email, nom, prenom, role, actif) " +

                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, login);

        ps.setString(2, motDePasseHash);

        ps.setString(3, email);

        ps.setString(4, nom);

        ps.setString(5, prenom);

        ps.setString(6, "ADMIN");

        ps.setBoolean(7, true);
 
        ps.executeUpdate();
 
        ps.close();

        conn.close();
 
        System.out.println("");

        System.out.println("Compte ADMIN cree avec succes !");

        System.out.println("Login        : " + login);

        System.out.println("Mot de passe : " + motDePasseClair);

    }

}
 