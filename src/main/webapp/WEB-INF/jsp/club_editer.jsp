<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Modifier mon espace</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="club-page">

    <header class="club-header">
        <div class="club-nav">
            <a href="${pageContext.request.contextPath}/accueil" class="club-logo">⚽ <strong>Club Sportif</strong></a>
            <div class="club-user">
                <span class="club-role-badge">CLUB</span>
                <a href="${pageContext.request.contextPath}/club" class="club-back">← Retour</a>
                <a href="${pageContext.request.contextPath}/deconnexion" class="club-logout">Déconnexion</a>
            </div>
        </div>
    </header>

    <main class="club-main">

        <div class="club-title-block">
            <h1>
                <c:choose>
                    <c:when test="${empty espace}">Créer mon espace</c:when>
                    <c:otherwise>Modifier mon espace</c:otherwise>
                </c:choose>
            </h1>
            <p>Toutes les informations seront visibles par le grand public</p>
        </div>

        <c:if test="${not empty erreur}">
            <div class="club-erreur">⚠️ ${erreur}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/club/editer" method="POST" enctype="multipart/form-data" class="club-form">

            <div class="club-form-section">
                <h3>📋 Informations générales</h3>

                <div class="champ">
                    <label>Nom du club *</label>
                    <input type="text" name="nomClub" value="${espace.nomClub}" required placeholder="AS Foot Beauvais">
                </div>

                <div class="champ">
                    <label>Description</label>
                    <textarea name="description" rows="4" placeholder="Présentez votre club en quelques lignes...">${espace.description}</textarea>
                </div>

                <div class="champ">
                    <label>Photo (JPG ou PNG)</label>
                    <input type="file" name="photo" accept=".jpg,.jpeg,.png">
                    <c:if test="${not empty espace.photo}">
                        <p style="margin-top:8px; font-size:13px; color:#6b7d8d;">Photo actuelle : ${espace.photo}</p>
                    </c:if>
                </div>
            </div>

            <div class="club-form-section">
                <h3>📰 Actualités</h3>
                <div class="champ">
                    <label>Dernières actualités</label>
                    <textarea name="actualites" rows="6" placeholder="Annoncez vos prochains événements, résultats, etc.">${espace.actualites}</textarea>
                </div>
            </div>

            <div class="club-form-section">
                <h3>🕐 Horaires</h3>
                <div class="champ">
                    <label>Horaires d'entraînement / d'ouverture</label>
                    <textarea name="horaires" rows="4" placeholder="Lundi : 18h-20h&#10;Mercredi : 14h-16h&#10;Samedi : 10h-12h">${espace.horaires}</textarea>
                </div>
            </div>

            <div class="club-form-section">
                <h3>💰 Cotisations</h3>
                <div class="champ">
                    <label>Montant annuel (€)</label>
                    <input type="number" name="montantCotisation" value="${espace.montantCotisation}" step="0.01" min="0" placeholder="150.00">
                </div>
            </div>

            <div class="club-form-section">
                <h3>📞 Contact</h3>

                <div class="champ">
                    <label>Téléphone</label>
                    <input type="tel" name="contactTel" value="${espace.contactTel}" placeholder="03 44 12 34 56">
                </div>

                <div class="champ">
                    <label>Email</label>
                    <input type="email" name="contactEmail" value="${espace.contactEmail}" placeholder="contact@monclub.fr">
                </div>

                <div class="champ">
                    <label>Adresse</label>
                    <input type="text" name="adresse" value="${espace.adresse}" placeholder="12 rue du Sport, 60000 Beauvais">
                </div>
            </div>

            <button type="submit" class="btn-rechercher">
                💾 Enregistrer
            </button>
        </form>

    </main>

</div>

</body>
</html>
