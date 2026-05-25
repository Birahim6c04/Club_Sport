<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<title>Dashboard Élus</title>
<link rel="stylesheet" href="css/elus.css">
</head>

<body>

<div class="sidebar">
    <h2>🏛 Élus</h2>
    <a href="ElusDashboard">Dashboard</a>
    <a href="LeClassement">Classement</a>
    <a href="indicateurs">Statistiques</a>
    <a href="profil">Mon profil</a>
</div>

<div class="main">

    <div class="top-navbar">
    <div class="nav-left">
        <a href="accueil">Accueil</a>
    </div>

    <div class="nav-right">
        <a href="#">Déconnexion</a>
    </div>
</div>


<h1>Tableau de bord des élus</h1>

<c:if test="${not empty erreur}">
    <p style="color:red;">${erreur}</p>
</c:if>

<div class="dashboard-container">

    <div class="filter-box">
        <form action="ElusDashboard" method="GET">

            <label>Région :</label>
            <select name="region" onchange="this.form.submit()">
                <option value="">Choisir une région</option>
                <c:forEach var="r" items="${regions}">
                    <option value="${r}" ${r == region ? 'selected' : ''}>
                        ${r}
                    </option>
                </c:forEach>
            </select>

            <c:if test="${empty region}">
                <small style="color: #777;">Choisissez d'abord une région.</small>
            </c:if>

            <label>Département :</label>
            <select name="departement" onchange="this.form.submit()" ${empty region ? 'disabled' : ''}>
                <option value="">Choisir un département</option>
                <c:forEach var="d" items="${departements}">
                    <option value="${d}" ${d == departement ? 'selected' : ''}>
                        ${d}
                    </option>
                </c:forEach>
            </select>

            <c:if test="${not empty region and empty departement}">
                <small style="color: #777;">Choisissez un département.</small>
            </c:if>

            <label>Commune :</label>
            <select name="codeCommune" onchange="this.form.submit()" ${empty departement ? 'disabled' : ''}>
                <option value="">Choisir une commune</option>

                <c:forEach var="c" items="${communes}">
                    <option value="${c[0]}" ${c[0] == codeCommune ? 'selected' : ''}>
                        ${c[1]} (${c[2]})
                    </option>
                </c:forEach>
            </select>

            <label>Fédération :</label>
            <select name="federation" ${empty codeCommune ? 'disabled' : ''}>
                <option value="">Toutes les fédérations</option>
                <c:forEach var="f" items="${federations}">
                    <option value="${f}" ${f == federation ? 'selected' : ''}>
                        ${f}
                    </option>
                </c:forEach>
            </select>

            <button type="submit">Appliquer</button>

            <a class="export-btn"
               href="export?type=dashboard&region=${region}&departement=${departement}&codeCommune=${codeCommune}&federation=${federation}"
               onclick="return verifierExport('${region}');">
                Exporter Excel
            </a>
            
            <a class="export-btn"
			   href="export-pdf?type=dashboard&region=${region}&departement=${departement}&codeCommune=${codeCommune}&federation=${federation}"
			   onclick="return verifierExport('${region}');">
			    Exporter PDF
			</a>

        </form>
    </div>

    <div class="stats-container">

        <div class="cards">
            <div class="card">
                <h3>Licenciés</h3>
                <p>${total}</p>
            </div>

            <div class="card">
                <h3>Hommes</h3>
                <p>${hommes}</p>
            </div>

            <div class="card">
                <h3>Femmes</h3>
                <p>${femmes}</p>
            </div>
        </div>

        <div class="chart-box">
            <canvas id="genreChart"></canvas>
        </div>

    </div>

</div>

</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="js/chart.js?v=2"></script>

<script>
    drawChart(${hommes}, ${femmes});
</script>

<script>
function verifierExport(region) {
    if (region === null || region.trim() === "") {
        alert("Veuillez choisir au moins une région avant d’exporter.");
        return false;
    }

    return true;
}
</script>

</body>
</html>