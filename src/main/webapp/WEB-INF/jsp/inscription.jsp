<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Club Sportif</title>

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
                    Inscription avec validation
                </div>

                <h1 class="auth-hero-title">
                    Rejoignez la <span class="gradient-text">communauté</span> sportive
                </h1>

                <p class="auth-hero-text">
                    Pour garantir la qualité de notre plateforme, chaque inscription est validée par un administrateur.
                </p>

                <div class="auth-features">
                    <div class="auth-feature">
                        <div class="feature-check">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <path d="M5 13l4 4L19 7"/>
                            </svg>
                        </div>
                        <span>Joignez un justificatif (PDF, JPG, PNG)</span>
                    </div>

                    <div class="auth-feature">
                        <div class="feature-check">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <path d="M5 13l4 4L19 7"/>
                            </svg>
                        </div>
                        <span>Validation sous 48h</span>
                    </div>

                    <div class="auth-feature">
                        <div class="feature-check">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                                <path d="M5 13l4 4L19 7"/>
                            </svg>
                        </div>
                        <span>Vie privée respectée</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="auth-right">
            <div class="auth-form-wrapper">
                <div class="auth-form-header">
                    <h2 class="auth-form-title">Créer un compte</h2>
                    <p class="auth-form-subtitle">Inscription soumise à validation</p>
                </div>

                <c:if test="${not empty erreur}">
                    <div class="auth-erreur">
                        <span>${erreur}</span>
                    </div>
                </c:if>

                <form action="inscription"
                      method="POST"
                      enctype="multipart/form-data"
                      class="auth-form-modern">

                    <div class="champs-row">
                        <div class="champ-modern">
                            <label>Prénom</label>
                            <div class="input-wrapper">
                                <input type="text"
                                       name="prenom"
                                       value="${prenom}"
                                       placeholder="Jean"
                                       required>
                            </div>
                        </div>

                        <div class="champ-modern">
                            <label>Nom</label>
                            <div class="input-wrapper">
                                <input type="text"
                                       name="nom"
                                       value="${nom}"
                                       placeholder="Dupont"
                                       required>
                            </div>
                        </div>
                    </div>

                    <div class="champ-modern">
                        <label>Identifiant</label>
                        <div class="input-wrapper">
                            <input type="text"
                                   name="login"
                                   value="${login}"
                                   placeholder="jdupont"
                                   required
                                   minlength="3">
                        </div>
                    </div>

                    <div class="champ-modern">
                        <label>Email</label>
                        <div class="input-wrapper">
                            <input type="email"
                                   name="email"
                                   value="${email}"
                                   placeholder="jean@example.com"
                                   required>
                        </div>
                    </div>

                    <div class="champ-modern">
                        <label>Mot de passe</label>
                        <div class="input-wrapper">
                            <input type="password"
                                   name="motDePasse"
                                   placeholder="6 caractères minimum"
                                   required
                                   minlength="6">
                        </div>
                    </div>

                    <div class="champ-modern">
                        <label>Type de compte</label>

                        <div class="role-selector">

                            <label class="role-card">
                                <c:choose>
                                    <c:when test="${role == 'CLUB' || empty role}">
                                        <input type="radio" name="role" value="CLUB" checked required>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="radio" name="role" value="CLUB" required>
                                    </c:otherwise>
                                </c:choose>

                                <div class="role-content">
                                    <div class="role-icon">🏆</div>
                                    <strong>Club</strong>
                                    <span>Responsable</span>
                                </div>
                            </label>

                            <label class="role-card">
                                <c:choose>
                                    <c:when test="${role == 'ELU'}">
                                        <input type="radio" name="role" value="ELU" checked>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="radio" name="role" value="ELU">
                                    </c:otherwise>
                                </c:choose>

                                <div class="role-content">
                                    <div class="role-icon">🏛️</div>
                                    <strong>Élu</strong>
                                    <span>Local</span>
                                </div>
                            </label>

                            <label class="role-card">
                                <c:choose>
                                    <c:when test="${role == 'ADMIN'}">
                                        <input type="radio" name="role" value="ADMIN" checked>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="radio" name="role" value="ADMIN">
                                    </c:otherwise>
                                </c:choose>

                                <div class="role-content">
                                    <div class="role-icon">⚙️</div>
                                    <strong>Admin</strong>
                                    <span>Plateforme</span>
                                </div>
                            </label>

                        </div>
                    </div>

                    <div class="champ-modern">
                        <label>Justificatif PDF, JPG, PNG - max 10MB</label>

                        <div class="file-upload">
                            <input type="file"
                                   name="pieceJointe"
                                   id="pieceJointe"
                                   accept=".pdf,.jpg,.jpeg,.png"
                                   required>

                            <label for="pieceJointe" class="file-upload-label">
                                <span id="file-name">Cliquez pour choisir un fichier</span>
                            </label>
                        </div>
                    </div>

                    <button type="submit" class="btn-auth-modern">
                        <span>Envoyer ma demande</span>
                    </button>
                </form>

                <div class="auth-form-footer">
                    Déjà inscrit ?
                    <a href="connexion">Se connecter</a>
                </div>
            </div>
        </div>

    </div>

    <script>
        var inputFichier = document.getElementById('pieceJointe');

        inputFichier.addEventListener('change', function(e) {
            var nom = 'Cliquez pour choisir un fichier';

            if (e.target.files[0]) {
                nom = e.target.files[0].name;
            }

            document.getElementById('file-name').textContent = nom;
        });
    </script>

</body>
</html>