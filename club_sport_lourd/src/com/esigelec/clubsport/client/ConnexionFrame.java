package com.esigelec.clubsport.client;

import com.esigelec.clubsport.client.dao.DbConnection;
import com.esigelec.clubsport.client.dao.UtilisateurDAO;
import com.esigelec.clubsport.client.model.Utilisateur;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ConnexionFrame extends JFrame {

    private JTextField loginField;
    private JPasswordField motDePasseField;
    private JLabel messageLabel;

    public ConnexionFrame() {
        setTitle("Club Sportif - Administration");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titre = new JLabel("Connexion Administrateur");
        titre.setFont(new Font("Arial", Font.BOLD, 16));
        titre.setBounds(80, 20, 250, 30);
        panel.add(titre);

        JLabel labelLogin = new JLabel("Login :");
        labelLogin.setBounds(40, 70, 100, 25);
        panel.add(labelLogin);

        loginField = new JTextField();
        loginField.setBounds(140, 70, 200, 25);
        panel.add(loginField);

        JLabel labelMdp = new JLabel("Mot de passe :");
        labelMdp.setBounds(40, 110, 100, 25);
        panel.add(labelMdp);

        motDePasseField = new JPasswordField();
        motDePasseField.setBounds(140, 110, 200, 25);
        panel.add(motDePasseField);

        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setBounds(40, 145, 320, 25);
        panel.add(messageLabel);

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

    private void seConnecter() {
        String login = loginField.getText();
        String motDePasse = new String(motDePasseField.getPassword());

        if (login.isEmpty() || motDePasse.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            UtilisateurDAO dao = new UtilisateurDAO();
            Utilisateur u = dao.trouverParLogin(login);

            if (u == null) {
                logConnexion(login, false);
                messageLabel.setText("Login ou mot de passe incorrect");
                return;
            }

            boolean motDePasseOk = BCrypt.checkpw(motDePasse, u.getMotDePasse());
            if (!motDePasseOk) {
                logConnexion(login, false);
                messageLabel.setText("Login ou mot de passe incorrect");
                return;
            }

            if (!u.getRole().equals("ADMIN")) {
                logConnexion(login, false);
                messageLabel.setText("Acces reserve aux administrateurs");
                return;
            }

            if (!u.isActif()) {
                logConnexion(login, false);
                messageLabel.setText("Compte desactive");
                return;
            }

            // Connexion réussie : on log et on ouvre la fenêtre admin
            logConnexion(login, true);
            new AdminFrame(u);
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            messageLabel.setText("Erreur de connexion a la base");
        }
    }

    // Enregistre une tentative de connexion dans la table log_connexion
    private void logConnexion(String login, boolean succes) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();

            Connection conn = DbConnection.getConnection();

            String sql = "INSERT INTO log_connexion (login_tente, adresse_ip, succes, user_agent) " +
                         "VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, login);
            ps.setString(2, ip);
            ps.setBoolean(3, succes);
            ps.setString(4, "Client lourd Java");

            ps.executeUpdate();

            ps.close();
            conn.close();

            System.out.println("Log enregistre : " + login + " / " + ip + " / succes=" + succes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}