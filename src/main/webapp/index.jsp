<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Club Sportif - Recherche de clubs</title>
 
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
</head>
 
<body>
 
    <!-- NAVBAR -->
    <div class="navbar">
        <div class="nav-left">
            <h1>â½ Club Sportif</h1>
            <a href="#">Accueil</a>
            <a href="#recherche">Recherche</a>
            <a href="#carte">Carte</a>
        </div>
        <div class="nav-right">
            <a href="#" class="btn inscription">S'inscrire</a>
            <a href="#" class="btn connexion">Se connecter</a>
        </div>
    </div>
 
    <!-- =========================
         HERO
    ========================= -->
    <div class="hero">
        <h2>Trouvez un club sportif prÃ¨s de chez vous</h2>
        <p>Plus de 100 000 clubs affiliÃ©s aux fÃ©dÃ©rations sportives franÃ§aises</p>
    </div>
 
    <!-- =========================
         SECTION RECHERCHE
    ========================= -->
    <div class="recherche-section" id="recherche">
        <h3>ð Rechercher des clubs</h3>
 
        <!-- Onglets pour les 2 modes de recherche -->
        <div class="onglets">
            <button class="onglet actif" data-mode="zone">Par zone gÃ©ographique</button>
            <button class="onglet" data-mode="rayon">Par rayon</button>
        </div>
 
        <!-- ============= MODE 1 : par zone gÃ©ographique ============= -->
        <div class="formulaire" id="formulaire-zone">
 
            <div class="champ">
                <label for="federation-zone">FÃ©dÃ©ration sportive *</label>
                <select id="federation-zone">
                    <option value="">-- Choisir une fÃ©dÃ©ration --</option>
                </select>
            </div>
 
            <div class="champ">
                <label for="type-zone">Type de zone</label>
                <select id="type-zone">
                    <option value="region">RÃ©gion</option>
                    <option value="codepostal">Code postal</option>
                </select>
            </div>
 
            <!-- Sous-champ : rÃ©gion -->
            <div class="champ" id="champ-region">
                <label for="region">RÃ©gion</label>
                <select id="region">
                    <option value="">-- Toutes les rÃ©gions --</option>
                </select>
            </div>
 
            <!-- Sous-champ : code postal (cachÃ© par dÃ©faut) -->
            <div class="champ" id="champ-cp" style="display:none">
                <label for="codepostal">Code postal</label>
                <input type="text" id="codepostal" placeholder="Ex : 75001" maxlength="5">
            </div>
 
            <button class="btn-rechercher" id="btn-recherche-zone">Rechercher</button>
        </div>
 
        <!-- ============= MODE 2 : par rayon ============= -->
        <div class="formulaire" id="formulaire-rayon" style="display:none">
 
            <div class="champ">
                <label for="federation-rayon">FÃ©dÃ©ration sportive *</label>
                <select id="federation-rayon">
                    <option value="">-- Choisir une fÃ©dÃ©ration --</option>
                </select>
            </div>
 
            <div class="champ">
                <label for="commune">Commune de rÃ©fÃ©rence *</label>
                <input type="text" id="commune" placeholder="Tapez le nom d'une commune..." autocomplete="off">
                <div id="suggestions-commune" class="suggestions"></div>
                <input type="hidden" id="commune-code">
            </div>
 
            <div class="champ">
                <label for="rayon">Rayon de recherche</label>
                <select id="rayon">
                    <option value="10">10 km</option>
                    <option value="20" selected>20 km</option>
                    <option value="50">50 km</option>
                    <option value="100">100 km</option>
                </select>
            </div>
 
            <button class="btn-rechercher" id="btn-recherche-rayon">Rechercher</button>
        </div>
    </div>
 
    <!-- =========================
         RESULTATS
    ========================= -->
    <div class="resultats-section" id="resultats-section" style="display:none">
        <h3>ð RÃ©sultats de la recherche</h3>
        <div class="info-resultats" id="info-resultats"></div>
        <div class="liste-resultats" id="liste-resultats"></div>
    </div>
 
    <!-- =========================
         CARTE
    ========================= -->
    <div class="map-section" id="carte">
        <h3>ð Carte des clubs</h3>
        <div id="map"></div>
    </div>
 
    <!-- =========================
         FOOTER
    ========================= -->
    <footer>
        <p>Â© 2026 Club Sportif - DonnÃ©es : MinistÃ¨re des Sports</p>
        <p><a href="#">Mentions lÃ©gales</a> Â· <a href="#">Politique de cookies</a></p>
    </footer>
 
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script src="js/app.js"></script>
</body>
</html>