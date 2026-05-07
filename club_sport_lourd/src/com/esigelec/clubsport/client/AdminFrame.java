package com.esigelec.clubsport.client;
 
import com.esigelec.clubsport.client.dao.LogDAO;
import com.esigelec.clubsport.client.dao.UtilisateurDAO;
import com.esigelec.clubsport.client.model.LogEntry;
import com.esigelec.clubsport.client.model.Utilisateur;
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
 
public class AdminFrame extends JFrame {
 
    private Utilisateur adminConnecte;
 
    private DefaultTableModel modeleUtilisateurs;
    private JTable tableUtilisateurs;
 
    private DefaultTableModel modeleLogsConnexion;
    private JTable tableLogsConnexion;
 
    private DefaultTableModel modeleLogsRecherche;
    private JTable tableLogsRecherche;
 
    public AdminFrame(Utilisateur admin) {
        this.adminConnecte = admin;
 
        setTitle("Administration - Connecte : " + admin.getLogin());
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        // Création des onglets
        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Utilisateurs", creerOngletUtilisateurs());
        onglets.addTab("Logs de connexion", creerOngletLogsConnexion());
        onglets.addTab("Logs de recherche", creerOngletLogsRecherche());
 
        getContentPane().add(onglets);
        setVisible(true);
 
        // Charger les données au démarrage
        chargerUtilisateurs();
        chargerLogsConnexion();
        chargerLogsRecherche();
    }
 
    // ========================================================
    // ONGLET 1 : UTILISATEURS
    // ========================================================
    private JPanel creerOngletUtilisateurs() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
 
        // Titre
        JLabel titre = new JLabel("Liste des utilisateurs");
        titre.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titre, BorderLayout.NORTH);
 
        // Tableau
        String[] colonnes = {"ID", "Login", "Email", "Nom", "Prenom", "Role", "Actif"};
        modeleUtilisateurs = new DefaultTableModel(colonnes, 0);
        tableUtilisateurs = new JTable(modeleUtilisateurs);
        panel.add(new JScrollPane(tableUtilisateurs), BorderLayout.CENTER);
 
        // Boutons
        JPanel boutons = new JPanel();
 
        JButton btnRafraichir = new JButton("Rafraichir");
        btnRafraichir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chargerUtilisateurs();
            }
        });
        boutons.add(btnRafraichir);
 
        JButton btnActiver = new JButton("Activer");
        btnActiver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changerActivation(true);
            }
        });
        boutons.add(btnActiver);
 
        JButton btnDesactiver = new JButton("Desactiver");
        btnDesactiver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changerActivation(false);
            }
        });
        boutons.add(btnDesactiver);
 
        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                supprimer();
            }
        });
        boutons.add(btnSupprimer);
 
        panel.add(boutons, BorderLayout.SOUTH);
 
        return panel;
    }
 
    private void chargerUtilisateurs() {
        try {
            modeleUtilisateurs.setRowCount(0);  // Vide le tableau
 
            UtilisateurDAO dao = new UtilisateurDAO();
            List<Utilisateur> liste = dao.listerTous();
 
            for (Utilisateur u : liste) {
                Object[] ligne = new Object[7];
                ligne[0] = u.getId();
                ligne[1] = u.getLogin();
                ligne[2] = u.getEmail();
                ligne[3] = u.getNom();
                ligne[4] = u.getPrenom();
                ligne[5] = u.getRole();
                ligne[6] = u.isActif() ? "Oui" : "Non";
                modeleUtilisateurs.addRow(ligne);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement");
        }
    }
 
    private void changerActivation(boolean actif) {
        int ligne = tableUtilisateurs.getSelectedRow();
 
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez un utilisateur");
            return;
        }
 
        int id = (int) modeleUtilisateurs.getValueAt(ligne, 0);
 
        // Empêcher l'admin de se désactiver lui-même
        if (id == adminConnecte.getId() && !actif) {
            JOptionPane.showMessageDialog(this, "Vous ne pouvez pas vous desactiver vous-meme");
            return;
        }
 
        try {
            UtilisateurDAO dao = new UtilisateurDAO();
            dao.changerActivation(id, actif);
            chargerUtilisateurs();  // Recharger pour voir le changement
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur");
        }
    }
 
    private void supprimer() {
        int ligne = tableUtilisateurs.getSelectedRow();
 
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez un utilisateur");
            return;
        }
 
        int id = (int) modeleUtilisateurs.getValueAt(ligne, 0);
        String login = (String) modeleUtilisateurs.getValueAt(ligne, 1);
 
        if (id == adminConnecte.getId()) {
            JOptionPane.showMessageDialog(this, "Vous ne pouvez pas vous supprimer vous-meme");
            return;
        }
 
        // Demander confirmation
        int reponse = JOptionPane.showConfirmDialog(this,
            "Supprimer l'utilisateur " + login + " ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
 
        if (reponse == JOptionPane.YES_OPTION) {
            try {
                UtilisateurDAO dao = new UtilisateurDAO();
                dao.supprimer(id);
                chargerUtilisateurs();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur");
            }
        }
    }
 
    // ========================================================
    // ONGLET 2 : LOGS CONNEXION
    // ========================================================
    private JPanel creerOngletLogsConnexion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
 
        JLabel titre = new JLabel("Historique des connexions");
        titre.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titre, BorderLayout.NORTH);
 
        String[] colonnes = {"Date", "Login", "IP", "Succes"};
        modeleLogsConnexion = new DefaultTableModel(colonnes, 0);
        tableLogsConnexion = new JTable(modeleLogsConnexion);
        panel.add(new JScrollPane(tableLogsConnexion), BorderLayout.CENTER);
 
        JButton btnRafraichir = new JButton("Rafraichir");
        btnRafraichir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chargerLogsConnexion();
            }
        });
        JPanel boutons = new JPanel();
        boutons.add(btnRafraichir);
        panel.add(boutons, BorderLayout.SOUTH);
 
        return panel;
    }
 
    private void chargerLogsConnexion() {
        try {
            modeleLogsConnexion.setRowCount(0);
 
            LogDAO dao = new LogDAO();
            List<LogEntry> liste = dao.listerLogsConnexion();
 
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
 
            for (LogEntry log : liste) {
                Object[] ligne = new Object[4];
                ligne[0] = format.format(log.getDate());
                ligne[1] = log.getLogin();
                ligne[2] = log.getAdresseIp();
                ligne[3] = log.isSucces() ? "Reussi" : "Echec";
                modeleLogsConnexion.addRow(ligne);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    // ========================================================
    // ONGLET 3 : LOGS RECHERCHE
    // ========================================================
    private JPanel creerOngletLogsRecherche() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
 
        JLabel titre = new JLabel("Historique des recherches");
        titre.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titre, BorderLayout.NORTH);
 
        String[] colonnes = {"Date", "Utilisateur", "IP", "Type", "Criteres"};
        modeleLogsRecherche = new DefaultTableModel(colonnes, 0);
        tableLogsRecherche = new JTable(modeleLogsRecherche);
        panel.add(new JScrollPane(tableLogsRecherche), BorderLayout.CENTER);
 
        JButton btnRafraichir = new JButton("Rafraichir");
        btnRafraichir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chargerLogsRecherche();
            }
        });
        JPanel boutons = new JPanel();
        boutons.add(btnRafraichir);
        panel.add(boutons, BorderLayout.SOUTH);
 
        return panel;
    }
 
    private void chargerLogsRecherche() {
        try {
            modeleLogsRecherche.setRowCount(0);
 
            LogDAO dao = new LogDAO();
            List<LogEntry> liste = dao.listerLogsRecherche();
 
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
 
            for (LogEntry log : liste) {
                Object[] ligne = new Object[5];
                ligne[0] = format.format(log.getDate());
                ligne[1] = log.getLogin() != null ? log.getLogin() : "anonyme";
                ligne[2] = log.getAdresseIp();
                ligne[3] = log.getTypeRecherche();
                ligne[4] = log.getCriteres();
                modeleLogsRecherche.addRow(ligne);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}