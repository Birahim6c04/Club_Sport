<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Club Sportif - Recherche de clubs</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
</head>
<body>

    <!-- NAVBAR -->
    <div class="navbar">
        <div class="nav-left">
            <h1>⚽ Club Sportif</h1>
            <a href="${pageContext.request.contextPath}/accueil">Accueil</a>
            <a href="#recherche">Recherche</a>
            <a href="#carte">Carte</a>
        </div>
        <div class="nav-right">
            <a href="#" class="btn inscription">S'inscrire</a>
            <a href="#" class="btn connexion">Se connecter</a>
        </div>
    </div>

    <!-- HERO -->
    <div class="hero">
        <h2>Trouvez un club sportif près de chez vous</h2>
        <p>Plus de 100 000 clubs affiliés aux fédérations sportives françaises</p>
    </div>

    <!-- MESSAGE D'ERREUR -->
    <c:if test="${not empty erreur}">
        <div style="max-width:800px;margin:20px auto;padding:15px;background:#fee2e2;color:#b91c1c;border-radius:8px;text-align:center">
            ⚠️ ${erreur}
        </div>
    </c:if>

    <!-- FORMULAIRE DE RECHERCHE -->
    <div class="recherche-section" id="recherche">
        <h3>🔍 Rechercher des clubs</h3>

        <!-- Onglets -->
        <div class="onglets">
            <button class="onglet ${modeRecherche != 'rayon' ? 'actif' : ''}" data-mode="zone" type="button">Par zone géographique</button>
            <button class="onglet ${modeRecherche == 'rayon' ? 'actif' : ''}" data-mode="rayon" type="button">Par rayon</button>
        </div>

        <!-- MODE 1 : par zone -->
        <form id="form-zone" class="formulaire" action="${pageContext.request.contextPath}/clubs/rechercher" method="POST" style="${modeRecherche == 'rayon' ? 'display:none' : ''}">
            <input type="hidden" name="mode" value="zone">

            <div class="champ">
                <label for="federation-zone">Fédération sportive *</label>
                <select id="federation-zone" name="federation" required>
                   <option value="101" ${federationChoisie == '101' ? 'selected' : ''}>FF
						d'Athlétisme</option>
					<option value="102" ${federationChoisie == '102' ? 'selected' : ''}>FF
						d'Aviron</option>
					<option value="103" ${federationChoisie == '103' ? 'selected' : ''}>FF
						de Badminton</option>
					<option value="105" ${federationChoisie == '105' ? 'selected' : ''}>FF
						de Basketball</option>
					<option value="106" ${federationChoisie == '106' ? 'selected' : ''}>FF
						de Boxe</option>
					<option value="107" ${federationChoisie == '107' ? 'selected' : ''}>FF
						de Canoë-Kayak et Sports de Pagaie</option>
					<option value="108" ${federationChoisie == '108' ? 'selected' : ''}>FF
						de Cyclisme</option>
					<option value="109" ${federationChoisie == '109' ? 'selected' : ''}>FF
						d'Équitation</option>
					<option value="110" ${federationChoisie == '110' ? 'selected' : ''}>FF
						d'Escrime</option>
					<option value="111" ${federationChoisie == '111' ? 'selected' : ''}>FF
						de Football</option>
					<option value="112" ${federationChoisie == '112' ? 'selected' : ''}>FF
						des Sports de Glace</option>
					<option value="113" ${federationChoisie == '113' ? 'selected' : ''}>FF
						de Gymnastique</option>
					<option value="114" ${federationChoisie == '114' ? 'selected' : ''}>FF
						d'Haltérophilie, Musculation</option>
					<option value="115" ${federationChoisie == '115' ? 'selected' : ''}>FF
						de Handball</option>
					<option value="116" ${federationChoisie == '116' ? 'selected' : ''}>FF
						de Hockey</option>
					<option value="117" ${federationChoisie == '117' ? 'selected' : ''}>FF
						de Judo, Jujitsu, Kendo et DA</option>
					<option value="118" ${federationChoisie == '118' ? 'selected' : ''}>FF
						de Lutte</option>
					<option value="119" ${federationChoisie == '119' ? 'selected' : ''}>FF
						de Natation</option>
					<option value="120" ${federationChoisie == '120' ? 'selected' : ''}>FF
						de Pentathlon Moderne</option>
					<option value="121" ${federationChoisie == '121' ? 'selected' : ''}>FF
						de Ski</option>
					<option value="122" ${federationChoisie == '122' ? 'selected' : ''}>FF
						de Taekwondo et DA</option>
					<option value="123" ${federationChoisie == '123' ? 'selected' : ''}>FF
						de Tennis</option>
					<option value="124" ${federationChoisie == '124' ? 'selected' : ''}>FF
						de Tennis de Table</option>
					<option value="125" ${federationChoisie == '125' ? 'selected' : ''}>FF
						de Tir</option>
					<option value="126" ${federationChoisie == '126' ? 'selected' : ''}>FF
						de Tir à l'Arc</option>
					<option value="127" ${federationChoisie == '127' ? 'selected' : ''}>FF
						de Triathlon et Disciplines Enchaînées</option>
					<option value="128" ${federationChoisie == '128' ? 'selected' : ''}>FF
						de Voile</option>
					<option value="129" ${federationChoisie == '129' ? 'selected' : ''}>FF
						de Volley</option>
					<option value="131" ${federationChoisie == '131' ? 'selected' : ''}>FF
						de Hockey sur Glace</option>
					<option value="132" ${federationChoisie == '132' ? 'selected' : ''}>FF
						de Golf</option>
					<option value="133" ${federationChoisie == '133' ? 'selected' : ''}>FF
						de Rugby</option>
					<option value="135" ${federationChoisie == '135' ? 'selected' : ''}>FF
						de la Montagne et de l'Escalade</option>
					<option value="136" ${federationChoisie == '136' ? 'selected' : ''}>FF
						de Roller et Skateboard</option>
					<option value="137" ${federationChoisie == '137' ? 'selected' : ''}>FF
						de Surf</option>
					<option value="138" ${federationChoisie == '138' ? 'selected' : ''}>FF
						de Baseball, Softball</option>
					<option value="139" ${federationChoisie == '139' ? 'selected' : ''}>FF
						de Danse</option>
					<option value="140" ${federationChoisie == '140' ? 'selected' : ''}>FF
						de Football Américain</option>
					<option value="141" ${federationChoisie == '141' ? 'selected' : ''}>FF
						de Squash</option>
					<option value="201" ${federationChoisie == '201' ? 'selected' : ''}>FF
						d'Aéromodélisme</option>
					<option value="202" ${federationChoisie == '202' ? 'selected' : ''}>FF
						Aéronautique</option>
					<option value="203" ${federationChoisie == '203' ? 'selected' : ''}>FF
						d'Aérostation</option>
					<option value="204" ${federationChoisie == '204' ? 'selected' : ''}>FF
						d'Aïkido, d'Aïkibudo et Affinitaires</option>
					<option value="205" ${federationChoisie == '205' ? 'selected' : ''}>FF
						d'Aïkido et de Budo</option>
					<option value="206" ${federationChoisie == '206' ? 'selected' : ''}>FF
						du Sport Automobile</option>
					<option value="208" ${federationChoisie == '208' ? 'selected' : ''}>FF
						de Jeu de Balle au Tambourin</option>
					<option value="209" ${federationChoisie == '209' ? 'selected' : ''}>FF
						de Ball-Trap</option>
					<option value="210" ${federationChoisie == '210' ? 'selected' : ''}>FF
						de Billard</option>
					<option value="211" ${federationChoisie == '211' ? 'selected' : ''}>FF
						du Sport Boules</option>
					<option value="212" ${federationChoisie == '212' ? 'selected' : ''}>FF
						de Savate, Boxe Française et DA</option>
					<option value="213" ${federationChoisie == '213' ? 'selected' : ''}>FF
						de Bowling et de Sport de Quilles</option>
					<option value="214" ${federationChoisie == '214' ? 'selected' : ''}>FF
						de Char à Voile</option>
					<option value="215" ${federationChoisie == '215' ? 'selected' : ''}>FF
						de Course Camarguaise</option>
					<option value="216" ${federationChoisie == '216' ? 'selected' : ''}>FF
						de la Course Landaise</option>
					<option value="217" ${federationChoisie == '217' ? 'selected' : ''}>FF
						de Course d'Orientation</option>
					<option value="218" ${federationChoisie == '218' ? 'selected' : ''}>FF
						de Cyclotourisme</option>
					<option value="220" ${federationChoisie == '220' ? 'selected' : ''}>FF
						des Échecs</option>
					<option value="221" ${federationChoisie == '221' ? 'selected' : ''}>FF
						d'Études et Sports Sous-Marins</option>
					<option value="224" ${federationChoisie == '224' ? 'selected' : ''}>FF
						d'Hélicoptère</option>
					<option value="226" ${federationChoisie == '226' ? 'selected' : ''}>FF
						de Javelot Tir sur Cible</option>
					<option value="227" ${federationChoisie == '227' ? 'selected' : ''}>FF
						de Jeu de Paume</option>
					<option value="228" ${federationChoisie == '228' ? 'selected' : ''}>FF
						de Joute et Sauvetage Nautique</option>
					<option value="229" ${federationChoisie == '229' ? 'selected' : ''}>FF
						de Karaté et DA</option>
					<option value="231" ${federationChoisie == '231' ? 'selected' : ''}>FF
						de Longue Paume</option>
					<option value="233" ${federationChoisie == '233' ? 'selected' : ''}>FF
						Motocyclisme</option>
					<option value="234" ${federationChoisie == '234' ? 'selected' : ''}>FF
						Motonautique</option>
					<option value="237" ${federationChoisie == '237' ? 'selected' : ''}>FF
						de Parachutisme</option>
					<option value="241" ${federationChoisie == '241' ? 'selected' : ''}>FF
						de Pelote Basque</option>
					<option value="244" ${federationChoisie == '244' ? 'selected' : ''}>FF
						de Pulka et Traîneau à Chiens</option>
					<option value="248" ${federationChoisie == '248' ? 'selected' : ''}>FF
						de Rugby à XIII</option>
					<option value="249" ${federationChoisie == '249' ? 'selected' : ''}>FF
						de Sauvetage et de Secourisme</option>
					<option value="250" ${federationChoisie == '250' ? 'selected' : ''}>FF
						de Ski Nautique et de Wakeboard</option>
					<option value="251" ${federationChoisie == '251' ? 'selected' : ''}>FF
						Spéléologie</option>
					<option value="254" ${federationChoisie == '254' ? 'selected' : ''}>FF
						des Arts Énergétiques et Martiaux Chinois</option>
					<option value="257" ${federationChoisie == '257' ? 'selected' : ''}>FF
						de Vol en Planeur</option>
					<option value="258" ${federationChoisie == '258' ? 'selected' : ''}>FF
						de Vol Libre</option>
					<option value="260" ${federationChoisie == '260' ? 'selected' : ''}>FF
						de Polo</option>
					<option value="261" ${federationChoisie == '261' ? 'selected' : ''}>FF
						de Kick Boxing, Muay Thaï et DA</option>
					<option value="263" ${federationChoisie == '263' ? 'selected' : ''}>FF
						de Double Dutch</option>
					<option value="264" ${federationChoisie == '264' ? 'selected' : ''}>FF
						de Flying Disc</option>
					<option value="266" ${federationChoisie == '266' ? 'selected' : ''}>FF
						de Force</option>
					<option value="267" ${federationChoisie == '267' ? 'selected' : ''}>FF
						des Pêches Sportives</option>
					<option value="402" ${federationChoisie == '402' ? 'selected' : ''}>FF
						d'Éducation Physique et de Gymnastique Volontaire</option>
					<option value="403" ${federationChoisie == '403' ? 'selected' : ''}>FF
						Sports Pour Tous</option>
					<option value="404" ${federationChoisie == '404' ? 'selected' : ''}>FF
						de la Retraite Sportive</option>
					<option value="409" ${federationChoisie == '409' ? 'selected' : ''}>F
						Maccabi</option>
					<option value="420" ${federationChoisie == '420' ? 'selected' : ''}>F
						Sportive des ASPTT</option>
					<option value="501" ${federationChoisie == '501' ? 'selected' : ''}>FF
						Handisport</option>
					<option value="503" ${federationChoisie == '503' ? 'selected' : ''}>FF
						du Sport Adapté</option>
					<option value="601" ${federationChoisie == '601' ? 'selected' : ''}>FF
						du Sport Universitaire</option>
					<option value="602" ${federationChoisie == '602' ? 'selected' : ''}>F
						Sportive Educative de l'Enseignement</option>
					<option value="603" ${federationChoisie == '603' ? 'selected' : ''}>Union
						Nationale des Clubs Universitaires</option>
					<option value="604" ${federationChoisie == '604' ? 'selected' : ''}>Union
						Nationale du Sport Scolaire</option>
                </select>
            </div>

            <div class="champ">
                <label for="region">Région</label>
                <select id="region" name="region">
                    <option value="">-- Toutes les régions --</option>
                    <c:forEach items="${regions}" var="r">
                        <option value="${r}" ${regionChoisie == r ? 'selected' : ''}>${r}</option>
                    </c:forEach>
                </select>
            </div>

            <button type="submit" class="btn-rechercher">Rechercher</button>
        </form>

        <!-- MODE 2 : par rayon -->
        <form id="form-rayon" class="formulaire" action="${pageContext.request.contextPath}/clubs/rechercher" method="POST" style="${modeRecherche == 'rayon' ? '' : 'display:none'}">
            <input type="hidden" name="mode" value="rayon">

            <div class="champ">
                <label for="federation-rayon">Fédération sportive *</label>
                <select id="federation-rayon" name="federation" required>
                   <option value="">-- Choisir une fédération --</option>
					<option value="101">FF d'Athlétisme</option>
					<option value="102">FF d'Aviron</option>
					<option value="103">FF de Badminton</option>
					<option value="105">FF de Basketball</option>
					<option value="106">FF de Boxe</option>
					<option value="107">FF de Canoë-Kayak et Sports de Pagaie</option>
					<option value="108">FF de Cyclisme</option>
					<option value="109">FF d'Équitation</option>
					<option value="110">FF d'Escrime</option>
					<option value="111">FF de Football</option>
					<option value="112">FF des Sports de Glace</option>
					<option value="113">FF de Gymnastique</option>
					<option value="114">FF d'Haltérophilie, Musculation</option>
					<option value="115">FF de Handball</option>
					<option value="116">FF de Hockey</option>
					<option value="117">FF de Judo, Jujitsu, Kendo et DA</option>
					<option value="118">FF de Lutte</option>
					<option value="119">FF de Natation</option>
					<option value="120">FF de Pentathlon Moderne</option>
					<option value="121">FF de Ski</option>
					<option value="122">FF de Taekwondo et DA</option>
					<option value="123">FF de Tennis</option>
					<option value="124">FF de Tennis de Table</option>
					<option value="125">FF de Tir</option>
					<option value="126">FF de Tir à l'Arc</option>
					<option value="127">FF de Triathlon et Disciplines
						Enchaînées</option>
					<option value="128">FF de Voile</option>
					<option value="129">FF de Volley</option>
					<option value="131">FF de Hockey sur Glace</option>
					<option value="132">FF de Golf</option>
					<option value="133">FF de Rugby</option>
					<option value="135">FF de la Montagne et de l'Escalade</option>
					<option value="136">FF de Roller et Skateboard</option>
					<option value="137">FF de Surf</option>
					<option value="138">FF de Baseball, Softball</option>
					<option value="139">FF de Danse</option>
					<option value="140">FF de Football Américain</option>
					<option value="141">FF de Squash</option>
					<option value="201">FF d'Aéromodélisme</option>
					<option value="202">FF Aéronautique</option>
					<option value="203">FF d'Aérostation</option>
					<option value="204">FF d'Aïkido, d'Aïkibudo et Affinitaires</option>
					<option value="205">FF d'Aïkido et de Budo</option>
					<option value="206">FF du Sport Automobile</option>
					<option value="207">FF de Jeu de Balle au Tambourin</option>
					<option value="208">FF de Ballon au Poing</option>
					<option value="209">FF de Ball-Trap</option>
					<option value="210">FF de Billard</option>
					<option value="211">FF du Sport Boules</option>
					<option value="212">FF de Savate, Boxe Française et DA</option>
					<option value="213">FF de Bowling et de Sport de Quilles</option>
					<option value="214">FF de Char à Voile</option>
					<option value="215">FF de Course Camarguaise</option>
					<option value="216">FF de la Course Landaise</option>
					<option value="217">FF de Course d'Orientation</option>
					<option value="218">FF de Cyclotourisme</option>
					<option value="220">FF des Échecs</option>
					<option value="221">FF d'Études et Sports Sous-Marins</option>
					<option value="224">FF d'Hélicoptère</option>
					<option value="226">FF de Javelot Tir sur Cible</option>
					<option value="227">FF de Jeu de Paume</option>
					<option value="228">FF de Joute et Sauvetage Nautique</option>
					<option value="229">FF de Karaté et DA</option>
					<option value="231">FF de Longue Paume</option>
					<option value="233">FF de Motocyclisme</option>
					<option value="234">FF Motonautique</option>
					<option value="237">FF de Parachutisme</option>
					<option value="241">FF de Pelote Basque</option>
					<option value="242">FF de Pétanque et Jeu Provençal</option>
					<option value="243">FF de Planeur Ultraléger Motorisé</option>
					<option value="244">FF de Pulka et Traineau à Chiens</option>
					<option value="245">FF de la Randonnée Pédestre</option>
					<option value="248">FF de Rugby à XIII</option>
					<option value="249">FF de Sauvetage et de Secourisme</option>
					<option value="250">FF de Ski Nautique et de Wakeboard</option>
					<option value="251">FF de Spéléologie</option>
					<option value="254">F des Arts Énergétiques et Martiaux</option>
					<option value="255">FF des Sports de Traineau, de Ski V...</option>
					<option value="256">FF Sportive de Twirling Bâton</option>
					<option value="257">FF de Vol en Planeur</option>
					<option value="258">FF de Vol Libre</option>
					<option value="260">FF de Polo</option>
					<option value="261">FF de Kick Boxing, Muay Thaï et DA</option>
					<option value="263">F de Double Dutch</option>
					<option value="264">FF de Flying Disc</option>
					<option value="265">FF de Pêche Sportive en Apnée</option>
					<option value="266">FF de Force</option>
					<option value="267">FF des Pêches Sportives</option>
					<option value="268">F de Boxe Américaine et DA</option>
					<option value="269">F de Voitures Radio Commandées</option>
					<option value="270">FF des Sports et Loisirs Canins</option>
					<option value="271">FF des Clubs Alpins et de Montagne</option>
					<option value="402">FF d'Éducation Physique et de Gymnastique</option>
					<option value="403">FF Sports Pour Tous</option>
					<option value="404">FF de la Retraite Sportive</option>
					<option value="405">FF du Sport Travailliste</option>
					<option value="406">F des Clubs de la Défense</option>
					<option value="407">F Nationale du Sport en Milieu Rural</option>
					<option value="408">F Sportive et Culturelle de France</option>
					<option value="409">FF Maccabi</option>
					<option value="410">F Sportive et Gymnique du Travail</option>
					<option value="411">F Sportive de la Police Nationale</option>
					<option value="412">Union Française des Œuvres Laïques</option>
					<option value="415">FF Omnisports des Personnels de l'A...</option>
					<option value="417">Union Nationale Sportive Léo Lagrange</option>
					<option value="418">FF du Sport d'Entreprise</option>
					<option value="420">F Sportive des ASPTT</option>
					<option value="421">FF des Sports Populaires</option>
					<option value="422">FF des Clubs Omnisports</option>
					<option value="423">F Nationale des Offices Municipaux</option>
					<option value="424">UCPA Sport Loisirs</option>
					<option value="425">UCPA Sport Vacances</option>
					<option value="426">F Sportive LGBT+</option>
					<option value="501">FF Handisport</option>
					<option value="503">FF du Sport Adapté</option>
					<option value="601">FF du Sport Universitaire</option>
					<option value="602">F Sportive Éducative de l'Enseignement</option>
					<option value="603">Union Nationale des Clubs Universitaires</option>
					<option value="604">Union Nationale du Sport Scolaire (UNSS)</option>
					<option value="605">Union Sportive de l'Enseignement du Premier Degré</option>
                </select>
            </div>

            <div class="champ">
                <label for="commune-input">Commune de référence *</label>
                <input type="text" id="commune-input" placeholder="Tapez le nom d'une commune..." autocomplete="off" required>
                <div id="suggestions-commune" class="suggestions"></div>
                <input type="hidden" id="commune-code" name="commune" value="${communeChoisie}">
            </div>

            <div class="champ">
                <label for="rayon">Rayon de recherche</label>
                <select id="rayon" name="rayon">
                    <option value="10" ${rayonChoisi == '10' ? 'selected' : ''}>10 km</option>
                    <option value="20" ${rayonChoisi == '20' || empty rayonChoisi ? 'selected' : ''}>20 km</option>
                    <option value="50" ${rayonChoisi == '50' ? 'selected' : ''}>50 km</option>
                    <option value="100" ${rayonChoisi == '100' ? 'selected' : ''}>100 km</option>
                </select>
            </div>

            <button type="submit" class="btn-rechercher">Rechercher</button>
        </form>
    </div>

    <!-- RÉSULTATS -->
    <c:if test="${not empty clubs}">
        <div class="resultats-section">
            <h3>📋 Résultats de la recherche</h3>
            <p class="info-resultats">${nbResultats} commune(s) trouvée(s)</p>

        </div>
    </c:if>

    <!-- CARTE -->
    <div class="map-section" id="carte">
        <h3>📍 Carte des clubs</h3>
        <div id="map"></div>
    </div>

    <!-- FOOTER -->
    <footer>
        <p>© 2026 Club Sportif - Données : Ministère des Sports</p>
        <p><a href="#">Mentions légales</a> · <a href="#">Politique de cookies</a></p>
    </footer>

    <!-- Scripts -->
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script>
        // Variables passées du serveur au JavaScript
        window.contextPath       = '${pageContext.request.contextPath}';
        window.federationChoisie = '${federationChoisie}';
        window.regionChoisie     = '${regionChoisie}';
        window.communeChoisie    = '${communeChoisie}';
        window.rayonChoisi       = '${rayonChoisi}';
        window.modeRecherche     = '${modeRecherche}';
    </script>
    <script src="${pageContext.request.contextPath}/js/carte.js"></script>
</body>
</html>
