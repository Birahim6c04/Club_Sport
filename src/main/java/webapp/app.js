/* ============================================================
   app.js — Dashboard grand public (tâche 2)
   Gère la recherche, l'affichage de la liste et la connexion API
   ============================================================ */

// ---- URLs de l'API backend ----
const API = {
  search:      '/clubs/api/search',
  federations: '/clubs/api/federations',
  regions:     '/clubs/api/regions'
};

// ---- Données mémorisées ----
let clubsData = [];

// ============================================================
// DÉMARRAGE
// ============================================================
document.addEventListener('DOMContentLoaded', async () => {
  initMap();            // map.js
  await chargerFiltres();
});

// ============================================================
// CHARGEMENT DES LISTES DÉROULANTES
// ============================================================
async function chargerFiltres() {
  try {
    const [rFeds, rRegs] = await Promise.all([
      fetch(API.federations),
      fetch(API.regions)
    ]);
    const feds    = await rFeds.json();
    const regions = await rRegs.json();

    remplirSelect('sel-fed', feds.map(f => ({ value: f.code, label: f.nom })));
    remplirSelect('sel-region', regions.map(r => ({ value: r, label: r })));

  } catch (e) {
    console.warn('API non disponible — mode démo');
    chargerFiltresDemo();
  }
}

function chargerFiltresDemo() {
  remplirSelect('sel-fed', [
    { value: '111', label: 'Fédération française de football' },
    { value: '115', label: 'Fédération française de tennis' },
    { value: '119', label: 'Fédération française de basketball' },
    { value: '123', label: 'Fédération française de natation' }
  ]);
  remplirSelect('sel-region', [
    { value: 'Île-de-France',          label: 'Île-de-France' },
    { value: 'Bretagne',               label: 'Bretagne' },
    { value: 'Auvergne-Rhône-Alpes',   label: 'Auvergne-Rhône-Alpes' },
    { value: 'Occitanie',              label: 'Occitanie' },
    { value: 'Nouvelle-Aquitaine',     label: 'Nouvelle-Aquitaine' }
  ]);
}

function remplirSelect(id, items) {
  const sel = document.getElementById(id);
  items.forEach(({ value, label }) => {
    const o = document.createElement('option');
    o.value = value;
    o.textContent = label;
    sel.appendChild(o);
  });
}

// ============================================================
// RECHERCHE
// ============================================================
async function rechercherClubs() {
  const federation = document.getElementById('sel-fed').value;
  const region     = document.getElementById('sel-region').value;
  const commune    = document.getElementById('inp-commune').value.trim();
  const lat        = document.getElementById('inp-lat').value.trim();
  const lng        = document.getElementById('inp-lng').value.trim();
  const rayon      = document.getElementById('inp-rayon').value.trim();

  // Validation basique
  if (lat || lng || rayon) {
    if (!lat || !lng || !rayon) {
      alert('Pour la recherche par rayon, remplis les 3 champs : latitude, longitude et rayon.');
      return;
    }
  }

  showLoader(true);
  viderResultats();

  try {
    const params = new URLSearchParams({ federation, region, commune, lat, lng, rayon });
    const resp   = await fetch(`${API.search}?${params}`);
    if (!resp.ok) throw new Error('Erreur serveur ' + resp.status);
    clubsData = await resp.json();
  } catch (e) {
    console.warn('API non disponible — données démo');
    clubsData = donneesDemoClubs();
  } finally {
    showLoader(false);
  }

  afficherResultats(lat, lng, rayon);
}

// ============================================================
// AFFICHAGE DES RÉSULTATS
// ============================================================
function afficherResultats(lat, lng, rayon) {
  const countEl = document.getElementById('results-count');
  const listEl  = document.getElementById('results-list');

  // Mettre à jour la carte (map.js)
  afficherSurCarte(clubsData, lat, lng, rayon);

  if (!clubsData || clubsData.length === 0) {
    listEl.innerHTML = `
      <div class="empty-state">
        <span class="icon">🔍</span>
        <p>Aucun club trouvé.<br>Essaie d'élargir les filtres.</p>
      </div>`;
    countEl.classList.remove('visible');
    return;
  }

  countEl.textContent = `${clubsData.length} club${clubsData.length > 1 ? 's' : ''} trouvé${clubsData.length > 1 ? 's' : ''}`;
  countEl.classList.add('visible');

  listEl.innerHTML = '';
  clubsData.forEach((club, index) => {
    const card = document.createElement('div');
    card.className = 'club-card';

    card.innerHTML = `
      <div class="club-name">${esc(club.commune)}</div>
      <div class="club-meta">${esc(club.federation)} &bull; ${esc(club.region)}</div>
      <div class="club-stats">
        <span class="stat-badge">Total : ${club.total}</span>
        <span class="stat-badge f">F : ${club.femmes}</span>
        <span class="stat-badge h">H : ${club.hommes}</span>
      </div>`;

    card.addEventListener('click', () => {
      document.querySelectorAll('.club-card').forEach(c => c.classList.remove('active'));
      card.classList.add('active');
      focusClub(club.lat, club.lng, index); // map.js
    });

    listEl.appendChild(card);
  });
}

// ============================================================
// RESET
// ============================================================
function resetRecherche() {
  document.getElementById('sel-fed').value      = '';
  document.getElementById('sel-region').value   = '';
  document.getElementById('inp-commune').value  = '';
  document.getElementById('inp-lat').value      = '';
  document.getElementById('inp-lng').value      = '';
  document.getElementById('inp-rayon').value    = '';
  clubsData = [];
  viderResultats();
  resetMap(); // map.js
}

function viderResultats() {
  document.getElementById('results-list').innerHTML = `
    <div class="empty-state">
      <span class="icon">🏟️</span>
      <p>Utilise les filtres pour<br>trouver des clubs.</p>
    </div>`;
  document.getElementById('results-count').classList.remove('visible');
}

// ============================================================
// UTILITAIRES
// ============================================================
function showLoader(visible) {
  document.getElementById('loader').classList.toggle('visible', visible);
}

function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}

// Données de démo (si backend absent)
function donneesDemoClubs() {
  return [
    { commune:'Paris 15e',  federation:'Football',   region:'Île-de-France',        total:320, femmes:98,  hommes:222, lat:48.842, lng:2.296 },
    { commune:'Lyon 3e',    federation:'Tennis',     region:'Auvergne-Rhône-Alpes', total:145, femmes:72,  hommes:73,  lat:45.749, lng:4.855 },
    { commune:'Rennes',     federation:'Basketball', region:'Bretagne',             total:210, femmes:105, hommes:105, lat:48.117, lng:-1.677 },
    { commune:'Bordeaux',   federation:'Natation',   region:'Nouvelle-Aquitaine',   total:89,  femmes:54,  hommes:35,  lat:44.837, lng:-0.579 },
    { commune:'Marseille',  federation:'Athlétisme', region:'PACA',                 total:178, femmes:86,  hommes:92,  lat:43.292, lng:5.374 }
  ];
}