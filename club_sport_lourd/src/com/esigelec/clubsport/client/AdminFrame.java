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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminFrame extends JFrame {

    private Utilisateur adminConnecte;

    // Chemin du dossier uploads (sur le PC)
    // À adapter selon ton chemin local
    private static final String DOSSIER_UPLOADS = "C:\\Users\\63382952.INTRANET\\Downloads\\Club_Sport\\uploads";

    private DefaultTableModel modeleUtilisateurs;
    private JTable tableUtilisateurs;

    private DefaultTableModel modeleAttente;
    private JTable tableAttente;

    private DefaultTableModel modeleLogsConnexion;
    private JTable tableLogsConnexion;

    private DefaultTableModel modeleLogsRecherche;
    private JTable tableLogsRecherche;

    public AdminFrame(Utilisateur admin) {
        this.adminConnecte = admin;

        setTitle("Administration - Connecte : " + admin.getLogin());
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Gestion utilisateurs", creerOngletAttente());
        onglets.addTab("Utilisateurs", creerOngletUtilisateurs());
        onglets.addTab("Logs de connexion", creerOngletLogsConnexion());
        onglets.addTab("Logs de recherche", creerOngletLogsRecherche());

        add(onglets);
        setVisible(true);

        chargerAttente();
        chargerUtilisateurs();
        chargerLogsConnexion();
        chargerLogsRecherche();
    }

    // ========================================================
    // ONGLET COMPTES EN ATTENTE
    // ========================================================
    private JPanel creerOngletAttente() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titre = new JLabel("Inscriptions en attente de validation");
        titre.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titre, BorderLayout.NORTH);

        String[] colonnes = {"ID", "Login", "Email", "Nom", "Prenom", "Role", "Statut", "Piece jointe"};
        modeleAttente = new DefaultTableModel(colonnes, 0);
        tableAttente = new JTable(modeleAttente);
        panel.add(new JScrollPane(tableAttente), BorderLayout.CENTER);

        JPanel boutons = new JPanel();

        JButton btnRafraichir = new JButton("Rafraichir");
        btnRafraichir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chargerAttente();
            }
        });
        boutons.add(btnRafraichir);

        JButton btnVoirPiece = new JButton("Voir la piece jointe");
        btnVoirPiece.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ouvrirPieceJointe();
            }
        });
        boutons.add(btnVoirPiece);

        JButton btnValider = new JButton("Valider");
        btnValider.setBackground(new Color(34, 197, 94));
        btnValider.setForeground(Color.WHITE);
        btnValider.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                valider();
            }
        });
        boutons.add(btnValider);

        JButton btnRefuser = new JButton("Refuser");
        btnRefuser.setBackground(new Color(239, 68, 68));
        btnRefuser.setForeground(Color.WHITE);
        btnRefuser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refuser();
            }
        });
        boutons.add(btnRefuser);

        panel.add(boutons, BorderLayout.SOUTH);

        return panel;
    }

    private void chargerAttente() {
        try {
            modeleAttente.setRowCount(0);
            UtilisateurDAO dao = new UtilisateurDAO();
            List<Utilisateur> liste = dao.listerEnAttente();

            for (Utilisateur u : liste) {
                Object[] ligne = new Object[8];
                ligne[0] = u.getId();
                ligne[1] = u.getLogin();
                ligne[2] = u.getEmail();
                ligne[3] = u.getNom();
                ligne[4] = u.getPrenom();
                ligne[5] = u.getRole();
                ligne[6] = u.getStatut();
                ligne[7] = u.getPieceJointe() != null ? u.getPieceJointe() : "(aucune)";
                modeleAttente.addRow(ligne);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur");
        }
    }

    private void ouvrirPieceJointe() {
        int ligne = tableAttente.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez un utilisateur");
            return;
        }

        String nomFichier = (String) modeleAttente.getValueAt(ligne, 7);
        if (nomFichier == null || nomFichier.equals("(aucune)")) {
            JOptionPane.showMessageDialog(this, "Aucune piece jointe pour cet utilisateur");
            return;
        }

        try {
            File fichier = new File(DOSSIER_UPLOADS + File.separator + nomFichier);

            if (!fichier.exists()) {
                JOptionPane.showMessageDialog(this,
                    "Fichier introuvable :\n" + fichier.getAbsolutePath(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ouvre le fichier avec l'application par défaut de Windows
            Desktop.getDesktop().open(fichier);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur ouverture fichier : " + e.getMessage());
        }
    }

    private void valider() {
        int ligne = tableAttente.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez un utilisateur");
            return;
        }

        int id = (int) modeleAttente.getValueAt(ligne, 0);
        String login = (String) modeleAttente.getValueAt(ligne, 1);

        int reponse = JOptionPane.showConfirmDialog(this,
            "Valider l'inscription de " + login + " ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            try {
                UtilisateurDAO dao = new UtilisateurDAO();
                dao.valider(id);
                JOptionPane.showMessageDialog(this, "Inscription validee. L'utilisateur peut se connecter.");
                chargerAttente();
                chargerUtilisateurs();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur");
            }
        }
    }

    private void refuser() {
        int ligne = tableAttente.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez un utilisateur");
            return;
        }

        int id = (int) modeleAttente.getValueAt(ligne, 0);
        String login = (String) modeleAttente.getValueAt(ligne, 1);

        int reponse = JOptionPane.showConfirmDialog(this,
            "Refuser l'inscription de " + login + " ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            try {
                UtilisateurDAO dao = new UtilisateurDAO();
                dao.refuser(id);
                chargerAttente();
                chargerUtilisateurs();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur");
            }
        }
    }

    // ========================================================
    // ONGLET UTILISATEURS (existant)
    // ========================================================
    private JPanel creerOngletUtilisateurs() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titre = new JLabel("Liste des utilisateurs");
        titre.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titre, BorderLayout.NORTH);

        String[] colonnes = {"ID", "Login", "Email", "Nom", "Prenom", "Role", "Statut", "Actif"};
        modeleUtilisateurs = new DefaultTableModel(colonnes, 0);
        tableUtilisateurs = new JTable(modeleUtilisateurs);
        panel.add(new JScrollPane(tableUtilisateurs), BorderLayout.CENTER);

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
            modeleUtilisateurs.setRowCount(0);
            UtilisateurDAO dao = new UtilisateurDAO();
            List<Utilisateur> liste = dao.listerTous();

            for (Utilisateur u : liste) {
                Object[] ligne = new Object[8];
                ligne[0] = u.getId();
                ligne[1] = u.getLogin();
                ligne[2] = u.getEmail();
                ligne[3] = u.getNom();
                ligne[4] = u.getPrenom();
                ligne[5] = u.getRole();
                ligne[6] = u.getStatut();
                ligne[7] = u.isActif() ? "Oui" : "Non";
                modeleUtilisateurs.addRow(ligne);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changerActivation(boolean actif) {
        int ligne = tableUtilisateurs.getSelectedRow();
        if (ligne == -1) {
            JOptionPane.showMessageDialog(this, "Selectionnez un utilisateur");
            return;
        }

        int id = (int) modeleUtilisateurs.getValueAt(ligne, 0);

        if (id == adminConnecte.getId() && !actif) {
            JOptionPane.showMessageDialog(this, "Vous ne pouvez pas vous desactiver vous-meme");
            return;
        }

        try {
            UtilisateurDAO dao = new UtilisateurDAO();
            dao.changerActivation(id, actif);
            chargerUtilisateurs();
        } catch (Exception e) {
            e.printStackTrace();
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

        int reponse = JOptionPane.showConfirmDialog(this,
            "Supprimer " + login + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (reponse == JOptionPane.YES_OPTION) {
            try {
                UtilisateurDAO dao = new UtilisateurDAO();
                dao.supprimer(id);
                chargerUtilisateurs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ========================================================
    // ONGLETS LOGS (existants)
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