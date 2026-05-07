<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Club Sportif</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@600;700;800;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-page">

    <div class="auth-bg">
        <div class="auth-orb auth-orb-1"></div>
        <div class="auth-orb auth-orb-2"></div>
        <div class="auth-orb auth-orb-3"></div>
        <div class="auth-grid"></div>
    </div>

    <div class="auth-split">

        <!-- COTÉ GAUCHE : Branding -->
        <div class="auth-left">
            <a href="${pageContext.request.contextPath}/accueil" class="auth-brand">
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
                    Inscription gratuite
                </div>
                <h1 class="auth-hero-title">
                    Rejoignez la <span class="gradient-text">communauté</span> sportive française
                </h1>
                <p class="auth-hero-text">
                    Créez votre compte en quelques secondes et accédez à toutes les fonctionnalités de la plateforme.
                </p>

                <div class="auth-features">
                    <div class="auth-feature">
                        <div class="feature-check">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <path d="M5 13l4 4L19 7"/>
                            </svg>
                        </div>
                        <span>Inscription 100% gratuite</span>
                    </div>
                    <div class="auth-feature">
                        <div class="feature-check">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <path d="M5 13l4 4L19 7"/>
                            </svg>
                        </div>
                        <span>Espace personnel selon votre rôle</span>
                    </div>
                    <div class="auth-feature">
                        <div class="feature-check">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <path d="M5 13l4 4L19 7"/>
                            </svg>
                        </div>
                        <span>Sans engagement, vie privée respectée</span>
                    </div>
                </div>
            </div>

            <div class="auth-stats-mini">
                <div class="auth-stat-mini">
                    <strong>3</strong>
                    <span>Rôles disponibles</span>
                </div>
                <div class="auth-stat-mini">
                    <strong>10s</strong>
                    <span>Pour s'inscrire</span>
                </div>
                <div class="auth-stat-mini">
                    <strong>0€</strong>
                    <span>Toujours gratuit</span>
                </div>
            </div>
        </div>

        <!-- COTÉ DROIT : Formulaire -->
        <div class="auth-right">
            <div class="auth-form-wrapper">
                <div class="auth-form-header">
                    <h2 class="auth-form-title">Créer un compte</h2>
                    <p class="auth-form-subtitle">Rejoignez la plateforme</p>
                </div>

                <c:if test="${not empty erreur}">
                    <div class="auth-erreur">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10"/>
                            <line x1="12" y1="8" x2="12" y2="12"/>
                            <line x1="12" y1="16" x2="12.01" y2="16"/>
                        </svg>
                        <span>${erreur}</span>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/inscription" method="POST" class="auth-form-modern">

                    <div class="champs-row">
                        <div class="champ-modern">
                            <label>Prénom</label>
                            <div class="input-wrapper">
                                <input type="text" name="prenom" value="${prenom}" placeholder="Jean" required>
                            </div>
                        </div>
                        <div class="champ-modern">
                            <label>Nom</label>
                            <div class="input-wrapper">
                                <input type="text" name="nom" value="${nom}" placeholder="Dupont" required>
                            </div>
                        </div>
                    </div>

                    <div class="champ-modern">
                        <label>Identifiant</label>
                        <div class="input-wrapper">
                            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
                                <circle cx="12" cy="7" r="4"/>
                            </svg>
                            <input type="text" name="login" value="${login}" placeholder="jdupont" required minlength="3">
                        </div>
                    </div>

                    <div class="champ-modern">
                        <label>Email</label>
                        <div class="input-wrapper">
                            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                                <polyline points="22,6 12,13 2,6"/>
                            </svg>
                            <input type="email" name="email" value="${email}" placeholder="jean@example.com" required>
                        </div>
                    </div>

                    <div class="champ-modern">
                        <label>Mot de passe</label>
                        <div class="input-wrapper">
                            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="3" y="11" width="18" height="11" rx="2"/>
                                <path d="M7 11V7a5 5 0 0110 0v4"/>
                            </svg>
                            <input type="password" name="motDePasse" placeholder="6 caractères minimum" required minlength="6">
                        </div>
                    </div>

                    <div class="champ-modern">
                        <label>Type de compte</label>
                        <div class="role-selector">
                            <label class="role-card">
                                <input type="radio" name="role" value="CLUB" ${role == 'CLUB' || empty role ? 'checked' : ''} required>
                                <div class="role-content">
                                    <div class="role-icon">🏆</div>
                                    <strong>Club</strong>
                                    <span>Responsable</span>
                                </div>
                            </label>
                            <label class="role-card">
                                <input type="radio" name="role" value="ELU" ${role == 'ELU' ? 'checked' : ''}>
                                <div class="role-content">
                                    <div class="role-icon">🏛️</div>
                                    <strong>Élu</strong>
                                    <span>Local</span>
                                </div>
                            </label>
                            <label class="role-card">
                                <input type="radio" name="role" value="ADMIN" ${role == 'ADMIN' ? 'checked' : ''}>
                                <div class="role-content">
                                    <div class="role-icon">⚙️</div>
                                    <strong>Admin</strong>
                                    <span>Plateforme</span>
                                </div>
                            </label>
                        </div>
                    </div>

                    <button type="submit" class="btn-auth-modern">
                        <span>Créer mon compte</span>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                            <path d="M5 12h14M12 5l7 7-7 7"/>
                        </svg>
                    </button>
                </form>

                <div class="auth-form-footer">
                    Déjà inscrit ?
                    <a href="${pageContext.request.contextPath}/connexion">Se connecter</a>
                </div>
            </div>
        </div>

    </div>

</body>
</html>
