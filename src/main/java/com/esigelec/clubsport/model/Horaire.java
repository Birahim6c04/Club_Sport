package com.esigelec.clubsport.model;

import java.sql.Time;
import java.sql.Timestamp;

public class Horaire {
    private int       idHoraire;
    private int       idEspace;
    private String    jour;
    private Time      heureDebut;
    private Time      heureFin;
    private String    activite;
    private String    niveau;
    private Timestamp dateCreation;
    private Timestamp dateModification;

    public Horaire() {}

    public int     getIdHoraire()                    { return idHoraire; }
    public void    setIdHoraire(int v)               { this.idHoraire = v; }
    public int     getIdEspace()                     { return idEspace; }
    public void    setIdEspace(int v)                { this.idEspace = v; }
    public String  getJour()                         { return jour; }
    public void    setJour(String v)                 { this.jour = v; }
    public Time    getHeureDebut()                   { return heureDebut; }
    public void    setHeureDebut(Time v)             { this.heureDebut = v; }
    public Time    getHeureFin()                     { return heureFin; }
    public void    setHeureFin(Time v)               { this.heureFin = v; }
    public String  getActivite()                     { return activite; }
    public void    setActivite(String v)             { this.activite = v; }
    public String  getNiveau()                       { return niveau; }
    public void    setNiveau(String v)               { this.niveau = v; }
    public Timestamp getDateCreation()               { return dateCreation; }
    public void    setDateCreation(Timestamp v)      { this.dateCreation = v; }
    public Timestamp getDateModification()           { return dateModification; }
    public void    setDateModification(Timestamp v)  { this.dateModification = v; }
}