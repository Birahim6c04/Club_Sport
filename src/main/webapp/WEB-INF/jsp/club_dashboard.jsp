<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mon espace club</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="club-page">

    <header class="club-header">
        <div class="club-nav">
            <a href="${pageContext.request.contextPath}/accueil" class="club-logo">⚽ <strong>Club Sportif</strong></a>
            <div class="club-user">
                <span class="club-role-badge">CLUB</span>
                <span>${sessionScope.utilisateur.login}</span>
                <a href="${pageContext.request.contextPath}/deconnexion" class="club-logout">Déconnexion</a>
            </div>
        </div>
    </header>

    <main class="club-main">

        <c:if test="${not empty succes}">
            <div class="club-succes">✓ ${succes}</div>
        </c:if>
        <c:if test="${not empty erreur}">
            <div class="club-erreur">⚠️ ${erreur}</div>
        </c:if>

        <div class="club-title-block">
            <h1>Mon espace club</h1>
            <p>Gérez les informations visibles par le grand public</p>
        </div>

        <c:choose>
            <c:when test="${empty espace}">
                <div class="club-empty">
                    <h2>🏆 Bienvenue !</h2>
                    <p>Vous n'avez pas encore créé votre espace. Commencez maintenant pour communiquer avec votre communauté.</p>
                    <a href="${pageContext.request.contextPath}/club/editer" class="btn-rechercher">Créer mon espace</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="club-preview">

                    <div class="club-preview-header">
                        <h2>${espace.nomClub}</h2>
                        <div class="club-preview-actions">
                            <a href="${pageContext.request.contextPath}/club/editer" class="btn-secondary">Modifier</a>
                            <a href="${pageContext.request.contextPath}/club/public?slug=${espace.slug}" target="_blank" class="btn-rechercher">Voir page publique</a>
                        </div>
                    </div>

                    <p class="club-public-url">
                        🔗 URL publique :
                        <code>${pageContext.request.contextPath}/club/public?slug=${espace.slug}</code>
                    </p>

                    <c:if test="${not empty espace.photo}">
                        <img src="${pageContext.request.contextPath}/uploads/${espace.photo}" class="club-photo-preview" alt="Photo du club">
                    </c:if>

                    <c:if test="${not empty espace.description}">
                        <div class="club-section">
                            <h3>Description</h3>
                            <p>${espace.description}</p>
                        </div>
                    </c:if>

                    <c:if test="${not empty espace.actualites}">
                        <div class="club-section">
                            <h3>📰 Actualités</h3>
                            <p>${espace.actualites}</p>
                        </div>
                    </c:if>

                    <c:if test="${not empty espace.horaires}">
                        <div class="club-section">
                            <h3>🕐 Horaires</h3>
                            <p>${espace.horaires}</p>
                        </div>
                    </c:if>

                    <c:if test="${espace.montantCotisation > 0}">
                        <div class="club-section">
                            <h3>💰 Cotisation annuelle</h3>
                            <p>${espace.montantCotisation} €</p>
                        </div>
                    </c:if>

                    <div class="club-contacts">
                        <c:if test="${not empty espace.contactTel}">
                            <div>📞 ${espace.contactTel}</div>
                        </c:if>
                        <c:if test="${not empty espace.contactEmail}">
                            <div>✉ ${espace.contactEmail}</div>
                        </c:if>
                        <c:if test="${not empty espace.adresse}">
                            <div>📍 ${espace.adresse}</div>
                        </c:if>
                    </div>

                </div>
            </c:otherwise>
        </c:choose>

    </main>

</div>

</body>
</html>
