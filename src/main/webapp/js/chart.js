// Graphique circulaire pour afficher la répartition par genre
function drawChart(hommes, femmes) {
    // Récupère le canvas HTML qui doit afficher le graphique
    const canvas = document.getElementById('genreChart');

    // Si le canvas n'existe pas sur la page, on arrête la fonction
    if (!canvas) {
        return;
    }

    // Création du graphique avec Chart.js
    new Chart(canvas, {
        // Type de graphique : doughnut = graphique en anneau
        type: 'doughnut',

        // Données du graphique
        data: {
            // Libellés affichés dans la légende
            labels: ['Hommes', 'Femmes'],

            // Valeurs du graphique
            datasets: [{
                data: [hommes, femmes]
            }]
        },

        // Options d'affichage du graphique
        options: {
            // Le graphique s'adapte à la taille de l'écran
            responsive: true,

            plugins: {
                // Position de la légende
                legend: {
                    position: 'bottom'
                },

                // Titre du graphique
                title: {
                    display: true,
                    text: 'Répartition des licenciés par genre'
                }
            }
        }
    });
}