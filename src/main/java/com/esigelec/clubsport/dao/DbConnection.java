package com.esigelec.clubsport.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestion de la connexion à la base MySQL.
 * Note : pour un vrai projet, utiliser un DataSource JNDI ou HikariCP.
 */
public class DbConnection {

    // ATTENTION : quand le code tourne dans le conteneur Tomcat, 
    // "localhost" ne marche pas -> utiliser le nom du service Docker "mysql"
    private static final String URL  = "jdbc:mysql://mysql:3306/clubs_sportifs?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "rootpassword";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL introuvable", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}