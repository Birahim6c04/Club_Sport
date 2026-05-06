/* ============================================================
   map.js — Gestion de la carte OpenStreetMap (Leaflet)
   ============================================================ */

let map;
let markersLayer;
let circleLayer = null;

// Icône personnalisée bleu
const clubIcon = L.divIcon({
  className: '',
  html: `<div style="
    width:26px;height:26px;
    background:#4f8ef7;border:2px solid #fff;
    border-radius:50% 50% 50% 0;transform:rotate(-45deg);
    box-shadow:0 2px 8px rgba(0,0,0,0.4)">
  </div>`,
  iconSize: [26, 26],
  iconAnchor: [13, 26],
  popupAnchor: [0, -28]
});

/**
 * Initialise la carte centrée sur la France.
 * Appelé au chargement de la page.
 */
function initMap() {
  map = L.map('map', { zoomControl: true }).setView([46.5, 2.5], 6);

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    maxZoom: 19
  }).addTo(map);

  markersLayer = L.layerGroup().addTo(map);
}

/**
 * Affiche les clubs sur la carte avec leurs marqueurs.
 * @param {Array} clubs - tableau d'objets club retourné par l'API
 * @param {number|null} lat  - latitude du centre du rayon
 * @param {number|null} lng  - longitude du centre du rayon
 * @param {number|null} rayon - rayon en km
 */
function afficherSurCarte(clubs, lat, lng, rayon) {
  // Nettoyer les anciens marqueurs et cercle
  markersLayer.clearLayers();
  if (circleLayer) { map.removeLayer(circleLayer); circleLayer = null; }

  if (!clubs || clubs.length === 0) return;

  // Cercle de rayon si coordonnées fournies
  if (lat && lng && rayon) {
    circleLayer = L.circle([parseFloat(lat), parseFloat(lng)], {
      radius: rayon * 1000,
      color: '#4f8ef7',
      fillColor: '#4f8ef7',
      fillOpacity: 0.05,
      weight: 1.5,
      dashArray: '6 4'
    }).addTo(map);
  }

  const bounds = [];

  clubs.forEach((club, index) => {
    if (!club.lat || !club.lng) return;

    const pctF = club.total > 0 ? Math.round(club.femmes / club.total * 100) : 0;

    const marker = L.marker([club.lat, club.lng], { icon: clubIcon })
      .addTo(markersLayer)
      .bindPopup(buildPopup(club, pctF));

    // Stocker l'index pour le lien carte ↔ liste
    marker._clubIndex = index;
    bounds.push([club.lat, club.lng]);
  });

  // Ajuster la vue pour afficher tous les marqueurs
  if (bounds.length > 0) {
    map.fitBounds(bounds, { padding: [50, 50], maxZoom: 13 });
  }
}

/**
 * Recentre la carte sur un club et ouvre son popup.
 */
function focusClub(lat, lng, index) {
  if (!lat || !lng) return;
  map.setView([lat, lng], 14, { animate: true });
  markersLayer.eachLayer(m => {
    if (m._clubIndex === index) m.openPopup();
  });
}

/**
 * Remet la carte à la vue France.
 */
function resetMap() {
  markersLayer.clearLayers();
  if (circleLayer) { map.removeLayer(circleLayer); circleLayer = null; }
  map.setView([46.5, 2.5], 6, { animate: true });
}

/** Construit le HTML du popup d'un marqueur. */
function buildPopup(club, pctF) {
  return `
    <div style="min-width:210px">
      <div class="popup-title">${esc(club.commune)}</div>
      <div class="popup-fed">${esc(club.federation)}</div>
      <div class="popup-row"><span>Total licenciés</span><span>${club.total}</span></div>
      <div class="popup-row">
        <span>Femmes</span>
        <span style="color:#e85d75">${club.femmes} (${pctF}%)</span>
      </div>
      <div class="popup-row">
        <span>Hommes</span>
        <span style="color:#4fc896">${club.hommes} (${100 - pctF}%)</span>
      </div>
      <div class="popup-bar">
        <div class="popup-bar-f" style="width:${pctF}%"></div>
      </div>
      <div style="display:flex;justify-content:space-between;font-size:10px;color:#8b8fa8;margin-top:3px">
        <span>Femmes</span><span>Hommes</span>
      </div>
      <div style="margin-top:10px;text-align:center">
        <a href="club.html?id=${esc(club.codeCommune || club.commune)}"
           style="font-size:12px;color:#4f8ef7;text-decoration:none;font-weight:600">
          Voir la page du club →
        </a>
      </div>
    </div>`;
}

function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}