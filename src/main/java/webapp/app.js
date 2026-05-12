/* ============================================================
   app.js — Dashboard grand public (tâche 2)
   Connecté exclusivement à la vraie base de données MySQL.
   Chemin : src/main/webapp/app.js
   ============================================================ */

const API = {
  search:      '/clubs/api/search',
  federations: '/clubs/api/federations',
  regions:     '/clubs/api/regions'
};

let clubsData = [];

// ── DÉMARRAGE ────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {
  initMap();
  await chargerFiltres();
});

// ── CHARGEMENT DES FILTRES DEPUIS LA VRAIE BASE ──────────────
async function chargerFiltres() {
  try {
    const [rFeds, rRegs] = await Promise.all([
      fetch(API.federations),
      fetch(API.regions)
    ]);

    if (!rFeds.ok || !rRegs.ok) throw new Error('Erreur serveur');

    const feds    = await rFeds.json();
    const regions = await rRegs.json();

    remplirSelect('sel-fed',    feds.map(f => ({ value: f.code, label: f.nom })));
    remplirSelect('sel-region', regions.map(r => ({ value: r, label: r })));

  } catch (e) {
    // Afficher une erreur visible — pas de données fictives
    afficherErreurConnexion(
      'Impossible de charger les fédérations et régions depuis la base de données. ' +
      'Vérifiez que Tomcat est lancé et que MySQL est accessible.'
    );
  }
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

// ── RECHERCHE ────────────────────────────────────────────────
async function rechercherClubs() {
  const federation = document.getElementById('sel-fed').value;
  const region     = document.getElementById('sel-region').value;
  const commune    = document.getElementById('inp-commune').value.trim();
  const lat        = document.getElementById('inp-lat').value.trim();
  const lng        = document.getElementById('inp-lng').value.trim();
  const rayon      = document.getElementById('inp-rayon').value.trim();

  if ((lat || lng || rayon) && (!lat || !lng || !rayon)) {
    alert('Pour la recherche par rayon, remplis les 3 champs : latitude, longitude et rayon.');
    return;
  }

  showLoader(true);
  viderResultats();

  try {
    const params = new URLSearchParams({ federation, region, commune, lat, lng, rayon });
    const resp   = await fetch(`${API.search}?${params}`);

    if (!resp.ok) throw new Error('Erreur serveur ' + resp.status);

    clubsData = await resp.json();

  } catch (e) {
    showLoader(false);
    afficherErreurConnexion(
      'La recherche a échoué. Vérifiez que Tomcat est lancé et que la base de données est accessible.'
    );
    return;
  }

  showLoader(false);
  afficherResultats(lat, lng, rayon);
}

// ── AFFICHAGE DES RÉSULTATS ──────────────────────────────────
function afficherResultats(lat, lng, rayon) {
  const countEl = document.getElementById('results-count');
  const listEl  = document.getElementById('results-list');

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

  countEl.textContent =
    `${clubsData.length} club${clubsData.length > 1 ? 's' : ''} trouvé${clubsData.length > 1 ? 's' : ''}`;
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
      focusClub(club.lat, club.lng, index);
    });
    listEl.appendChild(card);
  });
}

// ── RESET ────────────────────────────────────────────────────
function resetRecherche() {
  document.getElementById('sel-fed').value     = '';
  document.getElementById('sel-region').value  = '';
  document.getElementById('inp-commune').value = '';
  document.getElementById('inp-lat').value     = '';
  document.getElementById('inp-lng').value     = '';
  document.getElementById('inp-rayon').value   = '';
  clubsData = [];
  viderResultats();
  resetMap();
}

function viderResultats() {
  document.getElementById('results-list').innerHTML = `
    <div class="empty-state">
      <span class="icon">🏟️</span>
      <p>Utilise les filtres pour<br>trouver des clubs.</p>
    </div>`;
  document.getElementById('results-count').classList.remove('visible');
}

// ── AFFICHAGE D'UNE ERREUR RÉELLE ────────────────────────────
function afficherErreurConnexion(message) {
  document.getElementById('results-list').innerHTML = `
    <div class="empty-state">
      <span class="icon">⚠️</span>
      <p style="color:#e85d75">${message}</p>
    </div>`;
  document.getElementById('results-count').classList.remove('visible');
}

// ── UTILITAIRES ──────────────────────────────────────────────
function showLoader(visible) {
  document.getElementById('loader').classList.toggle('visible', visible);
}

function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}