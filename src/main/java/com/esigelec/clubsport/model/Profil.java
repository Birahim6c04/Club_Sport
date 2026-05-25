package com.esigelec.clubsport.model;

public class Profil {

    private int idProfil;
    private String telephone;
    private String adresse;
    private String photoProfil;
    private String description;

    private String fonction;
    private String commune;
    private String departement;
    private String region;

    private String clubAssocie;
    private String fonctionClub;

    private int idUtilisateur;

	public int getIdProfil() {
		return idProfil;
	}

	public void setIdProfil(int idProfil) {
		this.idProfil = idProfil;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getPhotoProfil() {
		return photoProfil;
	}

	public void setPhotoProfil(String photoProfil) {
		this.photoProfil = photoProfil;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFonction() {
		return fonction;
	}

	public void setFonction(String fonction) {
		this.fonction = fonction;
	}

	public String getCommune() {
		return commune;
	}

	public void setCommune(String commune) {
		this.commune = commune;
	}

	public String getDepartement() {
		return departement;
	}

	public void setDepartement(String departement) {
		this.departement = departement;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getClubAssocie() {
		return clubAssocie;
	}

	public void setClubAssocie(String clubAssocie) {
		this.clubAssocie = clubAssocie;
	}

	public String getFonctionClub() {
		return fonctionClub;
	}

	public void setFonctionClub(String fonctionClub) {
		this.fonctionClub = fonctionClub;
	}

	public int getIdUtilisateur() {
		return idUtilisateur;
	}

	public void setIdUtilisateur(int idUtilisateur) {
		this.idUtilisateur = idUtilisateur;
	}

}