new Chart(document.getElementById('ageChart'), {
    type: 'bar',
    data: {
        labels: ageLabels,
        datasets: [{
            label: 'Nombre de licenciés',
            data: ageValues
        }]
    }
});

new Chart(document.getElementById('clubsChart'), {
    type: 'bar',
    data: {
        labels: clubsLabels,
        datasets: [{
            label: 'Nombre de clubs',
            data: clubsValues
        }]
    },
    options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                display: true
            }
        },
        scales: {
            y: {
                ticks: {
                    font: {
                        size: 11
                    }
                }
            }
        }
    }
});