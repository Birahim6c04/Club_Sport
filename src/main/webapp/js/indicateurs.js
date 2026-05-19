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
		datasets: [
		    {
		        label: 'Nombre de clubs',
		        data: clubsValues,
		        xAxisID: 'xClubs',
		        backgroundColor: '#3498db'
		    },
		    {
		        label: 'Nombre de licenciés',
		        data: licenciesValues,
		        xAxisID: 'xLicencies',
		        backgroundColor: '#f39c12'
		    }
		]
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
            xClubs: {
                type: 'linear',
                position: 'bottom'
            },

            xLicencies: {
                type: 'linear',
                position: 'top',
                grid: {
                    drawOnChartArea: false
                }
            },

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