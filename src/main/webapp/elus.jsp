<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<title>Dashboard Élus</title>

<style>
body {
    margin: 0;
    font-family: "Segoe UI", sans-serif;
    background: #f4f6f9;
    display: flex;
}

.sidebar {
    width: 250px;
    height: 100vh;
    background: linear-gradient(180deg, #1e3c72, #2a5298);
    color: white;
    padding: 20px;
}

.sidebar a {
    display: block;
    color: white;
    text-decoration: none;
    padding: 12px;
    border-radius: 10px;
    margin-bottom: 10px;
}

.main {
    flex: 1;
    padding: 25px;
}

.dashboard-container {
    display: flex;
    gap: 20px;
    align-items: flex-start;
    margin-top: 20px;
}

.filter-box {
    background: white;
    padding: 20px;
    border-radius: 10px;
    width: 320px;
    box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.filter-box form {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.filter-box select,
.filter-box input {
    padding: 8px;
    border-radius: 6px;
    border: 1px solid #ccc;
}

.filter-box button {
    margin-top: 10px;
    padding: 10px;
    border: none;
    border-radius: 8px;
    background-color: #2a5298;
    color: white;
    font-weight: bold;
    cursor: pointer;
}

.filter-box button:hover {
    background-color: #1e3c72;
}

.stats-container {
    flex: 1;
}

.cards {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
}

.card {
    background: white;
    padding: 20px;
    border-radius: 15px;
    text-align: center;
    box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.card p {
    font-size: 22px;
    font-weight: bold;
}

small {
    font-size: 12px;
}
.chart-box {
    margin-top: 20px;
    background: white;
    padding: 20px;
    border-radius: 15px;
    box-shadow: 0 4px 10px rgba(0,0,0,0.1);
    max-width: 500px;
}

</style>
</head>

<body>

<div class="sidebar">
    <h2>🏛 Élus</h2>
    <a href="ElusDashboard">Dashboard</a>
     <a href="LeClassement">🏆 Classement</a>
</div>

<div class="main">

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
<script src="js/chart.js"></script>

<script>
    drawChart(${hommes}, ${femmes});
</script>
</body>
</html>