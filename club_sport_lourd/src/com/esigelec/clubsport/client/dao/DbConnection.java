package com.esigelec.clubsport.client.dao;
 
import java.sql.Connection;
import java.sql.DriverManager;
 
/**
* Connexion à la base MySQL pour le client lourd.
* On utilise localhost car le client tourne sur le PC, pas dans Docker.
*/
public class DbConnection {
 
    private static final String URL  = "jdbc:mysql://localhost:3306/clubs_sportifs?allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "rootpassword";
 
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}