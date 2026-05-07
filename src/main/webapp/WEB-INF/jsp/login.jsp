<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion - Clubs Sportifs</title>
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #eaf8f9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); width: 350px; }
        h2 { color: #00a8b5; text-align: center; margin-bottom: 20px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; color: #333; font-weight: 500; }
        input, select { width: 100%; padding: 10px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
        button { width: 100%; padding: 10px; background-color: #00a8b5; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; font-weight: bold; margin-top: 10px;}
        button:hover { background-color: #008f9b; }
        .error { color: #d9534f; background-color: #fdf7f7; padding: 10px; border-radius: 4px; text-align: center; margin-bottom: 15px; font-size: 0.9em; border: 1px solid #d9534f;}
        .success { color: #3c763d; background-color: #dff0d8; padding: 10px; border-radius: 4px; text-align: center; margin-bottom: 15px; font-size: 0.9em; border: 1px solid #3c763d;}
    </style>
</head>
<body>
    <div class="container">
        <h2>Se connecter</h2>

        <%-- Affichage des messages d'erreur venant du Servlet --%>
        <% String erreur = (String) request.getAttribute("erreur");
           if (erreur != null) { %>
            <div class="error"><%= erreur %></div>
        <% } %>

        <%-- Affichage du message de succès venant de RegisterServlet --%>
        <% if ("success".equals(request.getParameter("inscription"))) { %>
            <div class="success">Inscription réussie ! Vous pouvez maintenant vous connecter.</div>
        <% } %>

        <form action="LoginServlet" method="post">
            <div class="form-group">
                <label for="login">Identifiant (Login)</label>
                <input type="text" id="login" name="login" required>
            </div>
            <div class="form-group">
                <label for="password">Mot de passe</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit">Connexion</button>
        </form>
        
        <p style="text-align: center; margin-top: 20px; font-size: 0.9em;">
            Pas encore de compte ? <a href="register.jsp" style="color: #00a8b5; text-decoration: none; font-weight: bold;">S'inscrire</a>
        </p>
    </div>
</body>
</html>