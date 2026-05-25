<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${espace.nomClub}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style_club.css">
</head>
<body class="club-body">

<header class="club-topbar">
    <div class="club-topbar-inner">
        <a href="${pageContext.request.contextPath}/accueil" class="club-brand">
            <span class="club-brand-logo">⚽</span> Club Sportif
        </a>
        <a href="${pageContext.request.contextPath}/clubs" class="btn btn-light" style="padding:8px 16px;">
            ← Tous les clubs
        </a>
    </div>
</header>

<%-- ===== HERO ===== --%>
<section class="pub-hero">
    <c:choose>
        <c:when test="${not empty espace.photo}">
            <img src="${pageContext.request.contextPath}/uploads/${espace.photo}" alt="${espace.nomClub}">
        </c:when>
        <c:otherwise>
            <div class="pub-hero-fallback"></div>
        </c:otherwise>
    </c:choose>
    <div class="pub-hero-veil"></div>
    <div class="pub-hero-content">
        <span class="pub-hero-tag">Club sportif</span>
        <h1 class="pub-hero-title">${espace.nomClub}</h1>
    </div>
</section>

<div class="pub-wrap">

    <c:if test="${not empty espace.description}">
        <div class="pub-intro">${espace.description}</div>
    </c:if>

    <div class="pub-grid">

        <c:if test="${not empty espace.actualites}">
            <div class="pub-block full">
                <div class="pub-block-head">
                    <div class="pub-block-ico">📰</div>
                    <div class="pub-block-title">Actualités</div>
                </div>
                <p>${espace.actualites}</p>
            </div>
        </c:if>

        <c:if test="${not empty espace.horaires}">
            <div class="pub-block">
                <div class="pub-block-head">
                    <div class="pub-block-ico">🕐</div>
                    <div class="pub-block-title">Horaires</div>
                </div>
                <p>${espace.horaires}</p>
            </div>
        </c:if>

        <c:if test="${espace.montantCotisation > 0}">
            <div class="pub-block">
                <div class="pub-block-head">
                    <div class="pub-block-ico">💰</div>
                    <div class="pub-block-title">Cotisation</div>
                </div>
                <div class="pub-money">${espace.montantCotisation} € <small>/ an</small></div>
            </div>
        </c:if>

        <div class="pub-block full">
            <div class="pub-block-head">
                <div class="pub-block-ico">📞</div>
                <div class="pub-block-title">Nous contacter</div>
            </div>
            <div class="pub-contacts">
                <c:if test="${not empty espace.contactTel}">
                    <div class="pub-contact">
                        <div class="pub-contact-ico">📞</div>
                        <div class="pub-contact-txt">${espace.contactTel}</div>
                    </div>
                </c:if>
                <c:if test="${not empty espace.contactEmail}">
                    <div class="pub-contact">
                        <div class="pub-contact-ico">✉</div>
                        <div class="pub-contact-txt">${espace.contactEmail}</div>
                    </div>
                </c:if>
                <c:if test="${not empty espace.adresse}">
                    <div class="pub-contact">
                        <div class="pub-contact-ico">📍</div>
                        <div class="pub-contact-txt">${espace.adresse}</div>
                    </div>
                </c:if>
            </div>
        </div>

    </div>
</div>

</body>
</html>
