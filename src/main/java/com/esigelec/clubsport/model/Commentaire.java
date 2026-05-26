package com.esigelec.clubsport.model;

import java.util.Date;

public class Commentaire {
    private long id;
    private int idEspace;
    private int idUtilisateur;
    private String auteur;
    private String contenu;
    private Date dateCom;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getIdEspace() { return idEspace; }
    public void setIdEspace(int i) { this.idEspace = i; }

    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int i) { this.idUtilisateur = i; }

    public String getAuteur() { return auteur; }
    public void setAuteur(String a) { this.auteur = a; }

    public String getContenu() { return contenu; }
    public void setContenu(String c) { this.contenu = c; }

    public Date getDateCom() { return dateCom; }
    public void setDateCom(Date d) { this.dateCom = d; }
}
