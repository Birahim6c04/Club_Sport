package com.esigelec.clubsport.model;

public class ExportDashboardDTO {

    private String region;
    private String departement;
    private String commune;
    private String federation;

    private int totalLicencies;
    private int hommes;
    private int femmes;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getFederation() {
        return federation;
    }

    public void setFederation(String federation) {
        this.federation = federation;
    }

    public int getTotalLicencies() {
        return totalLicencies;
    }

    public void setTotalLicencies(int totalLicencies) {
        this.totalLicencies = totalLicencies;
    }

    public int getHommes() {
        return hommes;
    }

    public void setHommes(int hommes) {
        this.hommes = hommes;
    }

    public int getFemmes() {
        return femmes;
    }

    public void setFemmes(int femmes) {
        this.femmes = femmes;
    }
}