package com.esigelec.clubsport.model;

public class StatDTO {
    private String label;
    private int valeur;

    public StatDTO(String label, int valeur) {
        this.label = label;
        this.valeur = valeur;
    }

    public String getLabel() {
        return label;
    }

    public int getValeur() {
        return valeur;
    }
}