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
        <div class="club-topbar-right">
            <a href="javascript:history.back()" class="btn btn-light" style="padding:8px 16px;">
                ← Retour
            </a>
            <a href="${pageContext.request.contextPath}/clubs" class="btn btn-light" style="padding:8px 16px;">
                Tous les clubs
            </a>
            <c:if test="${sessionScope.utilisateur.role == 'CLUB'}">
                <a href="${pageContext.request.contextPath}/club" class="btn btn-primary" style="padding:8px 16px;">
                    Mon espace
                </a>
            </c:if>
        </div>
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

        <%-- ===== LIKES + COMMENTAIRES ===== --%>
        <div class="pub-block full">
            <div class="pub-block-head">
                <div class="pub-block-ico">💬</div>
                <div class="pub-block-title">Réactions &amp; commentaires</div>
            </div>

            <%-- Bouton like --%>
            <c:if test="${sessionScope.utilisateur.role == 'PUBLIC'}">
            <form action="${pageContext.request.contextPath}/club/like" method="POST" style="margin-bottom:18px;">
                <input type="hidden" name="idEspace" value="${espace.idEspace}">
                <input type="hidden" name="slug" value="${espace.slug}">
                <button type="submit" class="btn ${dejaLike ? 'btn-primary' : 'btn-light'}">
                    ❤ ${dejaLike ? 'Aimé' : "J'aime"} · ${nbLikes}
                </button>
            </form>
            </c:if>
            
            <c:if test="${sessionScope.utilisateur.role != 'PUBLIC'}">
    <div style="margin-bottom:18px;font-size:14px;color:#64808a;">
        ❤ ${nbLikes} j'aime
    </div>
</c:if>

            <%-- Formulaire commentaire --%>
            <c:choose>
    <c:when test="${sessionScope.utilisateur.role == 'PUBLIC'}">
        <form action="${pageContext.request.contextPath}/club/commenter" method="POST" style="margin-bottom:22px;">
            <input type="hidden" name="idEspace" value="${espace.idEspace}">
            <input type="hidden" name="slug" value="${espace.slug}">
            <div class="field">
                <textarea name="contenu" rows="2"
                          placeholder="Écrire un commentaire..." required></textarea>
            </div>
            <button type="submit" class="btn btn-primary">Publier</button>
        </form>
    </c:when>
    <c:when test="${empty sessionScope.utilisateur}">
        <p style="color:#64808a;font-size:14px;margin-bottom:14px;">
            <a href="${pageContext.request.contextPath}/connexion" style="color:#0ea5b7;font-weight:600;">
                Connectez-vous
            </a>
            en tant que grand public pour aimer et commenter.
        </p>
    </c:when>
</c:choose>

            <%-- Liste des commentaires --%>
            <c:choose>
                <c:when test="${empty commentaires}">
                    <p style="color:#64808a;font-size:14px;">Aucun commentaire pour le moment.</p>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${commentaires}" var="com">
                        <div style="border-top:1px solid #e3edf0;padding:14px 0;">
                            <strong style="color:#053f48;font-size:14px;">${com.auteur}</strong>
                            <p style="color:#0f2730;font-size:14px;margin-top:4px;line-height:1.6;">${com.contenu}</p>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

    </div>
</div>

</body>
</html>
