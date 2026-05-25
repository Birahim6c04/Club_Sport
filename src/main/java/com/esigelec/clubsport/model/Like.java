package com.esigelec.clubsport.model;

import java.sql.Timestamp;

/**
 * Modèle représentant la table likes.
 * Un utilisateur connecté like une actualité (1 like par user/actu).
 *
 * Chemin : src/main/java/com/esigelec/clubsport/model/Like.java
 */
public class Like {

    private int       idLike;
    private int       idActualite;
    private int       idUtilisateur;
    private Timestamp dateCreation;

    public Like() {}

    public int    getIdLike()                      { return idLike; }
    public void   setIdLike(int v)                 { this.idLike = v; }
    public int    getIdActualite()                 { return idActualite; }
    public void   setIdActualite(int v)            { this.idActualite = v; }
    public int    getIdUtilisateur()               { return idUtilisateur; }
    public void   setIdUtilisateur(int v)          { this.idUtilisateur = v; }
    public Timestamp getDateCreation()             { return dateCreation; }
    public void      setDateCreation(Timestamp v)  { this.dateCreation = v; }
}