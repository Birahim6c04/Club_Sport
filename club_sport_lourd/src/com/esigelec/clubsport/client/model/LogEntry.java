package com.esigelec.clubsport.client.model;
 
import java.sql.Timestamp;
 
public class LogEntry {
 
    private long id;
    private String login;
    private String adresseIp;
    private boolean succes;
    private Timestamp date;
    private String typeRecherche;
    private String criteres;
 
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
 
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
 
    public String getAdresseIp() {
        return adresseIp;
    }
    public void setAdresseIp(String adresseIp) {
        this.adresseIp = adresseIp;
    }
 
    public boolean isSucces() {
        return succes;
    }
    public void setSucces(boolean succes) {
        this.succes = succes;
    }
 
    public Timestamp getDate() {
        return date;
    }
    public void setDate(Timestamp date) {
        this.date = date;
    }
 
    public String getTypeRecherche() {
        return typeRecherche;
    }
    public void setTypeRecherche(String typeRecherche) {
        this.typeRecherche = typeRecherche;
    }
 
    public String getCriteres() {
        return criteres;
    }
    public void setCriteres(String criteres) {
        this.criteres = criteres;
    }
}