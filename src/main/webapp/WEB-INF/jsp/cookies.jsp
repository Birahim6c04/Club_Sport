<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Politique des cookies - Club Sportif</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="legal-page">
        <div class="legal-container">
            <a href="${pageContext.request.contextPath}/accueil" class="legal-back">← Retour à l'accueil</a>

            <h1>Politique de cookies</h1>
            <p class="legal-date">Dernière mise à jour : Mai 2026</p>

            <section>
                <h2>Qu'est-ce qu'un cookie ?</h2>
                <p>Un cookie est un petit fichier texte déposé sur votre appareil par votre navigateur lorsque vous visitez un site web. Il permet au site de mémoriser certaines informations.</p>
            </section>

            <section>
                <h2>Cookies utilisés sur ce site</h2>
                <p>Notre site utilise uniquement les cookies suivants :</p>

                <table class="cookies-table">
                    <thead>
                        <tr>
                            <th>Nom</th>
                            <th>Type</th>
                            <th>Finalité</th>
                            <th>Durée</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td><code>cookies-consent</code></td>
                            <td>Technique</td>
                            <td>Mémoriser votre choix concernant les cookies</td>
                            <td>1 an</td>
                        </tr>
                        <tr>
                            <td><code>JSESSIONID</code></td>
                            <td>Session</td>
                            <td>Maintenir votre connexion utilisateur (uniquement si connecté)</td>
                            <td>Session</td>
                        </tr>
                    </tbody>
                </table>
            </section>

            <section>
                <h2>Aucun cookie tiers</h2>
                <p>Nous n'utilisons <strong>aucun</strong> cookie de tracking, publicitaire ou tiers (Google Analytics, Facebook Pixel, etc.).</p>
            </section>

            <section>
                <h2>Gérer vos cookies</h2>
                <p>Vous pouvez à tout moment supprimer les cookies via les paramètres de votre navigateur. Vous pouvez également réinitialiser votre choix ci-dessous :</p>
                <button id="reset-cookies" class="btn-rechercher" style="max-width:300px;">Réinitialiser mon choix</button>
            </section>

            <section>
                <h2>Vos droits</h2>
                <p>Conformément au RGPD, vous avez le droit de refuser tout cookie non essentiel sans que cela n'affecte votre navigation. Voir nos <a href="${pageContext.request.contextPath}/mentions">Mentions légales</a> pour plus d'informations.</p>
            </section>
        </div>
    </div>

    <jsp:include page="/WEB-INF/jsp/cookies_banner.jsp" />

    <script>
        document.getElementById('reset-cookies').addEventListener('click', function() {
            localStorage.removeItem('cookies-consent');
            location.reload();
        });
    </script>

</body>
</html>
