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

    // Si recherche par RAYON : on passe commune + rayon
    if (window.modeRecherche === 'rayon' && window.communeChoisie && window.rayonChoisi) {
        url += '&commune=' + window.communeChoisie + '&rayon=' + window.rayonChoisi;
    }
    // Sinon recherche par ZONE : on passe la région
    else if (window.regionChoisie) {
        url += '&region=' + encodeURIComponent(window.regionChoisie);
    }

    fetch(url)
        .then(res => res.json())
        .then(clubs => {
            // Supprime les anciens marqueurs et le cercle
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
                    marker.bindPopup(
                        '<b>' + club.nomCommune + '</b><br>' +
                        club.nomFederation + '<br>' +
                        club.total + ' club(s)'
                    );
                    marqueurs.push(marker);
                    points.push([club.latitude, club.longitude]);
                }
            });

            // Mode rayon : on dessine un cercle de référence et on centre dessus
            if (window.modeRecherche === 'rayon' && clubs.length > 0) {
                // Le 1er club est la commune de référence (la plus proche = elle-même)
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
            // Mode zone : on cadre sur tous les marqueurs
            else if (points.length > 0) {
                map.fitBounds(points, { padding: [40, 40], maxZoom: 12 });
            }
        })
        .catch(err => console.error('Erreur chargement marqueurs :', err));
}