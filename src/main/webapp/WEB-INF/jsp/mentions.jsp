<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mentions légales - Club Sportif</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="legal-page">
        <div class="legal-container">
            <a href="${pageContext.request.contextPath}/accueil" class="legal-back">← Retour à l'accueil</a>

            <h1>Mentions légales</h1>
            <p class="legal-date">Dernière mise à jour : Mai 2026</p>

            <section>
                <h2>1. Éditeur du site</h2>
                <p><strong>Club Sportif</strong> est un projet étudiant réalisé dans le cadre du semestre 8 à l'École Supérieure d'Ingénieurs en Génie Électrique (ESIGELEC).</p>
                <ul>
                    <li><strong>Établissement</strong> : ESIGELEC</li>
                    <li><strong>Adresse</strong> : Technopôle du Madrillet, 76800 Saint-Étienne-du-Rouvray</li>
                    <li><strong>Email contact</strong> : contact@clubsportif.fr</li>
                </ul>
            </section>

            <section>
                <h2>2. Hébergement</h2>
                <p>Site hébergé en local pour les besoins du projet étudiant. Aucun hébergement commercial.</p>
            </section>

            <section>
                <h2>3. Données sources</h2>
                <p>Les données sportives proviennent de sources publiques officielles :</p>
                <ul>
                    <li><strong>data.gouv.fr</strong> : statistiques 2019 du Ministère des Sports</li>
                    <li><strong>OpenStreetMap</strong> : fond de carte (sous licence ODbL)</li>
                </ul>
            </section>

            <section>
                <h2>4. Propriété intellectuelle</h2>
                <p>Le code source, la structure du site et les éléments graphiques sont la propriété des étudiants du projet. Les données utilisées sont sous licence ouverte (Licence Ouverte 2.0).</p>
            </section>

            <section>
                <h2>5. Protection des données personnelles (RGPD)</h2>
                <p>Conformément au Règlement Général sur la Protection des Données (RGPD - Règlement UE 2016/679), nous vous informons que :</p>
                <ul>
                    <li>Aucune donnée personnelle n'est collectée sans votre consentement</li>
                    <li>Les utilisateurs inscrits fournissent : nom, prénom, email, login, mot de passe (haché avec BCrypt)</li>
                    <li>Les mots de passe sont stockés hachés et ne sont jamais accessibles en clair</li>
                    <li>Les logs de connexion enregistrent : login, IP, date, succès/échec — uniquement à des fins de sécurité</li>
                    <li>Aucune donnée n'est transmise à des tiers</li>
                </ul>

                <p><strong>Vos droits</strong> : vous disposez d'un droit d'accès, de rectification, de suppression et de portabilité de vos données. Pour exercer ces droits, contactez-nous à <em>contact@clubsportif.fr</em>.</p>
            </section>

            <section>
                <h2>6. Cookies</h2>
                <p>Notre site utilise un seul cookie technique pour mémoriser votre choix concernant les cookies. Aucun cookie de tracking ou publicitaire n'est utilisé.</p>
                <p>Voir notre <a href="${pageContext.request.contextPath}/cookies">Politique de cookies</a>.</p>
            </section>

            <section>
                <h2>7. Limitation de responsabilité</h2>
                <p>Les informations affichées sont issues de sources publiques et peuvent contenir des inexactitudes. L'éditeur ne saurait être tenu responsable d'éventuelles erreurs ou omissions.</p>
            </section>

            <section>
                <h2>8. Droit applicable</h2>
                <p>Le présent site est soumis au droit français. Tout litige relatif à son utilisation relève de la compétence des tribunaux français.</p>
            </section>
        </div>
    </div>

    <jsp:include page="/WEB-INF/jsp/cookies_banner.jsp" />

</body>
</html>
