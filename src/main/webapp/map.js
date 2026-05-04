/**
 * map.js — SportIF
 * Carte Leaflet : recherche par rayon + affichage des clubs depuis les données CSV
 */

// ==============================
// COORDONNÉES DES COMMUNES CONNUES (fallback sans backend)
// ==============================
const COMMUNE_COORDS = {
    'paris':       [48.8566, 2.3522],
    'marseille':   [43.2965, 5.3698],
    'lyon':        [45.7640, 4.8357],
    'toulouse':    [43.6047, 1.4442],
    'nice':        [43.7102, 7.2620],
    'nantes':      [47.2184, -1.5536],
    'bordeaux':    [44.8378, -0.5792],
    'strasbourg':  [48.5734, 7.7521],
    'montpellier': [43.6112, 3.8767],
    'lille':       [50.6292, 3.0573],
    'rennes':      [48.1173, -1.6778],
    'reims':       [49.2583, 4.0317],
    'grenoble':    [45.1885, 5.7245],
    'dijon':       [47.3220, 5.0415],
    'angers':      [47.4784, -0.5632],
    'nimes':       [43.8367, 4.3601],
    'clermont-ferrand': [45.7772, 3.0870],
    'le mans':     [48.0077, 0.1984],
    'aix-en-provence': [43.5297, 5.4474],
    'saint-etienne': [45.4397, 4.3872],
    'tours':       [47.3941, 0.6848],
    'limoges':     [45.8336, 1.2611],
    'amiens':      [49.8942, 2.2957],
    'metz':        [49.1193, 6.1757],
    'besancon':    [47.2380, 6.0243],
    'orleans':     [47.9029, 1.9039],
    'mulhouse':    [47.7508, 7.3359],
    'rouen':       [49.4432, 1.0993],
    'caen':        [49.1829, -0.3707],
    'nancy':       [48.6921, 6.1844],
    'perpignan':   [42.6887, 2.8948],
    'pau':         [43.2951, -0.3708],
    'brest':       [48.3905, -4.4860],
    'toulon':      [43.1242, 5.9280],
    'avignon':     [43.9493, 4.8055],
    'lorient':     [47.7485, -3.3674],
    'poitiers':    [46.5802, 0.3404],
    'dunkerque':   [51.0343, 2.3773],
};

// Coordonnées simulées par code département (fallback ultime)
const DEP_COORDS = {
    '75': [48.8566, 2.3522], '13': [43.2965, 5.3698], '69': [45.7640, 4.8357],
    '31': [43.6047, 1.4442], '06': [43.7102, 7.2620], '44': [47.2184, -1.5536],
    '33': [44.8378, -0.5792], '67': [48.5734, 7.7521], '34': [43.6112, 3.8767],
    '59': [50.6292, 3.0573], '35': [48.1173, -1.6778], '51': [49.2583, 4.0317],
    '38': [45.1885, 5.7245], '21': [47.3220, 5.0415], '49': [47.4784, -0.5632],
    '30': [43.8367, 4.3601], '63': [45.7772, 3.0870], '72': [48.0077, 0.1984],
    '57': [49.1193, 6.1757], '25': [47.2380, 6.0243], '45': [47.9029, 1.0993],
    '68': [47.7508, 7.3359], '76': [49.4432, 1.0993], '14': [49.1829, -0.3707],
    '54': [48.6921, 6.1844], '66': [42.6887, 2.8948], '64': [43.2951, -0.3708],
    '29': [48.3905, -4.4860], '83': [43.1242, 5.9280], '84': [43.9493, 4.8055],
    '56': [47.7485, -3.3674], '86': [46.5802, 0.3404], '59': [51.0343, 2.3773],
};

// ==============================
// INIT CARTE
// ==============================
const franceBounds = [[41.0, -5.5], [51.5, 10.0]];

const map = L.map('map', {
    center: [46.603354, 1.888334],
    zoom: 6,
    minZoom: 5,
    maxBounds: franceBounds,
    maxBoundsViscosity: 0.9
});

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

// ==============================
// GESTION DES LAYERS
// ==============================
let searchCircle = null;
let markersLayer = L.layerGroup().addTo(map);

function clearMap() {
    if (searchCircle) { map.removeLayer(searchCircle); searchCircle = null; }
    markersLayer.clearLayers();
}

// ==============================
// GEOCODAGE NOMINATIM (async)
// ==============================
async function geocodeCity(cityName) {
    // 1. Chercher dans notre dictionnaire local
    const key = cityName.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
    for (const [k, coords] of Object.entries(COMMUNE_COORDS)) {
        if (key.includes(k) || k.includes(key)) return coords;
    }

    // 2. Appel Nominatim
    try {
        const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(cityName + ', France')}&format=json&limit=1`;
        const resp = await fetch(url, { headers: { 'Accept-Language': 'fr' } });
        const data = await resp.json();
        if (data.length > 0) {
            return [parseFloat(data[0].lat), parseFloat(data[0].lon)];
        }
    } catch(e) {
        console.warn('Nominatim indisponible :', e);
    }

    // 3. Fallback : centre de France
    return [46.603354, 1.888334];
}

/**
 * Obtenir coords approximatives d'une commune depuis son code commune
 */
function getCoordsFromCode(codeCommune) {
    const dep = codeCommune ? codeCommune.substring(0, 2) : '';
    const coords = DEP_COORDS[dep];
    if (coords) {
        // Ajouter un léger décalage aléatoire pour éviter l'empilement
        return [
            coords[0] + (Math.random() - 0.5) * 0.8,
            coords[1] + (Math.random() - 0.5) * 0.8
        ];
    }
    return [
        46.603354 + (Math.random() - 0.5) * 8,
        1.888334 + (Math.random() - 0.5) * 8
    ];
}

// ==============================
// ICONES MARQUEURS
// ==============================
function makeMarkerIcon(color = '#2563eb') {
    return L.divIcon({
        className: '',
        html: `<svg width="24" height="32" viewBox="0 0 24 32" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 0C5.373 0 0 5.373 0 12c0 9 12 20 12 20S24 21 24 12C24 5.373 18.627 0 12 0z" fill="${color}"/>
            <circle cx="12" cy="12" r="5" fill="white"/>
        </svg>`,
        iconSize: [24, 32],
        iconAnchor: [12, 32],
        popupAnchor: [0, -32]
    });
}

const FED_COLORS = [
    '#2563eb','#e8281e','#16a34a','#9333ea','#ea580c',
    '#0891b2','#be185d','#65a30d','#d97706','#0f766e'
];

function getFedColor(fedCode) {
    const idx = parseInt(fedCode || '0') % FED_COLORS.length;
    return FED_COLORS[idx] || '#2563eb';
}

// ==============================
// AFFICHAGE DES MARQUEURS
// ==============================
window.showMarkersOnMap = function(rows) {
    markersLayer.clearLayers();

    // Grouper par commune pour éviter trop de marqueurs
    const byCommune = {};
    rows.forEach(row => {
        const key = row.code_commune || row.commune || row.libelle;
        if (!byCommune[key]) byCommune[key] = [];
        byCommune[key].push(row);
    });

    const bounds = [];

    Object.entries(byCommune).forEach(([key, entries]) => {
        const first = entries[0];
        const coords = getCoordsFromCode(first.code_commune);
        bounds.push(coords);

        // Préparer contenu popup
        const totalLic = entries.reduce((sum, r) => sum + parseInt(r.l_2019 || 0), 0);
        const totalF   = entries.reduce((sum, r) => sum + parseInt(r.l_f_2019 || 0), 0);
        const totalH   = entries.reduce((sum, r) => sum + parseInt(r.l_h_2019 || 0), 0);
        const fedColor = getFedColor(first.fed_2019 || first.code_federation);

        const fedsList = entries.slice(0, 5).map(r =>
            `<div style="font-size:0.78rem;color:#555;margin-top:2px;">• ${r.nom_fed || r.nom_federation || '—'}</div>`
        ).join('') + (entries.length > 5 ? `<div style="font-size:0.75rem;color:#999">+${entries.length - 5} autres…</div>` : '');

        const popupHtml = `
            <div class="popup-content">
                <div class="popup-fed">${entries.length} fédération${entries.length > 1 ? 's' : ''}</div>
                <div class="popup-commune">${first.libelle || first.commune || key}</div>
                <div class="popup-region">${first.region || ''}</div>
                ${fedsList}
                <div class="popup-stats">
                    👥 <strong>${totalLic.toLocaleString('fr-FR')}</strong> licenciés
                    &nbsp;·&nbsp; ♂ <strong>${totalH.toLocaleString('fr-FR')}</strong>
                    &nbsp;·&nbsp; ♀ <strong>${totalF.toLocaleString('fr-FR')}</strong>
                </div>
            </div>
        `;

        L.marker(coords, { icon: makeMarkerIcon(fedColor) })
            .addTo(markersLayer)
            .bindPopup(popupHtml, { maxWidth: 260 });
    });

    if (bounds.length > 0) {
        try {
            map.fitBounds(bounds, { padding: [40, 40], maxZoom: 10 });
        } catch(e) {}
    }
};

// ==============================
// RECHERCHE PAR RAYON
// ==============================
document.getElementById('search-btn')?.addEventListener('click', async () => {
    const location = document.getElementById('location-input').value.trim();
    const radius   = parseFloat(document.getElementById('radius-input').value);
    const fedCode  = document.getElementById('fed-select-radius').value;

    if (!location || !radius || radius <= 0) {
        alert('Veuillez saisir une ville et un rayon valide (en km).');
        return;
    }

    clearMap();

    // Afficher un indicateur de chargement
    document.getElementById('results-count').textContent = 'Recherche en cours…';

    const coords = await geocodeCity(location);

    // Cercle de recherche
    searchCircle = L.circle(coords, {
        color: '#f97316',
        fillColor: '#fed7aa',
        fillOpacity: 0.18,
        radius: radius * 1000,
        weight: 2,
        dashArray: '6 4'
    }).addTo(map);

    map.fitBounds(searchCircle.getBounds());

    // Filtrer les données par fédération si demandé
    const state = window.APP_STATE;
    if (state && state.licData.length) {
        let filtered = state.licData;
        if (fedCode) filtered = filtered.filter(r => (r.fed_2019 || r.code_federation) === fedCode);

        // Pour la démo sans backend, on affiche les résultats filtrés sur la zone
        // En production : on enverrait coords + radius au backend Java
        window.showMarkersOnMap(filtered.slice(0, 150));

        // Mettre à jour le panel résultats
        if (typeof displayResults === 'function') {
            displayResults(filtered.slice(0, 60));
        } else {
            document.getElementById('results-count').textContent =
                `${filtered.length} résultats (${radius} km autour de « ${location} »)`;
        }
    } else {
        document.getElementById('results-count').textContent =
            `Zone de ${radius} km autour de « ${location} » affichée. Connectez le backend Java pour les résultats réels.`;
    }

    console.log(`[map.js] → Backend Java : coords=[${coords}], rayon=${radius}km, fed=${fedCode || 'toutes'}`);
});

// ==============================
// CALLBACK : données CSV prêtes (appelé depuis app.js)
// ==============================
window.onDataReady = function(licData) {
    console.log('[map.js] Données reçues :', licData.length, 'lignes');
    // Optionnel : afficher quelques marqueurs exemples au démarrage
};