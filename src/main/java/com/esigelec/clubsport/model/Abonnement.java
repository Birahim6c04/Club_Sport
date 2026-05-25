package com.esigelec.clubsport.model;

import java.sql.Timestamp;

/**
 * Modèle représentant la table abonnements.
 * Un utilisateur s'abonne à un espace club pour suivre ses actualités.
 *
 * Chemin : src/main/java/com/esigelec/clubsport/model/Abonnement.java
 */
public class Abonnement {

    private int       idAbonnement;
    private int       idUtilisateur;
    private int       idEspace;
    private String    nomClub;       // jointure avec espace_club (affichage)
    private String    nomCommune;    // jointure avec commune (affichage)
    private Timestamp dateAbonnement;

    public Abonnement() {}

    public int    getIdAbonnement()                    { return idAbonnement; }
    public void   setIdAbonnement(int v)               { this.idAbonnement = v; }
    public int    getIdUtilisateur()                   { return idUtilisateur; }
    public void   setIdUtilisateur(int v)              { this.idUtilisateur = v; }
    public int    getIdEspace()                        { return idEspace; }
    public void   setIdEspace(int v)                   { this.idEspace = v; }
    public String getNomClub()                         { return nomClub; }
    public void   setNomClub(String v)                 { this.nomClub = v; }
    public String getNomCommune()                      { return nomCommune; }
    public void   setNomCommune(String v)              { this.nomCommune = v; }
    public Timestamp getDateAbonnement()               { return dateAbonnement; }
    public void      setDateAbonnement(Timestamp v)    { this.dateAbonnement = v; }
}