<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon espace club</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=JetBrains+Mono&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style_club.css">
</head>
<body class="club-body">

<header class="club-topbar">
    <div class="club-topbar-inner">
        <a href="${pageContext.request.contextPath}/accueil" class="club-brand">
            <span class="club-brand-logo">⚽</span> Club Sportif
        </a>
        <div class="club-topbar-right">
            <span class="club-chip">CLUB</span>
            <span class="club-chip-name">${sessionScope.utilisateur.login}</span>
            <a href="${pageContext.request.contextPath}/deconnexion" class="club-link-out">Déconnexion</a>
        </div>
    </div>
</header>

<main class="club-shell">

    <div class="club-head">
        <span class="club-eyebrow">Espace privé</span>
        <h1 class="club-h1">Mon espace club</h1>
        <p class="club-sub">Gérez les informations visibles par le grand public et vos adhérents.</p>
    </div>

    <c:if test="${not empty succes}">
        <div class="club-alert ok"><span>✓</span> ${succes}</div>
    </c:if>
    <c:if test="${not empty erreur}">
        <div class="club-alert err"><span>⚠</span> ${erreur}</div>
    </c:if>

    <c:choose>

        <%-- ============ AUCUN ESPACE ============ --%>
        <c:when test="${empty espace}">
            <div class="club-empty">
                <div class="club-empty-badge">🏆</div>
                <h2>Bienvenue sur votre espace</h2>
                <p>Vous n'avez pas encore créé votre page club. Lancez-vous pour communiquer vos actualités, horaires et cotisations au grand public.</p>
                <a href="${pageContext.request.contextPath}/club/editer" class="btn btn-ghost">
                    Créer mon espace →
                </a>
            </div>
        </c:when>

        <%-- ============ ESPACE EXISTANT ============ --%>
        <c:otherwise>
            <div class="club-card">

                <%-- Bandeau --%>
                <c:choose>
                    <c:when test="${not empty espace.photo}">
                        <div class="dash-hero with-photo">
                            <img class="dash-hero-img"
                                 src="${pageContext.request.contextPath}/uploads/${espace.photo}"
                                 alt="${espace.nomClub}">
                            <div class="dash-hero-overlay">
                                <div class="dash-hero-title">${espace.nomClub}</div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="dash-hero">
                            <div class="dash-hero-title">${espace.nomClub}</div>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="dash-body">

                    <div class="dash-actions">
                        <a href="${pageContext.request.contextPath}/club/editer" class="btn btn-primary">
                            ✎ Modifier mon espace
                        </a>
                        <a href="${pageContext.request.contextPath}/club/public?slug=${espace.slug}"
                           target="_blank" class="btn btn-light">
                            👁 Voir la page publique
                        </a>
                    </div>

                    <div class="dash-urlbar">
                        🔗 <span>Adresse publique :</span>
                        <code>${pageContext.request.contextPath}/club/public?slug=${espace.slug}</code>
                    </div>

                    <div class="dash-grid">

                        <c:if test="${not empty espace.description}">
                            <div class="dash-tile wide">
                                <div class="dash-tile-label">📝 Description</div>
                                <div class="dash-tile-value">${espace.description}</div>
                            </div>
                        </c:if>

                        <c:if test="${not empty espace.actualites}">
                            <div class="dash-tile wide">
                                <div class="dash-tile-label">📰 Actualités</div>
                                <div class="dash-tile-value">${espace.actualites}</div>
                            </div>
                        </c:if>

                        <c:if test="${not empty espace.horaires}">
                            <div class="dash-tile">
                                <div class="dash-tile-label">🕐 Horaires</div>
                                <div class="dash-tile-value">${espace.horaires}</div>
                            </div>
                        </c:if>

                        <c:if test="${espace.montantCotisation > 0}">
                            <div class="dash-tile">
                                <div class="dash-tile-label">💰 Cotisation annuelle</div>
                                <div class="dash-tile-money">${espace.montantCotisation} €</div>
                            </div>
                        </c:if>

                        <div class="dash-tile wide">
                            <div class="dash-tile-label">📞 Contact</div>
                            <div class="dash-contacts">
                                <c:if test="${not empty espace.contactTel}">
                                    <span>📞 ${espace.contactTel}</span>
                                </c:if>
                                <c:if test="${not empty espace.contactEmail}">
                                    <span>✉ ${espace.contactEmail}</span>
                                </c:if>
                                <c:if test="${not empty espace.adresse}">
                                    <span>📍 ${espace.adresse}</span>
                                </c:if>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

</main>

</body>
</html>
