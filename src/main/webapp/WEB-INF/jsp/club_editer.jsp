<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modifier mon espace</title>
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
            <a href="${pageContext.request.contextPath}/club" class="btn btn-light" style="padding:8px 16px;">← Retour</a>
            <a href="${pageContext.request.contextPath}/deconnexion" class="club-link-out">Déconnexion</a>
        </div>
    </div>
</header>

<main class="club-shell">

    <div class="club-head">
        <span class="club-eyebrow">${empty espace ? 'Création' : 'Modification'}</span>
        <h1 class="club-h1">${empty espace ? 'Créer mon espace' : 'Modifier mon espace'}</h1>
        <p class="club-sub">Toutes les informations renseignées seront visibles par le grand public.</p>
    </div>

    <c:if test="${not empty erreur}">
        <div class="club-alert err"><span>⚠</span> ${erreur}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/club/editer" method="POST" enctype="multipart/form-data">
    <div class="edit-layout">

        <%-- ===== SIDEBAR NAVIGATION ===== --%>
        <nav class="edit-nav">
            <a class="edit-nav-item active" onclick="goSection(this,'sec-infos')">
                <span class="edit-nav-ico">📋</span> Informations
            </a>
            <a class="edit-nav-item" onclick="goSection(this,'sec-actus')">
                <span class="edit-nav-ico">📰</span> Actualités
            </a>
            <a class="edit-nav-item" onclick="goSection(this,'sec-horaires')">
                <span class="edit-nav-ico">🕐</span> Horaires
            </a>
            <a class="edit-nav-item" onclick="goSection(this,'sec-cotis')">
                <span class="edit-nav-ico">💰</span> Cotisations
            </a>
            <a class="edit-nav-item" onclick="goSection(this,'sec-contact')">
                <span class="edit-nav-ico">📞</span> Contact
            </a>
        </nav>

        <%-- ===== FORMULAIRE ===== --%>
        <div class="edit-form">

            <%-- INFOS --%>
            <section class="edit-section" id="sec-infos">
                <div class="edit-section-head">
                    <div class="edit-section-ico">📋</div>
                    <div class="edit-section-title">Informations générales
                        <small>Le nom et la présentation de votre club</small>
                    </div>
                </div>

                <div class="field">
                    <label>Nom du club *</label>
                    <input type="text" name="nomClub" value="${espace.nomClub}" required
                           placeholder="AS Foot Beauvais">
                </div>

                <div class="field">
                    <label>Description</label>
                    <textarea name="description" rows="4"
                              placeholder="Présentez votre club, son histoire, ses valeurs...">${espace.description}</textarea>
                </div>

                <div class="field">
                    <label>Photo du club</label>
                    <label class="file-drop" for="photoInput">
                        <div class="file-drop-ico">🖼️</div>
                        <div class="file-drop-text" id="photoLabel">Cliquez pour choisir une image</div>
                        <div class="file-drop-hint">JPG ou PNG · 5 Mo maximum</div>
                    </label>
                    <input type="file" id="photoInput" name="photo" accept=".jpg,.jpeg,.png"
                           style="display:none;" onchange="majPhoto(this)">
                    <c:if test="${not empty espace.photo}">
                        <div class="file-current">Photo actuelle : ${espace.photo}</div>
                    </c:if>
                </div>
            </section>

            <%-- ACTUALITES --%>
            <section class="edit-section" id="sec-actus">
                <div class="edit-section-head">
                    <div class="edit-section-ico">📰</div>
                    <div class="edit-section-title">Actualités
                        <small>Vos dernières nouvelles et événements</small>
                    </div>
                </div>
                <div class="field">
                    <label>Dernières actualités</label>
                    <textarea name="actualites" rows="6"
                              placeholder="Annoncez vos prochains matchs, résultats, événements...">${espace.actualites}</textarea>
                </div>
            </section>

            <%-- HORAIRES --%>
            <section class="edit-section" id="sec-horaires">
                <div class="edit-section-head">
                    <div class="edit-section-ico">🕐</div>
                    <div class="edit-section-title">Horaires
                        <small>Entraînements et ouverture</small>
                    </div>
                </div>
                <div class="field">
                    <label>Horaires d'entraînement / d'ouverture</label>
                    <textarea name="horaires" rows="4"
                              placeholder="Lundi : 18h-20h&#10;Mercredi : 14h-16h&#10;Samedi : 10h-12h">${espace.horaires}</textarea>
                </div>
            </section>

            <%-- COTISATIONS --%>
            <section class="edit-section" id="sec-cotis">
                <div class="edit-section-head">
                    <div class="edit-section-ico">💰</div>
                    <div class="edit-section-title">Cotisations
                        <small>Le montant de l'adhésion annuelle</small>
                    </div>
                </div>
                <div class="field">
                    <label>Montant annuel (€)</label>
                    <input type="number" name="montantCotisation" value="${espace.montantCotisation}"
                           step="0.01" min="0" placeholder="150.00">
                </div>
            </section>

            <%-- CONTACT --%>
            <section class="edit-section" id="sec-contact">
                <div class="edit-section-head">
                    <div class="edit-section-ico">📞</div>
                    <div class="edit-section-title">Contact
                        <small>Comment vous joindre</small>
                    </div>
                </div>
                <div class="two-col">
                    <div class="field">
                        <label>Téléphone</label>
                        <input type="tel" name="contactTel" value="${espace.contactTel}"
                               placeholder="03 44 12 34 56">
                    </div>
                    <div class="field">
                        <label>Email</label>
                        <input type="email" name="contactEmail" value="${espace.contactEmail}"
                               placeholder="contact@monclub.fr">
                    </div>
                </div>
                <div class="field">
                    <label>Adresse</label>
                    <input type="text" name="adresse" value="${espace.adresse}"
                           placeholder="12 rue du Sport, 60000 Beauvais">
                </div>
            </section>

            <%-- BARRE DE SAUVEGARDE --%>
            <div class="edit-save-bar">
                <a href="${pageContext.request.contextPath}/club" class="btn btn-light">Annuler</a>
                <button type="submit" class="btn btn-primary">💾 Enregistrer</button>
            </div>

        </div>
    </div>
    </form>

</main>

<script>
    // Navigation sidebar : scroll vers la section + surbrillance
    function goSection(item, id) {
        document.querySelectorAll('.edit-nav-item').forEach(function(n) {
            n.classList.remove('active');
        });
        item.classList.add('active');
        document.getElementById(id).scrollIntoView({ behavior: 'smooth', block: 'start' });
    }

    // Affiche le nom du fichier choisi
    function majPhoto(input) {
        var label = document.getElementById('photoLabel');
        if (input.files && input.files.length > 0) {
            label.textContent = input.files[0].name;
        }
    }

    // Surbrillance auto selon le scroll
    var sections = document.querySelectorAll('.edit-section');
    var navItems = document.querySelectorAll('.edit-nav-item');
    window.addEventListener('scroll', function() {
        var pos = window.scrollY + 140;
        sections.forEach(function(sec, i) {
            if (pos >= sec.offsetTop && pos < sec.offsetTop + sec.offsetHeight) {
                navItems.forEach(function(n) { n.classList.remove('active'); });
                if (navItems[i]) navItems[i].classList.add('active');
            }
        });
    });
</script>

</body>
</html>
