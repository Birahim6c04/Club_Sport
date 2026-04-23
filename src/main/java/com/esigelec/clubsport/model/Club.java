package com.esigelec.clubsport.model;

public class Club {

    private String codeCommune;
    private String nomCommune;
    private String region;
    private String departement;
    private Double latitude;
    private Double longitude;
    private String codeFederation;
    private String nomFederation;
    private int clubs;
    private int epa;
    private int total;
    private Double distanceKm;  // uniquement pour recherche par rayon

    public Club() {}

    public String getCodeCommune() { return codeCommune; }
    public void setCodeCommune(String v) { this.codeCommune = v; }

    public String getNomCommune() { return nomCommune; }
    public void setNomCommune(String v) { this.nomCommune = v; }

    public String getRegion() { return region; }
    public void setRegion(String v) { this.region = v; }

    public String getDepartement() { return departement; }
    public void setDepartement(String v) { this.departement = v; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double v) { this.latitude = v; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double v) { this.longitude = v; }

    public String getCodeFederation() { return codeFederation; }
    public void setCodeFederation(String v) { this.codeFederation = v; }

    public String getNomFederation() { return nomFederation; }
    public void setNomFederation(String v) { this.nomFederation = v; }

    public int getClubs() { return clubs; }
    public void setClubs(int v) { this.clubs = v; }

    public int getEpa() { return epa; }
    public void setEpa(int v) { this.epa = v; }

    public int getTotal() { return total; }
    public void setTotal(int v) { this.total = v; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double v) { this.distanceKm = v; }
}