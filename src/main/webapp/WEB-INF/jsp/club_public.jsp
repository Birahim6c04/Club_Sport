<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>${espace.nomClub}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="club-public-page">

    <header class="club-public-header">
        <a href="${pageContext.request.contextPath}/accueil" class="club-logo">⚽ <strong>Club Sportif</strong></a>
        <a href="${pageContext.request.contextPath}/clubs" class="club-back">← Tous les clubs</a>
    </header>

    <c:if test="${not empty espace.photo}">
        <div class="club-public-banner">
            <img src="${pageContext.request.contextPath}/uploads/${espace.photo}" alt="${espace.nomClub}">
        </div>
    </c:if>

    <main class="club-public-main">

        <h1 class="club-public-title">${espace.nomClub}</h1>

        <c:if test="${not empty espace.description}">
            <p class="club-public-description">${espace.description}</p>
        </c:if>

        <c:if test="${not empty espace.actualites}">
            <section class="club-public-section">
                <h2>📰 Actualités</h2>
                <p style="white-space:pre-line;">${espace.actualites}</p>
            </section>
        </c:if>

        <c:if test="${not empty espace.horaires}">
            <section class="club-public-section">
                <h2>🕐 Horaires</h2>
                <p style="white-space:pre-line;">${espace.horaires}</p>
            </section>
        </c:if>

        <c:if test="${espace.montantCotisation > 0}">
            <section class="club-public-section">
                <h2>💰 Cotisation</h2>
                <p><strong>${espace.montantCotisation} €</strong> par an</p>
            </section>
        </c:if>

        <section class="club-public-section">
            <h2>📞 Nous contacter</h2>
            <div class="club-contacts-grid">
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
        </section>

    </main>

</div>

</body>
</html>
