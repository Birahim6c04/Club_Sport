<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:url var="profilUrl" value="/profil" />
<c:url var="profilCss" value="/css/profil.css" />

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Profil élu</title>
    <link rel="stylesheet" href="${profilCss}">
</head>

<body>

<div class="sidebar">
    <h2>🏛 Élus</h2>
    <a href="ElusDashboard">Dashboard</a>
    <a href="LeClassement">Classement</a>
    <a href="indicateurs">Statistiques</a>
    <a href="${profilUrl}">Mon profil</a>
</div>

<div class="main">

    <div class="top-navbar">
        <div class="nav-left">
            <a href="accueil">Accueil</a>
        </div>

        <div class="nav-right">
            <a href="deconnexion">Déconnexion</a>
        </div>
    </div>

    <h1>Mon profil</h1>

    <c:if test="${not empty erreur}">
        <p style="color:red;">
            <c:out value="${erreur}" />
        </p>
    </c:if>

    <div class="profile-card">

        <div class="profile-header">
            <div class="profile-user">

                <div class="profile-avatar">
                    <c:choose>
                        <c:when test="${not empty profil.photoProfil}">
                            <c:url var="photoUrl" value="/uploads/${profil.photoProfil}" />

                            <img class="avatar-img"
                                 src="${photoUrl}"
                                 alt="Photo de profil">
                        </c:when>

                        <c:otherwise>
                            👤
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="profile-info">
                    <h2>
                        <c:out value="${sessionScope.utilisateur.prenom}" />
                        <c:out value="${sessionScope.utilisateur.nom}" />
                    </h2>
                </div>
            </div>

            <button type="button" class="edit-btn" onclick="afficherFormulaire()">
                <c:choose>
                    <c:when test="${empty profil}">
                        Créer mon profil
                    </c:when>
                    <c:otherwise>
                        Modifier
                    </c:otherwise>
                </c:choose>
            </button>
        </div>

        <c:choose>
            <c:when test="${empty profil}">
                <div class="empty-profile">
                    <h2>Aucun profil créé</h2>
                    <p>Complétez vos informations pour créer votre profil élu.</p>
                </div>
            </c:when>

            <c:otherwise>
                <table class="profile-table">
                    <tbody>
                    <tr>
                        <th>Fonction</th>
                        <td><c:out value="${profil.fonction}" /></td>
                    </tr>

                    <tr>
                        <th>Email</th>
                        <td><c:out value="${sessionScope.utilisateur.email}" /></td>
                    </tr>

                    <tr>
                        <th>Téléphone</th>
                        <td><c:out value="${profil.telephone}" /></td>
                    </tr>

                    <tr>
                        <th>Adresse</th>
                        <td><c:out value="${profil.adresse}" /></td>
                    </tr>

                    <tr>
                        <th>Commune</th>
                        <td><c:out value="${profil.commune}" /></td>
                    </tr>

                    <tr>
                        <th>Département</th>
                        <td><c:out value="${profil.departement}" /></td>
                    </tr>

                    <tr>
                        <th>Région</th>
                        <td><c:out value="${profil.region}" /></td>
                    </tr>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <form id="profileForm"
              class="profile-form ${empty profil ? 'active' : ''}"
              method="post"
              action="${profilUrl}"
              enctype="multipart/form-data">

            <h2>
                <c:choose>
                    <c:when test="${empty profil}">
                        Créer mon profil
                    </c:when>
                    <c:otherwise>
                        Modifier mes informations
                    </c:otherwise>
                </c:choose>
            </h2>

            <label>Photo de profil :</label>
            <input type="file" name="photoProfil" accept="image/*">

            <label>Fonction :</label>
            <input type="text" name="fonction" value="<c:out value='${profil.fonction}' />">

            <label>Téléphone :</label>
            <input type="text" name="telephone" value="<c:out value='${profil.telephone}' />">

            <label>Adresse :</label>
            <input type="text" name="adresse" value="<c:out value='${profil.adresse}' />">

            <label>Commune :</label>
            <input type="text" name="commune" value="<c:out value='${profil.commune}' />">

            <label>Département :</label>
            <input type="text" name="departement" value="<c:out value='${profil.departement}' />">

            <label>Région :</label>
            <input type="text" name="region" value="<c:out value='${profil.region}' />">

            <button type="submit">
                <c:choose>
                    <c:when test="${empty profil}">
                        Créer le profil
                    </c:when>
                    <c:otherwise>
                        Enregistrer les modifications
                    </c:otherwise>
                </c:choose>
            </button>
        </form>

    </div>

</div>

<script>
function afficherFormulaire() {
    const formulaire = document.getElementById("profileForm");
    formulaire.classList.toggle("active");
}
</script>

</body>
</html>