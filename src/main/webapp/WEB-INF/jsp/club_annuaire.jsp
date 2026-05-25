<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Annuaire des clubs</title>
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
        <a href="${pageContext.request.contextPath}/accueil" class="btn btn-light" style="padding:8px 16px;">
            Accueil
        </a>
    </div>
</header>

<main class="club-shell">

    <div class="club-head">
        <span class="club-eyebrow">Découvrir</span>
        <h1 class="club-h1">Annuaire des clubs</h1>
        <p class="club-sub">Explorez tous les clubs inscrits sur la plateforme.</p>
    </div>

    <c:choose>
        <c:when test="${empty clubs}">
            <div class="club-empty">
                <div class="club-empty-badge">📭</div>
                <h2>Aucun club pour le moment</h2>
                <p>Les clubs inscrits apparaîtront ici dès qu'ils auront créé leur espace.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="dir-grid">
                <c:forEach items="${clubs}" var="club">
                    <a href="${pageContext.request.contextPath}/club/public?slug=${club.slug}" class="dir-card">
                        <div class="dir-photo">
                            <c:choose>
                                <c:when test="${not empty club.photo}">
                                    <img src="${pageContext.request.contextPath}/uploads/${club.photo}"
                                         alt="${club.nomClub}">
                                </c:when>
                                <c:otherwise>
                                    <div class="dir-photo-empty">⚽</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="dir-body">
                            <div class="dir-name">${club.nomClub}</div>
                            <c:if test="${not empty club.description}">
                                <div class="dir-desc">${club.description}</div>
                            </c:if>
                            <div class="dir-foot">
                                <span>
                                    <c:if test="${not empty club.adresse}">📍 ${club.adresse}</c:if>
                                </span>
                                <span class="dir-foot-link">Voir →</span>
                            </div>
                        </div>
                    </a>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

</main>

</body>
</html>
