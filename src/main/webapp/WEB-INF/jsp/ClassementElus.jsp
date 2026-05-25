<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> 
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<title>Classement des communes</title>
<link rel="stylesheet" href="css/classement.css">

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

<h1>Classement des communes</h1>

<c:if test="${not empty erreur}">
    <p style="color:red;">${erreur}</p>
</c:if>

<div class="container">

<div class="filter-box">
<form method="get" action="LeClassement">

<label>Région :</label>
<select name="region" onchange="this.form.submit()">
    <option value="">Choisir une région</option>
    <c:forEach var="r" items="${regions}">
        <option value="${r}" ${r == region ? 'selected' : ''}>${r}</option>
    </c:forEach>
</select>

<label>Département :</label>
<select name="departement" onchange="this.form.submit()" ${empty region ? 'disabled' : ''}>
    <option value="">Choisir un département</option>
    <c:forEach var="d" items="${departements}">
        <option value="${d}" ${d == departement ? 'selected' : ''}>${d}</option>
    </c:forEach>
</select>

<label>Fédération :</label>
<select name="federation" ${empty departement ? 'disabled' : ''}>
    <option value="">Toutes les fédérations</option>
    <c:forEach var="f" items="${federations}">
        <option value="${f}" ${f == federation ? 'selected' : ''}>${f}</option>
    </c:forEach>
</select>

<button type="submit">Appliquer</button>

<a class="export-btn"
   href="export?type=classement&region=${region}&departement=${departement}&federation=${federation}"
   onclick="return verifierExportClassement('${region}');">
    Exporter Excel
</a>
<a class="export-btn"
   href="export-pdf?type=classement&region=${region}&departement=${departement}&federation=${federation}"
   onclick="return verifierExport('${region}');">
    Exporter PDF
</a>

</form>
</div>

<div class="result-box">

<table>
<thead>
<tr>
<th>Rang</th>
<th>Commune</th>
<th>Licenciés</th>
<th>Part</th>
</tr>
</thead>

<tbody>
<c:forEach var="commune" items="${classementCommunes}" varStatus="status">
<tr>
<td class="rank">${status.index + 1}</td>
<td>${commune.nomCommune} (${commune.codePostal})</td>
<td>${commune.totalLicencies}</td>
<td>${commune.pourcentage} %</td>
</tr>
</c:forEach>
</tbody>
</table>

<div class="chart-box">
    <canvas id="classementChart"></canvas>
</div>
</div>
</div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
    new Chart(document.getElementById('classementChart'), {
        type: 'bar',
        data: {
            labels: ${labelsCommunes},
            datasets: [{
                label: 'Part (%)',
                data: ${dataPourcentages},
                backgroundColor: '#0ea5b7'
            }]
        }
    });
</script>

<script>
function verifierExportClassement(region) {
    if (region === null || region.trim() === "") {
        alert("Veuillez choisir au moins une région avant d’exporter.");
        return false;
    }

    return true;
}
</script>

</body>
</html>