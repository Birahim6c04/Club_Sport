<%-- Bannière cookies RGPD - à inclure avec <jsp:include page="/WEB-INF/jsp/cookies_banner.jsp" /> --%>

<div id="cookies-banner" class="cookies-banner" style="display:none;">
    <div class="cookies-content">
        <div class="cookies-text">
            <strong>🍪 Nous utilisons des cookies</strong>
            <p>Notre site utilise des cookies pour assurer son bon fonctionnement et mesurer son audience.
               Aucune donnée personnelle n'est partagée avec des tiers.</p>
        </div>
        <div class="cookies-buttons">
            <button id="cookies-refuse"  class="btn-cookies-ghost">Refuser</button>
            <button id="cookies-accept"  class="btn-cookies-primary">Tout accepter</button>
        </div>
    </div>
</div>

<script>
(function() {
    var banner = document.getElementById('cookies-banner');
    var choix = localStorage.getItem('cookies-consent');

    // Afficher si pas encore choisi
    if (!choix) {
        banner.style.display = 'flex';
    }

    document.getElementById('cookies-accept').addEventListener('click', function() {
        localStorage.setItem('cookies-consent', 'accept');
        banner.style.display = 'none';
    });

    document.getElementById('cookies-refuse').addEventListener('click', function() {
        localStorage.setItem('cookies-consent', 'refuse');
        banner.style.display = 'none';
    });
})();
</script>
