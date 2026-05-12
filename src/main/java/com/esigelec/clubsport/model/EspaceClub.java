package com.esigelec.clubsport.model;

import java.sql.Timestamp;

public class EspaceClub {
    private int       idEspace;
    private String    nomClub;
    private String    codeCommune;
    private String    codeFederation;
    private Integer   idResponsable;
    private String    adresse;
    private double    montantCotisation;
    private Timestamp dateCreation;
    private Timestamp dateModification;

    public EspaceClub() {}

    public int     getIdEspace()                    { return idEspace; }
    public void    setIdEspace(int v)               { this.idEspace = v; }
    public String  getNomClub()                     { return nomClub; }
    public void    setNomClub(String v)             { this.nomClub = v; }
    public String  getCodeCommune()                 { return codeCommune; }
    public void    setCodeCommune(String v)         { this.codeCommune = v; }
    public String  getCodeFederation()              { return codeFederation; }
    public void    setCodeFederation(String v)      { this.codeFederation = v; }
    public Integer getIdResponsable()               { return idResponsable; }
    public void    setIdResponsable(Integer v)      { this.idResponsable = v; }
    public String  getAdresse()                     { return adresse; }
    public void    setAdresse(String v)             { this.adresse = v; }
    public double  getMontantCotisation()           { return montantCotisation; }
    public void    setMontantCotisation(double v)   { this.montantCotisation = v; }
    public Timestamp getDateCreation()              { return dateCreation; }
    public void    setDateCreation(Timestamp v)     { this.dateCreation = v; }
    public Timestamp getDateModification()          { return dateModification; }
    public void    setDateModification(Timestamp v) { this.dateModification = v; }
}