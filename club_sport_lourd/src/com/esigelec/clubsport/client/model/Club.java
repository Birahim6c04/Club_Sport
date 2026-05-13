package com.esigelec.clubsport.client.model;

public class Club {

    private String nomCommune;
    private String departement;
    private String region;
    private String nomFederation;
    private int clubs;
    private int epa;
    private int total;
    private int licencesH;
    private int licencesF;

    public String getNomCommune() { return nomCommune; }
    public void setNomCommune(String n) { this.nomCommune = n; }

    public String getDepartement() { return departement; }
    public void setDepartement(String d) { this.departement = d; }

    public String getRegion() { return region; }
    public void setRegion(String r) { this.region = r; }

    public String getNomFederation() { return nomFederation; }
    public void setNomFederation(String f) { this.nomFederation = f; }

    public int getClubs() { return clubs; }
    public void setClubs(int c) { this.clubs = c; }

    public int getEpa() { return epa; }
    public void setEpa(int e) { this.epa = e; }

    public int getTotal() { return total; }
    public void setTotal(int t) { this.total = t; }

    public int getLicencesH() { return licencesH; }
    public void setLicencesH(int h) { this.licencesH = h; }

    public int getLicencesF() { return licencesF; }
    public void setLicencesF(int f) { this.licencesF = f; }
}