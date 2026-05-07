package com.esigelec.clubsport.client;
 
import com.esigelec.clubsport.client.dao.UtilisateurDAO;

import com.esigelec.clubsport.client.model.Utilisateur;
 
import org.mindrot.jbcrypt.BCrypt;
 
import javax.swing.*;

import java.awt.*;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
 
public class ConnexionFrame extends JFrame {
 
    private JTextField loginField;

    private JPasswordField motDePasseField;

    private JLabel messageLabel;
 
    public ConnexionFrame() {

        setTitle("Club Sportif - Administration");

        setSize(400, 300);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        // Création du panel

        JPanel panel = new JPanel();

        panel.setLayout(null);
 
        // Titre

        JLabel titre = new JLabel("Connexion Administrateur");

        titre.setFont(new Font("Arial", Font.BOLD, 16));

        titre.setBounds(80, 20, 250, 30);

        panel.add(titre);
 
        // Label et champ login

        JLabel labelLogin = new JLabel("Login :");

        labelLogin.setBounds(40, 70, 100, 25);

        panel.add(labelLogin);
 
        loginField = new JTextField();

        loginField.setBounds(140, 70, 200, 25);

        panel.add(loginField);
 
        // Label et champ mot de passe

        JLabel labelMdp = new JLabel("Mot de passe :");

        labelMdp.setBounds(40, 110, 100, 25);

        panel.add(labelMdp);
 
        motDePasseField = new JPasswordField();

        motDePasseField.setBounds(140, 110, 200, 25);

        panel.add(motDePasseField);
 
        // Message d'erreur

        messageLabel = new JLabel("");

        messageLabel.setForeground(Color.RED);

        messageLabel.setBounds(40, 145, 320, 25);

        panel.add(messageLabel);
 
        // Bouton se connecter

        JButton btnConnexion = new JButton("Se connecter");

        btnConnexion.setBounds(140, 180, 200, 30);

        btnConnexion.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                seConnecter();

            }

        });

        panel.add(btnConnexion);
 
        add(panel);

        setVisible(true);

    }
 
    // Méthode appelée quand on clique sur "Se connecter"

    private void seConnecter() {

        String login = loginField.getText();

        String motDePasse = new String(motDePasseField.getPassword());
 
        // Vérification que les champs sont remplis

        if (login.isEmpty() || motDePasse.isEmpty()) {

            messageLabel.setText("Veuillez remplir tous les champs");

            return;

        }
 
        try {

            UtilisateurDAO dao = new UtilisateurDAO();

            Utilisateur u = dao.trouverParLogin(login);
 
            // Si l'utilisateur n'existe pas

            if (u == null) {

                messageLabel.setText("Login ou mot de passe incorrect");

                return;

            }
 
            // Vérification du mot de passe avec BCrypt

            // BCrypt compare le mot de passe en clair avec le hash en BDD

            boolean motDePasseOk = BCrypt.checkpw(motDePasse, u.getMotDePasse());
 
            if (!motDePasseOk) {

                messageLabel.setText("Login ou mot de passe incorrect");

                return;

            }
 
            // Vérification que c'est un ADMIN

            if (!u.getRole().equals("ADMIN")) {

                messageLabel.setText("Acces reserve aux administrateurs");

                return;

            }
 
            // Vérification que le compte est actif

            if (!u.isActif()) {

                messageLabel.setText("Compte desactive");

                return;

            }
 
            // Tout est OK : on ouvre la fenêtre admin

            new AdminFrame(u);

            dispose();  // Ferme la fenêtre de connexion
 
        } catch (Exception ex) {

            ex.printStackTrace();

            messageLabel.setText("Erreur de connexion a la base");

        }

    }

}
 