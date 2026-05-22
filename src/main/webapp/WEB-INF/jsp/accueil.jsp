<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>

<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Club Sportif - Trouvez votre club idéal</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link
	href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet"
	href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
</head>
<body>

	<!-- ARRIÈRE-PLAN ANIMÉ -->
	<div class="bg-blobs">
		<div class="blob blob-1"></div>
		<div class="blob blob-2"></div>
		<div class="blob blob-3"></div>
	</div>

	<!-- NAVBAR -->
	<nav class="navbar">
		<div class="nav-content">
			<div class="nav-logo">
				<div class="logo-icon">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
						stroke-width="2.5">
                        <circle cx="12" cy="12" r="10" />
                        <path
							d="M12 2 L12 22 M2 12 L22 12 M4.93 4.93 L19.07 19.07 M19.07 4.93 L4.93 19.07" />
                    </svg>
				</div>
				<span>Club<strong>Sportif</strong></span>
			</div>
			<div class="nav-links">
				<a href="#recherche">Rechercher</a> <a href="#carte">Carte</a> <a
					href="#federations">Sports</a> <a href="#apropos">À propos</a>
			</div>
			<div class="nav-actions">
				<a href="connexion" class="btn-ghost">Connexion</a> <a
					href="inscription" class="btn-primary">S'inscrire</a>
			</div>
		</div>
	</nav>

	<!-- HERO -->
	<section class="hero">
		<div class="hero-content">
			<div class="hero-badge fade-in">
				<span class="dot"></span> Plateforme officielle - Données Ministère
				des Sports
			</div>
			<h1 class="hero-title fade-in">
				Trouvez le club sportif <span class="gradient-text">parfait
					pour vous</span>
			</h1>
			<p class="hero-subtitle fade-in">Plus de 110 000 clubs affiliés,
				120 fédérations, partout en France. Découvrez en quelques clics les
				structures sportives près de chez vous.</p>
			<div class="hero-cta fade-in">
				<a href="#recherche" class="btn-hero-primary"> Commencer ma
					recherche <svg viewBox="0 0 24 24" fill="none"
						stroke="currentColor" stroke-width="2.5">
                        <path d="M5 12h14M12 5l7 7-7 7" />
                    </svg>
				</a> <a href="#federations" class="btn-hero-ghost"> Voir les
					disciplines </a>
			</div>

			<div class="hero-stats fade-in">
				<div class="stat-card glass">
					<div class="stat-icon">🏆</div>
					<div class="stat-value" data-target="110000">0</div>
					<div class="stat-label">Clubs affiliés</div>
				</div>
				<div class="stat-card glass">
					<div class="stat-icon">🎯</div>
					<div class="stat-value" data-target="120">0</div>
					<div class="stat-label">Fédérations</div>
				</div>
				<div class="stat-card glass">
					<div class="stat-icon">📍</div>
					<div class="stat-value" data-target="22000">0</div>
					<div class="stat-label">Communes</div>
				</div>
				<div class="stat-card glass">
					<div class="stat-icon">⚡</div>
					<div class="stat-value" data-target="14">0</div>
					<div class="stat-label">Millions licenciés</div>
				</div>
			</div>
		</div>
		<div class="hero-scroll-hint">
			<span>Faites défiler</span>
			<div class="scroll-line"></div>
		</div>
	</section>

	<!-- COMMENT ÇA MARCHE -->
	<section class="section-howto reveal">
		<div class="section-header">
			<div class="section-tag">Simple & rapide</div>
			<h2>
				Trouvez votre club en <span class="gradient-text">3 étapes</span>
			</h2>
			<p>Une recherche intuitive conçue pour vous accompagner dans
				votre choix d'activité sportive</p>
		</div>

		<div class="steps-grid">
			<div class="step-card reveal">
				<div class="step-number">01</div>
				<div class="step-icon-wrap">
					<svg class="step-icon" viewBox="0 0 24 24" fill="none"
						stroke="currentColor" stroke-width="2">
                        <path
							d="M21 21l-4.3-4.3M19 11a8 8 0 11-16 0 8 8 0 0116 0z" />
                    </svg>
				</div>
				<h3>Choisissez un sport</h3>
				<p>Plus de 100 disciplines à explorer : du football au judo, en
					passant par les sports nautiques et la danse.</p>
				<div class="step-arrow">→</div>
			</div>

			<div class="step-card reveal">
				<div class="step-number">02</div>
				<div class="step-icon-wrap">
					<svg class="step-icon" viewBox="0 0 24 24" fill="none"
						stroke="currentColor" stroke-width="2">
                        <path
							d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z" />
                        <circle cx="12" cy="10" r="3" />
                    </svg>
				</div>
				<h3>Définissez votre zone</h3>
				<p>Recherchez par région, code postal, ou directement dans un
					rayon précis autour de votre commune.</p>
				<div class="step-arrow">→</div>
			</div>

			<div class="step-card reveal">
				<div class="step-number">03</div>
				<div class="step-icon-wrap">
					<svg class="step-icon" viewBox="0 0 24 24" fill="none"
						stroke="currentColor" stroke-width="2">
                        <path
							d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
				</div>
				<h3>Découvrez les clubs</h3>
				<p>Visualisez les résultats sur une carte interactive et obtenez
					toutes les infos d'un simple clic.</p>
			</div>
		</div>
	</section>

	<!-- MESSAGE D'ERREUR -->
	<c:if test="${not empty erreur}">
		<div class="message-erreur">⚠️ ${erreur}</div>
	</c:if>

	<!-- RECHERCHE -->
	<section class="recherche-section" id="recherche">
		<div class="recherche-header">
			<div class="section-tag">Recherche avancée</div>
			<h2>
				Démarrez votre <span class="gradient-text">recherche</span>
			</h2>
		</div>

		<div class="onglets">
			<button class="onglet ${modeRecherche != 'rayon' ? 'actif' : ''}"
				data-mode="zone" type="button">
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
					stroke-width="2">
                    <path
						d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z" />
                    <circle cx="12" cy="10" r="3" />
                </svg>
				Par zone géographique
			</button>
			<button class="onglet ${modeRecherche == 'rayon' ? 'actif' : ''}"
				data-mode="rayon" type="button">
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
					stroke-width="2">
                    <circle cx="12" cy="12" r="10" />
                    <path d="M12 2v20M2 12h20" />
                </svg>
				Par rayon
			</button>
		</div>

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

			<button type="submit" class="btn-rechercher">
				Lancer la recherche
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
					stroke-width="2.5">
                    <path d="M5 12h14M12 5l7 7-7 7" />
                </svg>
			</button>
		</form>

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

			<button type="submit" class="btn-rechercher">
				Lancer la recherche
				<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
					stroke-width="2.5">
                    <path d="M5 12h14M12 5l7 7-7 7" />
                </svg>
			</button>
		</form>
	</section>

	<!-- RÉSULTATS -->
	<c:if test="${not empty clubs}">
		<div class="resultats-section">
			<h3>📋 Résultats de la recherche</h3>
			<p class="info-resultats">${nbResultats}commune(s) trouvée(s)</p>
		</div>
	</c:if>

	<!-- CARTE -->
	<section class="map-section" id="carte">
		<div class="section-header">
			<div class="section-tag">Vue géographique</div>
			<h2>
				Explorez la <span class="gradient-text">carte interactive</span>
			</h2>
		</div>
		<div id="map"></div>
	</section>

	<!-- FÉDÉRATIONS POPULAIRES -->
	<section class="federations-section reveal" id="federations">
		<div class="section-header">
			<div class="section-tag">Disciplines populaires</div>
			<h2>
				Les sports les plus <span class="gradient-text">pratiqués</span>
			</h2>
			<p>Découvrez les fédérations qui rassemblent le plus de licenciés
				en France</p>
		</div>

		<div class="federations-grille">
			<a href="#recherche" class="federation-card glass">
				<div class="federation-icone">⚽</div>
				<div class="federation-nom">Football</div>
				<div class="federation-info">2,1M licenciés</div>
				<div class="federation-glow"></div>
			</a> <a href="#recherche" class="federation-card glass">
				<div class="federation-icone">🎾</div>
				<div class="federation-nom">Tennis</div>
				<div class="federation-info">1,1M licenciés</div>
				<div class="federation-glow"></div>
			</a> <a href="#recherche" class="federation-card glass">
				<div class="federation-icone">🏀</div>
				<div class="federation-nom">Basketball</div>
				<div class="federation-info">650K licenciés</div>
				<div class="federation-glow"></div>
			</a> <a href="#recherche" class="federation-card glass">
				<div class="federation-icone">🥋</div>
				<div class="federation-nom">Judo</div>
				<div class="federation-info">560K licenciés</div>
				<div class="federation-glow"></div>
			</a> <a href="#recherche" class="federation-card glass">
				<div class="federation-icone">🤾</div>
				<div class="federation-nom">Handball</div>
				<div class="federation-info">510K licenciés</div>
				<div class="federation-glow"></div>
			</a> <a href="#recherche" class="federation-card glass">
				<div class="federation-icone">🏉</div>
				<div class="federation-nom">Rugby</div>
				<div class="federation-info">320K licenciés</div>
				<div class="federation-glow"></div>
			</a> <a href="#recherche" class="federation-card glass">
				<div class="federation-icone">🏊</div>
				<div class="federation-nom">Natation</div>
				<div class="federation-info">300K licenciés</div>
				<div class="federation-glow"></div>
			</a> <a href="#recherche" class="federation-card glass">
				<div class="federation-icone">🏐</div>
				<div class="federation-nom">Volleyball</div>
				<div class="federation-info">140K licenciés</div>
				<div class="federation-glow"></div>
			</a>
		</div>
	</section>

	<!-- AVANTAGES -->
	<section class="avantages-section reveal">
		<div class="section-header">
			<div class="section-tag">Pourquoi nous</div>
			<h2>
				Une expérience <span class="gradient-text">premium</span>
			</h2>
			<p>Conçue pour vous offrir le meilleur outil de recherche de
				clubs sportifs</p>
		</div>

		<div class="avantages-grille">
			<div class="avantage glass">
				<div class="avantage-icone-wrap">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
						stroke-width="2">
                        <path
							d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
				</div>
				<h4>Données certifiées</h4>
				<p>Informations officielles fournies directement par le
					Ministère des Sports.</p>
			</div>
			<div class="avantage glass">
				<div class="avantage-icone-wrap">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
						stroke-width="2">
                        <path
							d="M12 22s-8-4.5-8-11.8A8 8 0 0112 2a8 8 0 018 8.2c0 7.3-8 11.8-8 11.8z" />
                        <circle cx="12" cy="10" r="3" />
                    </svg>
				</div>
				<h4>Géolocalisation précise</h4>
				<p>Recherche par rayon avec calcul de distance en temps réel
					grâce à la géolocalisation.</p>
			</div>
			<div class="avantage glass">
				<div class="avantage-icone-wrap">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
						stroke-width="2">
                        <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" />
                    </svg>
				</div>
				<h4>Ultra rapide</h4>
				<p>Recherche instantanée parmi 110 000 clubs grâce à une
					infrastructure optimisée.</p>
			</div>
			<div class="avantage glass">
				<div class="avantage-icone-wrap">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
						stroke-width="2">
                        <rect x="3" y="11" width="18" height="11" rx="2" />
                        <path d="M7 11V7a5 5 0 0110 0v4" />
                    </svg>
				</div>
				<h4>Sans inscription</h4>
				<p>Service entièrement gratuit, accessible sans création de
					compte ni données personnelles.</p>
			</div>
		</div>
	</section>

	<!-- À PROPOS -->
	<section class="apropos-section reveal" id="apropos">
		<div class="apropos-grid">
			<div class="apropos-text">
				<div class="section-tag">Le projet</div>
				<h2>
					Une plateforme <span class="gradient-text">étudiante</span>
					ambitieuse
				</h2>
				<p>
					Cette plateforme est née d'un projet étudiant à l'<strong>ESIGELEC</strong>,
					école d'ingénieurs en systèmes d'information.
				</p>
				<p>Notre objectif : démocratiser l'accès aux données sportives
					officielles et faciliter la mise en relation entre les pratiquants
					et les clubs affiliés.</p>

				<div class="apropos-tags">
					<span class="tag-mini">data.gouv.fr</span> <span class="tag-mini">OpenStreetMap</span>
					<span class="tag-mini">Java EE</span> <span class="tag-mini">MySQL</span>
					<span class="tag-mini">Docker</span>
				</div>
			</div>
			<div class="apropos-cards">
				<div class="apropos-card glass">
					<div class="card-icon">📊</div>
					<strong>Données 2019</strong> <span>Statistiques officielles
						complètes du Ministère</span>
				</div>
				<div class="apropos-card glass">
					<div class="card-icon">🗺️</div>
					<strong>Cartographie HD</strong> <span>Visualisation
						interactive haute précision</span>
				</div>
				<div class="apropos-card glass">
					<div class="card-icon">🔒</div>
					<strong>Vie privée</strong> <span>Aucune collecte de données
						personnelles</span>
				</div>
				<div class="apropos-card glass">
					<div class="card-icon">⚡</div>
					<strong>Performance</strong> <span>Architecture optimisée
						temps réel</span>
				</div>
			</div>
		</div>
	</section>

	<!-- CALL TO ACTION -->
	<section class="cta-section reveal">
		<div class="cta-bg">
			<div class="cta-blob cta-blob-1"></div>
			<div class="cta-blob cta-blob-2"></div>
		</div>
		<div class="cta-content">
			<h2>
				Vous gérez un <span class="gradient-text-light">club sportif</span>
				?
			</h2>
			<p>Rejoignez la plateforme et créez votre espace dédié pour
				communiquer avec vos licenciés et gagner en visibilité.</p>
			<div class="cta-buttons">
				<a href="#" class="btn-cta-primary"> Créer mon espace club <svg
						viewBox="0 0 24 24" fill="none" stroke="currentColor"
						stroke-width="2.5">
                        <path d="M5 12h14M12 5l7 7-7 7" />
                    </svg>
				</a> <a href="#" class="btn-cta-ghost">En savoir plus</a>
			</div>
		</div>
	</section>

	<!-- FOOTER -->
	<footer>
		<div class="footer-colonnes">
			<div class="footer-colonne footer-brand">
				<div class="nav-logo">
					<div class="logo-icon">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
							stroke-width="2.5">
                            <circle cx="12" cy="12" r="10" />
                            <path
								d="M12 2 L12 22 M2 12 L22 12 M4.93 4.93 L19.07 19.07 M19.07 4.93 L4.93 19.07" />
                        </svg>
					</div>
					<span>Club<strong>Sportif</strong></span>
				</div>
				<p>La plateforme de référence pour découvrir les clubs sportifs
					partout en France.</p>
			</div>
			<div class="footer-colonne">
				<h4>Navigation</h4>
				<ul>
					<li><a href="#recherche">Rechercher</a></li>
					<li><a href="#carte">Carte interactive</a></li>
					<li><a href="#federations">Disciplines</a></li>
					<li><a href="#apropos">À propos</a></li>
				</ul>
			</div>
			<div class="footer-colonne">
				<h4>Espace pro</h4>
				<ul>
					<li><a href="#">Inscription club</a></li>
					<li><a href="#">Connexion</a></li>
					<li><a href="#">Espace élu</a></li>
				</ul>
			</div>
			<div class="footer-colonne">
				<h4>Légal</h4>
				<ul>
					<li><a href="${pageContext.request.contextPath}/mentions">Mentions
							légales</a></li>
					<li><a href="#">Confidentialité</a></li>
					<li><a href="${pageContext.request.contextPath}/cookies">Cookies</a></li>
					<li><a href="#">Contact</a></li>

				</ul>
			</div>
		</div>
		<div class="footer-bas">
			<p>© 2026 ClubSportif - Données : Ministère des Sports - Projet
				ESIGELEC</p>
		</div>
	</footer>

	<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
	<script>
        window.contextPath       = '${pageContext.request.contextPath}';
        window.federationChoisie = '${federationChoisie}';
        window.regionChoisie     = '${regionChoisie}';
        window.codePostalChoisi  = '${codePostalChoisi}';
        window.communeChoisie    = '${communeChoisie}';
        window.rayonChoisi       = '${rayonChoisi}';
        window.modeRecherche     = '${modeRecherche}';

        // ========== ANIMATIONS AU SCROLL ==========
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                }
            });
        }, { threshold: 0.1 });

        document.querySelectorAll('.reveal, .fade-in').forEach(el => observer.observe(el));

        // ========== COMPTEURS ANIMÉS ==========
        const animateCounter = (el) => {
            const target = parseInt(el.dataset.target);
            const duration = 2000;
            const start = performance.now();
            const update = (now) => {
                const progress = Math.min((now - start) / duration, 1);
                const eased = 1 - Math.pow(1 - progress, 3);
                const current = Math.floor(eased * target);
                el.textContent = current >= 1000 ? current.toLocaleString('fr-FR') : current;
                if (progress < 1) requestAnimationFrame(update);
                else el.textContent = target >= 1000 ? target.toLocaleString('fr-FR') + '+' : target + '+';
            };
            requestAnimationFrame(update);
        };

        const counterObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting && !entry.target.dataset.animated) {
                    entry.target.dataset.animated = 'true';
                    animateCounter(entry.target);
                }
            });
        }, { threshold: 0.5 });

        document.querySelectorAll('.stat-value').forEach(el => counterObserver.observe(el));
        
     // Scroll automatique vers les résultats après recherche
        if (window.modeRecherche) {
            setTimeout(() => {
                const cible = document.getElementById('recherche');
                if (cible) {
                    cible.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            }, 100);
        }
    </script>
	<script src="${pageContext.request.contextPath}/js/carte.js"></script>
	<jsp:include page="/WEB-INF/jsp/cookies_banner.jsp" />
</body>
</html>
