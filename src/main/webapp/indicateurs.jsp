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
    <a href="ElusDashboard"> Dashboard</a>
    <a href="LeClassement"> Classement</a>
    <a href="indicateurs"> Statistiques</a>
    <a href="#"> Export</a>
</div>

<div class="main">

    <h1>Indicateurs statistiques</h1>

    <div class="charts-container">

        <div class="chart-box">
            <h2>Répartition par âge des licenciés</h2>
            <canvas id="ageChart"></canvas>
        </div>

        <div class="chart-box chart-large">
            <h2>Nombre de clubs par fédération</h2>
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
    <c:forEach var="stat" items="${clubsFederations}" varStatus="status">
        "${stat.label}"<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];

const clubsValues = [
    <c:forEach var="stat" items="${clubsFederations}" varStatus="status">
        ${stat.valeur}<c:if test="${!status.last}">,</c:if>
    </c:forEach>
];
</script>

<script src="js/indicateurs.js"></script>

</body>
</html>