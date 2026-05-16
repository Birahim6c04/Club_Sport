package com.esigelec.clubsport.model;

public class EspaceClub {

    private int idEspace;
    private int idResponsable;
    private String slug;
    private String nomClub;
    private String description;
    private String actualites;
    private String horaires;
    private double montantCotisation;
    private String contactTel;
    private String contactEmail;
    private String adresse;
    private String codeCommune;
    private String codeFederation;
    private String photo;

    public int getIdEspace() { return idEspace; }
    public void setIdEspace(int idEspace) { this.idEspace = idEspace; }

    public int getIdResponsable() { return idResponsable; }
    public void setIdResponsable(int id) { this.idResponsable = id; }

    public String getSlug() { return slug; }
    public void setSlug(String s) { this.slug = s; }

    public String getNomClub() { return nomClub; }
    public void setNomClub(String n) { this.nomClub = n; }

    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }

    public String getActualites() { return actualites; }
    public void setActualites(String a) { this.actualites = a; }

    public String getHoraires() { return horaires; }
    public void setHoraires(String h) { this.horaires = h; }

    public double getMontantCotisation() { return montantCotisation; }
    public void setMontantCotisation(double m) { this.montantCotisation = m; }

    public String getContactTel() { return contactTel; }
    public void setContactTel(String t) { this.contactTel = t; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String e) { this.contactEmail = e; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String a) { this.adresse = a; }

    public String getCodeCommune() { return codeCommune; }
    public void setCodeCommune(String c) { this.codeCommune = c; }

    public String getCodeFederation() { return codeFederation; }
    public void setCodeFederation(String c) { this.codeFederation = c; }

    public String getPhoto() { return photo; }
    public void setPhoto(String p) { this.photo = p; }
}