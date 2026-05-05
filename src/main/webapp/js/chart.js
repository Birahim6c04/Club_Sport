// Graphique circulaire (genre)
function drawChart(hommes, femmes) {
    const canvas = document.getElementById('genreChart');

    // Vérifie si le canvas existe (évite les erreurs sur les pages qui ne l'ont pas)
    if (!canvas) {
        return;
    }

    new Chart(canvas, {
        type: 'doughnut',
        data: {
            labels: ['Hommes', 'Femmes'],
            datasets: [{
                data: [hommes, femmes]
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom'
                },
                title: {
                    display: true,
                    text: 'Répartition des licenciés par genre'
                }
            }
        }
    });
}


// Graphique du classement des communes
/*
function drawClassementChart(labelsCommunes, dataPourcentages) {
    const canvas = document.getElementById('classementChart');

    if (!canvas) {
        return;
    }

    new Chart(canvas, {
        type: 'bar',
        data: {
            labels: labelsCommunes,
            datasets: [{
                label: 'Part (%)',
                data: dataPourcentages
            }]
        }
    });
}
*/