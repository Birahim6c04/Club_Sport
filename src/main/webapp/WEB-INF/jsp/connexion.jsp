<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - Club Sportif</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="auth-container">
        <div class="auth-card">
            <a href="${pageContext.request.contextPath}/accueil" class="auth-logo">
                ⚽ <strong>Club Sportif</strong>
            </a>

            <h1 class="auth-title">Connexion</h1>
            <p class="auth-subtitle">Connectez-vous pour accéder à votre espace</p>

            <c:if test="${not empty erreur}">
                <div class="auth-erreur">⚠️ ${erreur}</div>
            </c:if>

            <c:if test="${not empty succes}">
                <div class="auth-succes">✓ ${succes}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/connexion" method="POST" class="auth-form">
                <div class="champ">
                    <label>Login</label>
                    <input type="text" name="login" value="${login}" placeholder="Votre identifiant" required autofocus>
                </div>

                <div class="champ">
                    <label>Mot de passe</label>
                    <input type="password" name="motDePasse" placeholder="••••••••" required>
                </div>

                <button type="submit" class="btn-rechercher">Se connecter</button>
            </form>

            <div class="auth-footer">
                Pas encore de compte ?
                <a href="${pageContext.request.contextPath}/inscription">S'inscrire</a>
            </div>
        </div>
    </div>

</body>
</html>
