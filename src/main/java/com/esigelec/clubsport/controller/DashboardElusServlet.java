package com.esigelec.clubsport.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esigelec.clubsport.dao.LicenceStatsDAO;

@WebServlet("/ElusDashboard") // associe la servlet à cette URL 
public class DashboardElusServlet extends HttpServlet { // la classe DashboardElusServlet herite de la classe HttpServlet(devient une servlet web)

    private static final long serialVersionUID = 1L; // identifiant unique de version de sérialisation 

    @Override // je prend la methode de httpservlet doGet et je la remplace par mon propre code 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) // quand un user fait une requête cette methode doGet s'execute et lui envoie une réponse.
            throws ServletException, IOException { 
    	
        try {  
        	
            LicenceStatsDAO dao = new LicenceStatsDAO(); // Crée un objet DAO pour appeler les méthodes qui récupèrent les données depuis la base
            // Récuperation des valeurs de chaque paramètre envoyé dans l'url ou le formulaire
            String region = request.getParameter("region");
            String departement = request.getParameter("departement");
            String codeCommune = request.getParameter("codeCommune");
            String federation = request.getParameter("federation");

           // Récuperation de la liste complète de chacun des parametres 
            List<String> regions = dao.listerRegions();
            List<String> departements = dao.listerDepartementsParRegion(region);
            List<String[]> communes = dao.listerCommunesParFiltres(region, departement);
            List<String> federations = dao.listerFederationsParFiltres(region, departement, codeCommune);


            if (federation != null && !federation.isEmpty() && !federations.contains(federation)) {
                federation = "";
            } // vérifier que la fédération sélectionnée est valide par rapport aux filtres actuels, sinon elle est réinitialisée.

            // recuperer ces parametres dans les methodes du fichier DAO 
            int total = dao.getTotalFiltre(region, departement, federation, codeCommune);
            int femmes = dao.getTotalFemmesFiltre(region, departement, federation, codeCommune);
            int hommes = dao.getTotalHommesFiltre(region, departement, federation, codeCommune);

            //envoyer les listes des parametres demandés a la page JSP
            request.setAttribute("regions", regions);
            request.setAttribute("departements", departements);
            request.setAttribute("communes", communes);
            request.setAttribute("federations", federations);
            
            // envoie les totaux a la page JSP
            request.setAttribute("total", total);
            request.setAttribute("femmes", femmes);
            request.setAttribute("hommes", hommes);
            
            
            // Permet de conserver la selection dans le formulaire 
            request.setAttribute("region", region);
            request.setAttribute("departement", departement);
            request.setAttribute("codeCommune", codeCommune);
            request.setAttribute("federation", federation);

            request.getRequestDispatcher("/elus.jsp").forward(request, response); // Transfert de la requête et de la réponse vers la page elus.jsp

        } catch (Exception e) { // pour capturer les erreurs du bloc try . 
            e.printStackTrace(); // pour afficher les erreurs dans la console 

            request.setAttribute("erreur", "Erreur lors du chargement du dashboard élus."); 
            request.getRequestDispatcher("/elus.jsp").forward(request, response);
        }
    }
}