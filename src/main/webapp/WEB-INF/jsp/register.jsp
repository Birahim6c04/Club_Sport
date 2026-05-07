<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Inscription - Clubs Sportifs</title>
    <!-- On réutilise le même style que pour la page login -->
    <style>
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #eaf8f9; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); width: 400px; max-height: 90vh; overflow-y: auto;}
        h2 { color: #00a8b5; text-align: center; margin-bottom: 20px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; color: #333; font-weight: 500; }
        input, select { width: 100%; padding: 10px; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
        button { width: 100%; padding: 10px; background-color: #00a8b5; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; font-weight: bold; margin-top: 10px;}
        button:hover { background-color: #008f9b; }
        .error { color: #d9534f; background-color: #fdf7f7; padding: 10px; border-radius: 4px; text-align: center; margin-bottom: 15px; font-size: 0.9em; border: 1px solid #d9534f;}
    </style>
</head>
<body>
    <div class="container">
        <h2>Créer un compte</h2>

        <% String erreur = (String) request.getAttribute("erreur");
           if (erreur != null) { %>
            <div class="error"><%= erreur %></div>
        <% } %>

        <form action="RegisterServlet" method="post">
            <div class="form-group">
                <label for="nom">Nom</label>
                <input type="text" id="nom" name="nom" required>
            </div>
            <div class="form-group">
                <label for="prenom">Prénom</label>
                <input type="text" id="prenom" name="prenom" required>
            </div>
            <div class="form-group">
                <label for="email">Adresse Email</label>
                <input type="email" id="email" name="email" required>
            </div>
            <div class="form-group">
                <label for="login">Identifiant (Login)</label>
                <input type="text" id="login" name="login" required>
            </div>
            <div class="form-group">
                <label for="password">Mot de passe</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div class="form-group">
                <label for="role">Je suis un...</label>
                <select id="role" name="role" required>
                    <!-- On ne propose pas ADMIN ici pour des raisons de sécurité évidentes -->
                    <option value="LICENCIE">Sportif licencié</option>
                    <option value="ENTRAINEUR">Entraîneur</option>
                    <option value="PRESIDENT">Président de club</option>
                    <option value="ELU">Élu (Maire, Député...)</option>
                </select>
            </div>
            <button type="submit">S'inscrire</button>
        </form>
        
        <p style="text-align: center; margin-top: 20px; font-size: 0.9em;">
            Déjà un compte ? <a href="login.jsp" style="color: #00a8b5; text-decoration: none; font-weight: bold;">Se connecter</a>
        </p>
    </div>
</body>
</html>