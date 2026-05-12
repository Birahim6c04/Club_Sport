package com.esigelec.clubsport.model;

import java.sql.Timestamp;

public class Actualite {
    private int       idActualite;
    private int       idEspace;
    private String    titre;
    private String    contenu;
    private String    categorie;
    private Timestamp datePublication;
    private Timestamp dateModification;

    public Actualite() {}

    public int     getIdActualite()                   { return idActualite; }
    public void    setIdActualite(int v)              { this.idActualite = v; }
    public int     getIdEspace()                      { return idEspace; }
    public void    setIdEspace(int v)                 { this.idEspace = v; }
    public String  getTitre()                         { return titre; }
    public void    setTitre(String v)                 { this.titre = v; }
    public String  getContenu()                       { return contenu; }
    public void    setContenu(String v)               { this.contenu = v; }
    public String  getCategorie()                     { return categorie; }
    public void    setCategorie(String v)             { this.categorie = v; }
    public Timestamp getDatePublication()             { return datePublication; }
    public void    setDatePublication(Timestamp v)    { this.datePublication = v; }
    public Timestamp getDateModification()            { return dateModification; }
    public void    setDateModification(Timestamp v)   { this.dateModification = v; }
}