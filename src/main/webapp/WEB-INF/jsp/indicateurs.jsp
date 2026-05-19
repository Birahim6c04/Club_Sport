<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Indicateurs statistiques</title>
    <link rel="stylesheet" href="css/indicateurs.css">

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>

<body>

<div class="sidebar">
    <h2>🏛 Élus</h2>
    <a href="ElusDashboard">Dashboard</a>
    <a href="LeClassement">Classement</a>
    <a href="indicateurs">Statistiques</a>
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

    <h1>Indicateurs statistiques</h1>

    <c:if test="${not empty erreur}">
        <p style="color:red;">${erreur}</p>
    </c:if>

    <div class="top-container">

        <div class="filter-box">
            <form action="indicateurs" method="GET">

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
                    <small style="color:#777;">
                        Choisissez d'abord une région.
                    </small>
                </c:if>

                <label>Département :</label>
                <select name="departement"
                        onchange="this.form.submit()"
                        ${empty region ? 'disabled' : ''}>

                    <option value="">Choisir un département</option>

                    <c:forEach var="d" items="${departements}">
                        <option value="${d}" ${d == departement ? 'selected' : ''}>
                            ${d}
                        </option>
                    </c:forEach>
                </select>

                <c:if test="${not empty region and empty departement}">
                    <small style="color:#777;">
                        Choisissez un département.
                    </small>
                </c:if>

                <label>Commune :</label>
                <select name="codeCommune"
                        onchange="this.form.submit()"
                        ${empty departement ? 'disabled' : ''}>

                    <option value="">Choisir une commune</option>

                    <c:forEach var="c" items="${communes}">
                        <option value="${c[0]}" ${c[0] == codeCommune ? 'selected' : ''}>
                            ${c[1]} (${c[2]})
                        </option>
                    </c:forEach>
                </select>

                <label>Fédération :</label>
                <select name="federation"
                        ${empty codeCommune ? 'disabled' : ''}>

                    <option value="">Toutes les fédérations</option>

                    <c:forEach var="f" items="${federations}">
                        <option value="${f}" ${f == federation ? 'selected' : ''}>
                            ${f}
                        </option>
                    </c:forEach>
                </select>

                <button type="submit">Appliquer</button>
            </form>

            <a class="export-btn"
               href="export?type=indicateurs&region=${region}&departement=${departement}&codeCommune=${codeCommune}&federation=${federation}"
               onclick="return verifierExportIndicateurs('${region}');">
                Exporter Excel
            </a>
            <a class="export-btn"
			   href="export-pdf?type=indicateurs&region=${region}&departement=${departement}&codeCommune=${codeCommune}&federation=${federation}"
			   onclick="return verifierExport('${region}');">
			    Exporter PDF
			</a>
        </div>

        <div class="chart-box ratio-box">
            <h2>Licenciés par club selon l’âge</h2>
            <canvas id="rapportAgeClubsChart"></canvas>
        </div>

    </div>

    <div class="charts-container">

        <div class="chart-box">
            <h2>Répartition par âge des licenciés</h2>
            <canvas id="ageChart"></canvas>
        </div>

        <div class="chart-box chart-large">
            <h2>Nombre de clubs et de licenciés par fédération</h2>
            <canvas id="clubsChart"></canvas>
        </div>

    </div>

</div>

<script>
const ageLabels = [
    <c:forEach var="stat" items="${repartitionAge}" varStatus="status">
        "${stat.label}"<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];

const ageValues = [
    <c:forEach var="stat" items="${repartitionAge}" varStatus="status">
        ${stat.valeur}<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];

const clubsLabels = [
    <c:forEach var="item" items="${clubsLicenciesFederations}" varStatus="status">
        "${item.federation}"<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];

const clubsValues = [
    <c:forEach var="item" items="${clubsLicenciesFederations}" varStatus="status">
        ${item.clubs}<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];

const licenciesValues = [
    <c:forEach var="item" items="${clubsLicenciesFederations}" varStatus="status">
        ${item.licencies}<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];

const rapportAgeClubsLabels = [
    <c:forEach var="stat" items="${rapportAgeClubs}" varStatus="status">
        "${stat.label}"<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];

const rapportAgeClubsValues = [
    <c:forEach var="stat" items="${rapportAgeClubs}" varStatus="status">
        ${stat.valeur}<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];

new Chart(document.getElementById('rapportAgeClubsChart'), {
    type: 'line',
    data: {
        labels: rapportAgeClubsLabels,
        datasets: [{
            label: 'Licenciés par club',
            data: rapportAgeClubsValues,
            tension: 0.3,
            fill: false
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false
    }
});
</script>

<script src="js/indicateurs.js?v=2"></script>

<script>
function verifierExportIndicateurs(region) {
    if (region === null || region.trim() === "") {
        alert("Veuillez choisir au moins une région avant d’exporter.");
        return false;
    }

    return true;
}
</script>

</body>
</html>