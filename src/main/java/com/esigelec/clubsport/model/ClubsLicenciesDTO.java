package com.esigelec.clubsport.model;

public class ClubsLicenciesDTO {

    private String federation;
    private int clubs;
    private int licencies;

    public String getFederation() {
        return federation;
    }

    public void setFederation(String federation) {
        this.federation = federation;
    }

    public int getClubs() {
        return clubs;
    }

    public void setClubs(int clubs) {
        this.clubs = clubs;
    }

    public int getLicencies() {
        return licencies;
    }

    public void setLicencies(int licencies) {
        this.licencies = licencies;
    }
}