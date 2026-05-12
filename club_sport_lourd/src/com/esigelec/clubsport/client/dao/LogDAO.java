package com.esigelec.clubsport.client.dao;
 
import com.esigelec.clubsport.client.model.LogEntry;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
 
public class LogDAO {
 
    // Liste des logs de connexion
    public List<LogEntry> listerLogsConnexion() throws Exception {
        List<LogEntry> liste = new ArrayList<>();
 
        Connection conn = DbConnection.getConnection();
 
        String sql = "SELECT * FROM log_connexion ORDER BY date_tentative DESC LIMIT 200";
        PreparedStatement ps = conn.prepareStatement(sql);
 
        ResultSet rs = ps.executeQuery();
 
        while (rs.next()) {
            LogEntry log = new LogEntry();
            log.setId(rs.getLong("id_log"));
            log.setLogin(rs.getString("login_tente"));
            log.setAdresseIp(rs.getString("adresse_ip"));
            log.setSucces(rs.getBoolean("succes"));
            log.setDate(rs.getTimestamp("date_tentative"));
            liste.add(log);
        }
 
        rs.close();
        ps.close();
        conn.close();
 
        return liste;
    }
 
    // Liste des logs de recherche
    public List<LogEntry> listerLogsRecherche() throws Exception {
        List<LogEntry> liste = new ArrayList<>();
 
        Connection conn = DbConnection.getConnection();
 
        String sql = "SELECT * FROM log_recherche ORDER BY date_recherche DESC LIMIT 200";
        PreparedStatement ps = conn.prepareStatement(sql);
 
        ResultSet rs = ps.executeQuery();
 
        while (rs.next()) {
            LogEntry log = new LogEntry();
            log.setId(rs.getLong("id_log"));
            log.setLogin(rs.getString("login"));
            log.setAdresseIp(rs.getString("adresse_ip"));
            log.setTypeRecherche(rs.getString("type_recherche"));
            log.setCriteres(rs.getString("criteres"));
            log.setDate(rs.getTimestamp("date_recherche"));
            liste.add(log);
        }
 
        rs.close();
        ps.close();
        conn.close();
 
        return liste;
    }
}