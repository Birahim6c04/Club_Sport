<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Club Sportif</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="auth-container">
        <div class="auth-card">
            <a href="${pageContext.request.contextPath}/accueil" class="auth-logo">
                ⚽ <strong>Club Sportif</strong>
            </a>

            <h1 class="auth-title">Créer un compte</h1>
            <p class="auth-subtitle">Rejoignez la plateforme</p>

            <c:if test="${not empty erreur}">
                <div class="auth-erreur">⚠️ ${erreur}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/inscription" method="POST" class="auth-form">

                <div class="champs-row">
                    <div class="champ">
                        <label>Prénom</label>
                        <input type="text" name="prenom" value="${prenom}" placeholder="Jean" required>
                    </div>
                    <div class="champ">
                        <label>Nom</label>
                        <input type="text" name="nom" value="${nom}" placeholder="Dupont" required>
                    </div>
                </div>

                <div class="champ">
                    <label>Login (3 caractères min)</label>
                    <input type="text" name="login" value="${login}" placeholder="jdupont" required minlength="3">
                </div>

                <div class="champ">
                    <label>Email</label>
                    <input type="email" name="email" value="${email}" placeholder="jean@example.com" required>
                </div>

                <div class="champ">
                    <label>Mot de passe (6 caractères min)</label>
                    <input type="password" name="motDePasse" placeholder="••••••••" required minlength="6">
                </div>

                <div class="champ">
                    <label>Type de compte</label>
                    <select name="role" required>
                        <option value="">-- Choisir un rôle --</option>
                        <option value="CLUB" ${role == 'CLUB' ? 'selected' : ''}>Club sportif</option>
                        <option value="ELU"  ${role == 'ELU'  ? 'selected' : ''}>Élu local</option>
                        <option value="ADMIN" ${role == 'ADMIN' ? 'selected' : ''}>Administrateur</option>
                    </select>
                </div>

                <button type="submit" class="btn-rechercher">Créer mon compte</button>
            </form>

            <div class="auth-footer">
                Déjà inscrit ?
                <a href="${pageContext.request.contextPath}/connexion">Se connecter</a>
            </div>
        </div>
    </div>

</body>
</html>
