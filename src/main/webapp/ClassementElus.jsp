<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<title>Classement des communes</title>

<style>
body {                                                   
    margin: 0;
    font-family: "Segoe UI", sans-serif;
    background: #eafcff;
    display: flex;
}

.sidebar {
    width: 250px;
    height: 100vh;
    background: #0ea5b7;
    color: white;
    padding: 20px;
}

.sidebar h2 {
    margin-bottom: 40px;
}

.sidebar a {
    display: block;
    color: white;
    text-decoration: none;
    padding: 12px;
    border-radius: 10px;
    margin-bottom: 15px;
}

.sidebar a:hover {
    background: rgba(255,255,255,0.18);
}

.main {
    flex: 1;
    padding: 30px;
}

h1 {
    margin-bottom: 25px;
    color: #0ea5b7;
}

.container {
    display: flex;
    gap: 25px;
}

.filter-box {
    background: white;
    padding: 20px;
    border-radius: 12px;
    width: 350px;
    box-shadow: 0 6px 24px rgba(14, 165, 183, 0.18);
}

.filter-box form {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

select {
    padding: 8px;
    border-radius: 6px;
    border: 2px solid #c8f3f8;
}

select:focus {
    outline: none;
    border-color: #0ea5b7;
}

button {
    margin-top: 10px;
    padding: 10px;
    border-radius: 8px;
    border: none;
    background: #0ea5b7;
    color: white;
    font-weight: bold;
    cursor: pointer;
}

button:hover {
    background: #0b8ea0;
}

.result-box {
    flex: 1;
}

table {
    width: 100%;
    border-collapse: collapse;
    background: white;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 6px 24px rgba(14, 165, 183, 0.18);
}

th {
    background: #0ea5b7;
    color: white;
    padding: 12px;
}

td {
    padding: 10px;
    text-align: center;
    border-bottom: 1px solid #eafcff;
}

tr:nth-child(even) {
    background: #eafcff;
}

.rank {
    font-weight: bold;
    color: #0ea5b7;
}

.progress-bar {
    background: #eafcff;
    border-radius: 10px;
    width: 100%;
    padding: 3px;
}

.chart-box {
    margin-top: 30px;
    background: white;
    padding: 20px;
    border-radius: 12px;
    height: 350px;
    box-shadow: 0 6px 24px rgba(14, 165, 183, 0.18);
}
</style>
</head>

<body>

<div class="sidebar">
    <h2>🏛 Élus</h2>
    <a href="ElusDashboard"> Dashboard</a>
    <a href="LeClassement"> Classement</a>
    <a href="indicateurs"> Statistiques</a>
    <a href="#"> Export</a>
</div>

<div class="main">

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

</body>
</html>