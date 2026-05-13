<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - Club Sportif</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@600;700;800;900&display=swap">

    <link rel="stylesheet" href="css/style.css">
</head>

<body class="auth-page">

    <div class="auth-bg">
        <div class="auth-orb auth-orb-1"></div>
        <div class="auth-orb auth-orb-2"></div>
        <div class="auth-orb auth-orb-3"></div>
        <div class="auth-grid"></div>
    </div>

    <div class="auth-split">

        <div class="auth-left">
            <a href="accueil" class="auth-brand">
                <div class="logo-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <circle cx="12" cy="12" r="10"/>
                        <path d="M12 2 L12 22 M2 12 L22 12 M4.93 4.93 L19.07 19.07 M19.07 4.93 L4.93 19.07"/>
                    </svg>
                </div>
                <span>Club<strong>Sportif</strong></span>
            </a>

            <div class="auth-hero">
                <div class="auth-hero-badge">
                    <span class="dot-live"></span>
                    Plateforme officielle
                </div>

                <h1 class="auth-hero-title">
                    Bienvenue dans <span class="gradient-text">l'avenir</span> du sport en France
                </h1>

                <p class="auth-hero-text">
                    Connectez-vous pour accéder à votre espace personnalisé et gérer votre activité sportive.
                </p>

                <div class="auth-features">
                    <div class="auth-feature">
                        <div class="feature-check">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <path d="M5 13l4 4L19 7"/>
                            </svg>
                        </div>
                        <span>Plus de 110 000 clubs référencés</span>
                    </div>

                    <div class="auth-feature">
                        <div class="feature-check">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <path d="M5 13l4 4L19 7"/>
                            </svg>
                        </div>
                        <span>Données officielles certifiées</span>
                    </div>

                    <div class="auth-feature">
                        <div class="feature-check">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <path d="M5 13l4 4L19 7"/>
                            </svg>
                        </div>
                        <span>Sécurité et confidentialité garanties</span>
                    </div>
                </div>
            </div>

            <div class="auth-stats-mini">
                <div class="auth-stat-mini">
                    <strong>110K+</strong>
                    <span>Clubs</span>
                </div>

                <div class="auth-stat-mini">
                    <strong>120</strong>
                    <span>Fédérations</span>
                </div>

                <div class="auth-stat-mini">
                    <strong>14M+</strong>
                    <span>Licenciés</span>
                </div>
            </div>
        </div>

        <div class="auth-right">
            <div class="auth-form-wrapper">
                <div class="auth-form-header">
                    <h2 class="auth-form-title">Connexion</h2>
                    <p class="auth-form-subtitle">Accédez à votre espace personnel</p>
                </div>

                <c:if test="${not empty erreur}">
                    <div class="auth-erreur">
                        <span>${erreur}</span>
                    </div>
                </c:if>

                <c:if test="${not empty succes}">
                    <div class="auth-succes">
                        <span>${succes}</span>
                    </div>
                </c:if>

                <form action="connexion" method="POST" class="auth-form-modern">
                    <div class="champ-modern">
                        <label>Identifiant</label>

                        <div class="input-wrapper">
                            <input type="text"
                                   name="login"
                                   value="${login}"
                                   placeholder="Votre login"
                                   required
                                   autofocus>
                        </div>
                    </div>

                    <div class="champ-modern">
                        <label>Mot de passe</label>

                        <div class="input-wrapper">
                            <input type="password"
                                   name="motDePasse"
                                   placeholder="Mot de passe"
                                   required>
                        </div>
                    </div>

                    <button type="submit" class="btn-auth-modern">
                        <span>Se connecter</span>
                    </button>
                </form>

                <div class="auth-divider">
                    <span>ou</span>
                </div>

                <div class="auth-form-footer">
                    Pas encore de compte ?
                    <a href="inscription">Créer un compte</a>
                </div>
            </div>
        </div>

    </div>

</body>
</html>