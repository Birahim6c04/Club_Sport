<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>

<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Club Sportif - Recherche de clubs</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet"
	href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
</head>
<body>

	<!-- NAVBAR -->
	<div class="navbar">
		<div class="nav-left">
			<h1>⚽ Club Sportif</h1>
			<a href="${pageContext.request.contextPath}/accueil">Accueil</a> <a
				href="#recherche">Recherche</a> <a href="#carte">Carte</a>
		</div>
		<div class="nav-right">
			<a href="#" class="btn inscription">S'inscrire</a> <a href="#"
				class="btn connexion">Se connecter</a>
		</div>
	</div>

	<!-- HERO -->
	<div class="hero">
		<h2>Trouvez un club sportif près de chez vous</h2>
		<p>Plus de 100 000 clubs affiliés aux fédérations sportives
			françaises</p>
	</div>

	<!-- MESSAGE D'ERREUR -->
	<c:if test="${not empty erreur}">
		<div class="message-erreur">⚠️ ${erreur}</div>
	</c:if>

	<!-- FORMULAIRE DE RECHERCHE -->
	<div class="recherche-section" id="recherche">
		<h3>🔍 Rechercher des clubs</h3>

		<!-- Onglets -->
		<div class="onglets">
			<button class="onglet ${modeRecherche != 'rayon' ? 'actif' : ''}"
				data-mode="zone" type="button">Par zone géographique</button>
			<button class="onglet ${modeRecherche == 'rayon' ? 'actif' : ''}"
				data-mode="rayon" type="button">Par rayon</button>
		</div>

		<!-- MODE 1 : par zone -->
		<form id="form-zone" class="formulaire"
			action="${pageContext.request.contextPath}/clubs/rechercher"
			method="POST"
			style="${modeRecherche == 'rayon' ? 'display:none' : ''}">
			<input type="hidden" name="mode" value="zone">

			<div class="champ">
				<label>Fédération sportive *</label> <select name="federation"
					required>
					<option value="">-- Choisir une fédération --</option>
					<jsp:include page="/WEB-INF/jsp/federations.jsp" />
				</select>
			</div>

			<div class="champ">
				<label>Code postal (optionnel)</label> <input type="text"
					name="codePostal" value="${codePostalChoisi}"
					placeholder="Ex : 76000" maxlength="5">
			</div>

			<div class="champ">
				<label>Région</label> <select name="region">
					<option value="">-- Toutes les régions --</option>
					<c:forEach items="${regions}" var="r">
						<option value="${r}" ${regionChoisie == r ? 'selected' : ''}>${r}</option>
					</c:forEach>
				</select>
			</div>

			<button type="submit" class="btn-rechercher">Rechercher</button>
		</form>

		<!-- MODE 2 : par rayon -->
		<form id="form-rayon" class="formulaire"
			action="${pageContext.request.contextPath}/clubs/rechercher"
			method="POST"
			style="${modeRecherche == 'rayon' ? '' : 'display:none'}">
			<input type="hidden" name="mode" value="rayon">

			<div class="champ">
				<label>Fédération sportive *</label> <select name="federation"
					required>
					<option value="">-- Choisir une fédération --</option>
					<jsp:include page="/WEB-INF/jsp/federations.jsp" />
				</select>
			</div>

			<div class="champ">
				<label>Commune de référence *</label> <input type="text"
					id="commune-input" placeholder="Tapez le nom d'une commune..."
					autocomplete="off" required>
				<div id="suggestions-commune" class="suggestions"></div>
				<input type="hidden" id="commune-code" name="commune"
					value="${communeChoisie}">
			</div>

			<div class="champ">
				<label>Rayon de recherche</label> <select name="rayon">
					<option value="10" ${rayonChoisi == '10'  ? 'selected' : ''}>10
						km</option>
					<option value="20"
						${rayonChoisi == '20' || empty rayonChoisi ? 'selected' : ''}>20
						km</option>
					<option value="50" ${rayonChoisi == '50'  ? 'selected' : ''}>50
						km</option>
					<option value="100" ${rayonChoisi == '100' ? 'selected' : ''}>100
						km</option>
				</select>
			</div>

			<button type="submit" class="btn-rechercher">Rechercher</button>
		</form>
	</div>

	<!-- RÉSULTATS -->
	<c:if test="${not empty clubs}">
		<div class="resultats-section">
			<h3>📋 Résultats de la recherche</h3>
			<p class="info-resultats">${nbResultats}commune(s) trouvée(s)</p>
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
		<p>
			<a href="#">Mentions légales</a> · <a href="#">Politique de
				cookies</a>
		</p>
	</footer>

	<!-- Scripts -->
	<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
	<script>
        window.contextPath       = '${pageContext.request.contextPath}';
        window.federationChoisie = '${federationChoisie}';
        window.regionChoisie     = '${regionChoisie}';
        window.codePostalChoisi  = '${codePostalChoisi}';
        window.communeChoisie    = '${communeChoisie}';
        window.rayonChoisi       = '${rayonChoisi}';
        window.modeRecherche     = '${modeRecherche}';
    </script>
	<script src="${pageContext.request.contextPath}/js/carte.js"></script>
</body>
</html>
