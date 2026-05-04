// =========================
// Variables globales
// =========================
let map;
let marqueurs = [];
let cercleRayon = null;

// =========================
// Initialisation
// =========================
document.addEventListener('DOMContentLoaded', function() {
    initialiserCarte();
    initialiserOnglets();
    initialiserAutocompletion();

    // Si on vient d'une recherche, on affiche les marqueurs
    if (window.federationChoisie) {
        chargerMarqueurs();
    }
});

// =========================
// CARTE LEAFLET
// =========================
function initialiserCarte() {
    map = L.map('map', { zoomSnap: 0.25 });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap',
        maxZoom: 19
    }).addTo(map);

    fetch('https://raw.githubusercontent.com/gregoiredavid/france-geojson/master/departements-version-simplifiee.geojson')
        .then(res => res.json())
        .then(data => {
            const layer = L.geoJSON(data, {
                style: {
                    color: '#0ea5b7',
                    weight: 1.5,
                    fillColor: '#c8f3f8',
                    fillOpacity: 0.35
                },
                onEachFeature: function(feature, layer) {
                    layer.bindTooltip('<b>' + feature.properties.code + '</b> ' + feature.properties.nom);
                }
            }).addTo(map);
            map.fitBounds(layer.getBounds(), { padding: [20, 20] });
        });
}

// =========================
// ONGLETS DE RECHERCHE
// =========================
function initialiserOnglets() {
    const onglets = document.querySelectorAll('.onglet');
    onglets.forEach(onglet => {
        onglet.addEventListener('click', function() {
            onglets.forEach(o => o.classList.remove('actif'));
            this.classList.add('actif');

            const mode = this.dataset.mode;
            document.getElementById('form-zone').style.display  = (mode === 'zone')  ? 'flex' : 'none';
            document.getElementById('form-rayon').style.display = (mode === 'rayon') ? 'flex' : 'none';
        });
    });
}

// =========================
// AUTOCOMPLÉTION COMMUNE
// =========================
function initialiserAutocompletion() {
    const input = document.getElementById('commune-input');
    if (!input) return;

    const suggestions = document.getElementById('suggestions-commune');
    const codeHidden = document.getElementById('commune-code');

    let timeout;
    input.addEventListener('input', function() {
        clearTimeout(timeout);
        const q = this.value.trim();
        if (q.length < 2) {
            suggestions.classList.remove('actif');
            return;
        }

        timeout = setTimeout(() => {
            fetch(window.contextPath + '/carte/communes?q=' + encodeURIComponent(q))
                .then(res => res.json())
                .then(communes => {
                    suggestions.innerHTML = '';
                    if (communes.length === 0) {
                        suggestions.classList.remove('actif');
                        return;
                    }
                    communes.forEach(c => {
                        const item = document.createElement('div');
                        item.className = 'suggestion-item';
                        item.textContent = c[1] + (c[2] ? ' (' + c[2] + ')' : '');
                        item.addEventListener('click', () => {
                            input.value = c[1];
                            codeHidden.value = c[0];
                            suggestions.classList.remove('actif');
                        });
                        suggestions.appendChild(item);
                    });
                    suggestions.classList.add('actif');
                });
        }, 250);
    });

    document.addEventListener('click', e => {
        if (!input.contains(e.target) && !suggestions.contains(e.target)) {
            suggestions.classList.remove('actif');
        }
    });
}

// =========================
// CHARGER LES MARQUEURS (AJAX)
// =========================
function chargerMarqueurs() {
    let url = window.contextPath + '/carte/donnees?federation=' + window.federationChoisie;

    // Si recherche par RAYON
    if (window.modeRecherche === 'rayon' && window.communeChoisie && window.rayonChoisi) {
        url += '&commune=' + window.communeChoisie + '&rayon=' + window.rayonChoisi;
    }
    // Sinon recherche par ZONE
    else {
        if (window.regionChoisie) {
            url += '&region=' + encodeURIComponent(window.regionChoisie);
        }
        if (window.codePostalChoisi) {
            url += '&codePostal=' + window.codePostalChoisi;
        }
    }

    fetch(url)
        .then(res => res.json())
        .then(clubs => {
            // ... le reste de la fonction reste pareil
            marqueurs.forEach(m => map.removeLayer(m));
            marqueurs = [];
            if (cercleRayon) {
                map.removeLayer(cercleRayon);
                cercleRayon = null;
            }

            const points = [];
            clubs.forEach(club => {
                if (club.latitude && club.longitude) {
                    const marker = L.marker([club.latitude, club.longitude]).addTo(map);

                    let contenu = '<div class="popup-club">';
                    contenu += '<div class="popup-titre">' + club.nomCommune + '</div>';
                    contenu += '<div class="popup-region">' + club.departement + ' - ' + club.region + '</div>';
                    contenu += '<div class="popup-federation">' + club.nomFederation + '</div>';
                    contenu += '<div class="popup-stats">';
                    contenu += '<div><span class="nombre">' + club.clubs + '</span><span class="label">Clubs</span></div>';
                    contenu += '<div><span class="nombre">' + club.epa + '</span><span class="label">EPA</span></div>';
                    contenu += '<div><span class="nombre">' + club.total + '</span><span class="label">Total</span></div>';
                    contenu += '</div>';
                    if (club.distanceKm != null) {
                        contenu += '<div class="popup-distance">📍 ' + club.distanceKm.toFixed(1) + ' km</div>';
                    }
                    contenu += '</div>';

                    marker.bindPopup(contenu);
                    marqueurs.push(marker);
                    points.push([club.latitude, club.longitude]);
                }
            });

            // Si rayon : cercle + zoom
            if (window.modeRecherche === 'rayon' && clubs.length > 0) {
                const centre = [clubs[0].latitude, clubs[0].longitude];
                const rayonMetres = parseInt(window.rayonChoisi) * 1000;
                cercleRayon = L.circle(centre, {
                    radius: rayonMetres,
                    color: '#0ea5b7',
                    fillColor: '#0ea5b7',
                    fillOpacity: 0.1,
                    weight: 2
                }).addTo(map);
                map.fitBounds(cercleRayon.getBounds(), { padding: [20, 20] });
            }
            else if (points.length > 0) {
                map.fitBounds(points, { padding: [40, 40], maxZoom: 12 });
            }
        });
}