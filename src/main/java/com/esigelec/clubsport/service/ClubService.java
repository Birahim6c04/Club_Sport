package com.esigelec.clubsport.service;

import com.esigelec.clubsport.dao.ClubDAO;
import com.esigelec.clubsport.model.Club;

import java.util.List;

public class ClubService {

    private ClubDAO clubDAO = new ClubDAO();

 // Recherche par fédération et/ou région et/ou code postal
    public List<Club> rechercher(String codeFederation, String region, String codePostal) throws Exception {
        if ((codeFederation == null || codeFederation.isEmpty())
            && (region == null || region.isEmpty())
            && (codePostal == null || codePostal.isEmpty())) {
            throw new IllegalArgumentException(
                "Au moins un critère est requis : fédération, région ou code postal");
        }
        return clubDAO.rechercher(codeFederation, region, codePostal);
    }

    // Recherche par rayon autour d'une commune
    public List<Club> rechercherParRayon(String codeFederation, String codeCommune, int rayonKm) throws Exception {
        if (rayonKm != 10 && rayonKm != 20 && rayonKm != 50 && rayonKm != 100) {
            throw new IllegalArgumentException("Le rayon doit être 10, 20, 50 ou 100 km");
        }
        if (codeCommune == null || codeCommune.isEmpty()) {
            throw new IllegalArgumentException("La commune est obligatoire");
        }
        return clubDAO.rechercherParRayon(codeFederation, codeCommune, rayonKm);
    }

    // Liste des régions
    public List<String> listerRegions() throws Exception {
        return clubDAO.listerRegions();
    }

    // Recherche commune par nom (autocomplétion)
    public List<String[]> rechercherCommune(String query) throws Exception {
        if (query == null || query.length() < 2) {
            throw new IllegalArgumentException("Au moins 2 caractères sont requis");
        }
        return clubDAO.rechercherCommune(query);
    }
}