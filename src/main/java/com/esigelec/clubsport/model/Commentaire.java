package com.esigelec.clubsport.model;

import java.sql.Timestamp;

/**
 * Modèle représentant la table commentaires.
 * Un utilisateur connecté commente une actualité.
 *
 * Chemin : src/main/java/com/esigelec/clubsport/model/Commentaire.java
 */
public class Commentaire {

    private int       idCommentaire;
    private int       idActualite;
    private int       idUtilisateur;
    private String    contenu;
    private String    loginUtilisateur;  // jointure avec utilisateur (affichage)
    private String    nomUtilisateur;    // jointure avec utilisateur (affichage)
    private Timestamp dateCreation;
    private Timestamp dateModification;

    public Commentaire() {}

    public int    getIdCommentaire()                   { return idCommentaire; }
    public void   setIdCommentaire(int v)              { this.idCommentaire = v; }
    public int    getIdActualite()                     { return idActualite; }
    public void   setIdActualite(int v)                { this.idActualite = v; }
    public int    getIdUtilisateur()                   { return idUtilisateur; }
    public void   setIdUtilisateur(int v)              { this.idUtilisateur = v; }
    public String getContenu()                         { return contenu; }
    public void   setContenu(String v)                 { this.contenu = v; }
    public String getLoginUtilisateur()                { return loginUtilisateur; }
    public void   setLoginUtilisateur(String v)        { this.loginUtilisateur = v; }
    public String getNomUtilisateur()                  { return nomUtilisateur; }
    public void   setNomUtilisateur(String v)          { this.nomUtilisateur = v; }
    public Timestamp getDateCreation()                 { return dateCreation; }
    public void      setDateCreation(Timestamp v)      { this.dateCreation = v; }
    public Timestamp getDateModification()             { return dateModification; }
    public void      setDateModification(Timestamp v)  { this.dateModification = v; }
}