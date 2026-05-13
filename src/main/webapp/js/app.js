// =========================
// Configuration
// =========================
const API_BASE = '/api/clubs';

// Liste statique des fédérations principales (peut aussi être chargée depuis l'API)
const FEDERATIONS = [
    { code: '101', nom: "FF d'Athlétisme" },
    { code: '102', nom: "FF d'Aviron" },
    { code: '103', nom: 'FF de Badminton' },
    { code: '105', nom: 'FF de Basketball' },
    { code: '106', nom: 'FF de Boxe' },
    { code: '107', nom: 'FF de Canoë-Kayak' },
    { code: '108', nom: 'FF de Cyclisme' },
    { code: '109', nom: "FF d'Équitation" },
    { code: '110', nom: "FF d'Escrime" },
    { code: '111', nom: 'FF de Football' },
    { code: '113', nom: 'FF de Gymnastique' },
    { code: '115', nom: 'FF de Handball' },
    { code: '117', nom: 'FF de Judo' },
    { code: '119', nom: 'FF de Natation' },
    { code: '123', nom: 'FF de Tennis' },
    { code: '124', nom: 'FF de Tennis de Table' },
    { code: '127', nom: 'FF de Triathlon' },
    { code: '128', nom: 'FF de Voile' },
    { code: '129', nom: 'FF de Volley' },
    { code: '132', nom: 'FF de Golf' },
    { code: '133', nom: 'FF de Rugby' },
    { code: '135', nom: "FF de la Montagne et de l'Escalade" }
];

// =========================
// Variables globales
// =========================
let map;
let geojsonLayer;
let marqueurs = [];

// =========================
// Initialisation au chargement
// =========================
document.addEventListener('DOMContentLoaded', function() {
    initialiserCarte();
    chargerFederations();
    chargerRegions();
    initialiserOnglets();
    initialiserTypeZone();
    initialiserAutocompletion();
    initialiserBoutons();
});

// =========================
// CARTE
// =========================
function initialiserCarte() {
    map = L.map('map', { zoomSnap: 0.25 });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
        maxZoom: 19
    }).addTo(map);

    // Carte des départements en arrière-plan
    fetch('https://raw.githubusercontent.com/gregoiredavid/france-geojson/master/departements-version-simplifiee.geojson')
        .then(res => res.json())
        .then(data => {
            geojsonLayer = L.geoJSON(data, {
                style: {
                    color: '#0ea5b7',
                    weight: 1.5,
                    fillColor: '#c8f3f8',
                    fillOpacity: 0.35
                },
                onEachFeature: function(feature, layer) {
                    const nom  = feature.properties.nom;
                    const code = feature.properties.code;
                    layer.bindTooltip('<b>' + code + '</b> — ' + nom, { sticky: true, className: 'dept-tooltip' });
                    layer.on('mouseover', function() {
                        this.setStyle({ fillColor: '#0ea5b7', fillOpacity: 0.5, weight: 2.5 });
                    });
                    layer.on('mouseout', function() {
                        this.setStyle({ fillColor: '#c8f3f8', fillOpacity: 0.35, weight: 1.5 });
                    });
                }
            }).addTo(map);
            map.fitBounds(geojsonLayer.getBounds(), { padding: [20, 20] });
        });
}

// =========================
// CHARGEMENT DES DONNEES
// =========================
function chargerFederations() {
    const selects = [
        document.getElementById('federation-zone'),
        document.getElementById('federation-rayon')
    ];
    selects.forEach(select => {
        FEDERATIONS.forEach(fed => {
            const option = document.createElement('option');
            option.value = fed.code;
            option.textContent = fed.nom;
            select.appendChild(option);
        });
    });
}

function chargerRegions() {
    fetch(API_BASE + '/regions')
        .then(res => res.json())
        .then(regions => {
            const select = document.getElementById('region');
            regions.forEach(region => {
                const option = document.createElement('option');
                option.value = region;
                option.textContent = region;
                select.appendChild(option);
            });
        })
        .catch(err => console.error('Erreur chargement régions :', err));
}

// =========================
// ONGLETS
// =========================
function initialiserOnglets() {
    const onglets = document.querySelectorAll('.onglet');
    onglets.forEach(onglet => {
        onglet.addEventListener('click', function() {
            onglets.forEach(o => o.classList.remove('actif'));
            this.classList.add('actif');

            const mode = this.dataset.mode;
            document.getElementById('formulaire-zone').style.display  = (mode === 'zone')  ? 'flex' : 'none';
            document.getElementById('formulaire-rayon').style.display = (mode === 'rayon') ? 'flex' : 'none';
        });
    });
}

// =========================
// TYPE DE ZONE (région / code postal)
// =========================
function initialiserTypeZone() {
    const select = document.getElementById('type-zone');
    select.addEventListener('change', function() {
        document.getElementById('champ-region').style.display = (this.value === 'region') ? 'flex' : 'none';
        document.getElementById('champ-cp').style.display     = (this.value === 'codepostal') ? 'flex' : 'none';
    });
}

// =========================
// AUTOCOMPLETION COMMUNE
// =========================
function initialiserAutocompletion() {
    const input = document.getElementById('commune');
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
            fetch(API_BASE + '/communes?q=' + encodeURIComponent(q))
                .then(res => res.json())
                .then(communes => {
                    suggestions.innerHTML = '';
                    if (communes.length === 0) {
                        suggestions.classList.remove('actif');
                        return;
                    }
                    communes.forEach(c => {
                        // c = [code_commune, nom_commune, code_postal, departement]
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

    // Ferme les suggestions quand on clique ailleurs
    document.addEventListener('click', e => {
        if (!input.contains(e.target) && !suggestions.contains(e.target)) {
            suggestions.classList.remove('actif');
        }
    });
}

// =========================
// BOUTONS DE RECHERCHE
// =========================
function initialiserBoutons() {
    document.getElementById('btn-recherche-zone').addEventListener('click', rechercherParZone);
    document.getElementById('btn-recherche-rayon').addEventListener('click', rechercherParRayon);
}

function rechercherParZone() {
    const federation = document.getElementById('federation-zone').value;
    const typeZone   = document.getElementById('type-zone').value;
    const region     = document.getElementById('region').value;
    const cp         = document.getElementById('codepostal').value.trim();

    if (!federation) {
        alert('Veuillez choisir une fédération');
        return;
    }

    let url = API_BASE + '?federation=' + federation;
    if (typeZone === 'region' && region) {
        url += '&region=' + encodeURIComponent(region);
    } else if (typeZone === 'codepostal' && cp) {
        url += '&codePostal=' + cp;
    }

    lancerRecherche(url);
}

function rechercherParRayon() {
    const federation = document.getElementById('federation-rayon').value;
    const codeCommune = document.getElementById('commune-code').value;
    const rayon = document.getElementById('rayon').value;

    if (!federation) {
        alert('Veuillez choisir une fédération');
        return;
    }
    if (!codeCommune) {
        alert('Veuillez sélectionner une commune dans la liste');
        return;
    }

    const url = API_BASE + '?federation=' + federation + '&commune=' + codeCommune + '&rayon=' + rayon;
    lancerRecherche(url);
}

// =========================
// AFFICHAGE DES RESULTATS
// =========================
function lancerRecherche(url) {
    const section = document.getElementById('resultats-section');
    const liste   = document.getElementById('liste-resultats');

    section.style.display = 'block';
    info.textContent = 'Recherche en cours...';
    liste.innerHTML = '';

    fetch(url)
        .then(res => res.json())
        .then(clubs => {
            afficherResultats(clubs);
            afficherSurCarte(clubs);
        })
        .catch(err => {
            info.textContent = 'Erreur lors de la recherche';
            console.error(err);
        });
}

function afficherSurCarte(clubs) {
    // Supprime les anciens marqueurs
    marqueurs.forEach(m => map.removeLayer(m));
    marqueurs = [];

    if (!clubs || clubs.length === 0) return;

    const points = [];
    clubs.forEach(club => {
        if (club.latitude && club.longitude) {
            const marker = L.marker([club.latitude, club.longitude]).addTo(map);
            marker.bindPopup(`
                <div class="popup-club">
                    <div class="commune">${escapeHtml(club.nomCommune)}</div>
                    <div class="federation">${escapeHtml(club.nomFederation)}</div>
                    <div class="nombre">${club.total} club${club.total > 1 ? 's' : ''}</div>
                </div>
            `);
            marqueurs.push(marker);
            points.push([club.latitude, club.longitude]);
        }
    });

    // Recadre la carte sur les résultats
    if (points.length > 0) {
        map.fitBounds(points, { padding: [40, 40], maxZoom: 12 });
        // Scroll vers la carte
        document.getElementById('carte').scrollIntoView({ behavior: 'smooth' });
    }
}

// =========================
// UTILITAIRES
// =========================
function escapeHtml(text) {
    if (text == null) return '';
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}