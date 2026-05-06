package com.esigelec.clubsport.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connexion MySQL.
 * host.docker.internal = ton PC Windows vu depuis Docker (Tomcat).
 */
public class DBConnection {

    private static final String URL  =
        "jdbc:mysql://host.docker.internal:3306/clubs_sportifs"
        + "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER = "localhost";
    private static final String PASS = ""; 

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new RuntimeException("Driver MySQL introuvable", e); }
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private DBConnection() {}
}