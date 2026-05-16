<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Annuaire des clubs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="club-public-page">

    <header class="club-public-header">
        <a href="${pageContext.request.contextPath}/accueil" class="club-logo">⚽ <strong>Club Sportif</strong></a>
    </header>

    <main class="club-public-main">

        <div class="club-title-block">
            <h1>📋 Annuaire des clubs</h1>
            <p>Découvrez tous les clubs inscrits sur la plateforme</p>
        </div>

        <c:choose>
            <c:when test="${empty clubs}">
                <div class="club-empty">
                    <p>Aucun club inscrit pour le moment.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="annuaire-grid">
                    <c:forEach items="${clubs}" var="club">
                        <a href="${pageContext.request.contextPath}/club/public?slug=${club.slug}" class="annuaire-card">
                            <c:if test="${not empty club.photo}">
                                <img src="${pageContext.request.contextPath}/uploads/${club.photo}" alt="${club.nomClub}" class="annuaire-photo">
                            </c:if>
                            <div class="annuaire-info">
                                <h3>${club.nomClub}</h3>
                                <c:if test="${not empty club.description}">
                                    <p>${club.description}</p>
                                </c:if>
                                <c:if test="${not empty club.adresse}">
                                    <small>📍 ${club.adresse}</small>
                                </c:if>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </main>

</div>

</body>
</html>
